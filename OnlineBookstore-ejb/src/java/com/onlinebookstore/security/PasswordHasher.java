/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.onlinebookstore.security;

import org.mindrot.jbcrypt.BCrypt;
/**
 *
 * @author ngnph
 */
public class PasswordHasher {
    private PasswordHasher() {
        
    }
    
    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    
    public static boolean verify(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) return false;
        
        return BCrypt.checkpw(password, hashedPassword);
    }
}
