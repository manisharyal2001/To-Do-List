package org.example.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String raw, String hashed) {
        try {
            return BCrypt.checkpw(raw, hashed);
        } catch (Exception e) {
            return false;
        }
    }
}