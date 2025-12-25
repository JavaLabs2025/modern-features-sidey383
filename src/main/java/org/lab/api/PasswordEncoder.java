package org.lab.api;

public class PasswordEncoder {
    private final int bcryptRounds;

    PasswordEncoder() {
        this.bcryptRounds = 10;
    }

    public PasswordEncoder(int bcryptRounds) {
        this.bcryptRounds = bcryptRounds;
    }

    public String encode(String rawPassword) {
        return rawPassword;
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return rawPassword.equals(encodedPassword);
    }
}
