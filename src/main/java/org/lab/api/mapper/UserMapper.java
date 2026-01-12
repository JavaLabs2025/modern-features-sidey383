package org.lab.api.mapper;

import org.lab.api.dto.authentification.AuthorizationAnswer;
import org.lab.api.dto.UserAnswer;
import org.lab.data.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    @Mapping(target = "name", source = "username")
    @Mapping(target = "id", source = "userId")
    UserAnswer mapToAnswer(User user);

    AuthorizationAnswer mapToAnswer(User user, String accessToken, String refreshToken);

}
