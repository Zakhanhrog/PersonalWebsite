package com.bachnt.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    public static void main(String[] args) {
        String plainPasswordToHash = "admin";
        String hashedPassword = BCrypt.hashpw(plainPasswordToHash, BCrypt.gensalt(12));
        System.out.println("Plain Password: " + plainPasswordToHash);
        System.out.println("BCrypt Hashed Password: " + hashedPassword);
    }
}