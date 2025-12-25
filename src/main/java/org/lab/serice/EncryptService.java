package org.lab.serice;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptService {

    private static final int LOG_ROUNDS = 12;

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        String salt = BCrypt.gensalt(LOG_ROUNDS);
        return BCrypt.hashpw(plainPassword, salt);
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}

