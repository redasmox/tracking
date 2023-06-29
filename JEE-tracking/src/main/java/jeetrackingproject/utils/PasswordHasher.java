package jeetrackingproject.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    private final String SALT="$2a$10$ApplicationTracking2023";
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, SALT);
    }

    public boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
