package org.lab.serice;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.Authorization;
import org.lab.api.error.InvalidRequestException;
import org.lab.configuration.JwtProperty;
import org.lab.configuration.TokenProperty;
import org.lab.data.entity.UserType;

import java.time.Clock;
import java.util.Base64;
import java.util.Map;

@RequiredArgsConstructor
public class JwtService {

    private static final String USERNAME_CLAIM = "username";

    private static final String USER_TYPE_CLAIM = "userType";

    private final JwtProperty jwtProperty;

    private final TokenProperty tokenProperty;

    private final Clock clock;

    public String createToken(Authorization authorization) {
        return createToken(
                authorization.userId(),
                Map.of(
                        USERNAME_CLAIM, authorization.username(),
                        USER_TYPE_CLAIM, authorization.userType().name()
                )
        );
    }

    public Authorization getAuthorization(String token) {

        var decoded = decodeToken(token);
        long userId = Long.parseLong(decoded.getSubject());
        String username = decoded.getClaim(USERNAME_CLAIM).asString();
        UserType userType = UserType.valueOf(decoded.getClaim(USER_TYPE_CLAIM).asString());
        return new Authorization(userId, username, userType);
    }

    public String createToken(long userId, Map<String, Object> claims) {
        var builder = JWT.create();
        builder.withPayload(claims);
        builder.withSubject(Long.toString(userId));
        builder.withIssuedAt(clock.instant());
        builder.withExpiresAt(clock.instant().plus(tokenProperty.getAccessTokenLifeTime()));
        return builder.sign(getAlgorithm());
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(Base64.getDecoder().decode(jwtProperty.secret()));
    }

    private DecodedJWT decodeToken(String token) {
        try {
            var verifier = JWT.require(getAlgorithm())
                    .build();
            return verifier.verify(token);
        } catch (Exception e) {
            throw new InvalidRequestException("Fail to extract token claims", e);
        }
    }

    private void validateToken(String token) {
        try {
            var verifier = JWT.require(getAlgorithm()).build();
            verifier.verify(token);
        } catch (Exception e) {
            throw new InvalidRequestException("Token validation failed", e);
        }
    }

}
