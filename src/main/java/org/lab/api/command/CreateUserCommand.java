package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.data.entity.User;
import org.lab.data.entity.UserType;
import org.lab.data.repository.UserRepository;
import org.lab.serice.EncryptService;

@RequiredArgsConstructor
public final class CreateUserCommand implements AuthentificationCommand<User> {

    private final String username;
    private final String password;
    private final UserType userType;

    @Override
    public boolean requireAuthorization() {
        return false;
    }

    public User execute(UserRepository userRepository) {
        String passwordHash = EncryptService.hashPassword(password);
        long userId = userRepository
                .createUser(new User(0, username, passwordHash, userType, true));
        return userRepository.findById(userId).orElseThrow(() ->
                new IllegalStateException("Can't found created user")
        );
    }

}
