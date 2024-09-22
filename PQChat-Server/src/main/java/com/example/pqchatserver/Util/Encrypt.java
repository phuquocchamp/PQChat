package com.example.pqchatserver.Util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Encrypt {
    public static String encodePassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = String.format("%02x", b);
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String hashPassword ="dadb69701d0694b49564cce8c626002d108f17288cf591918f93d4a84f94da78";
        String password = "hoangtanphuquoc@gmail.com";

        if(encodePassword(password).equals(hashPassword)){
            System.out.println("Thành Công");
        }
    }

}
