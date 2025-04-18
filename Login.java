package org.example.demo6;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Base64;

public class Login {
    String username;
    String password;
    String salt;


    public static void addUser(String username, String password, String salt) {
        String sql = "INSERT INTO LoginTable(Username,Password,Salt) VALUES(?,?,?)";

        try (Connection conn = LoginBase.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, salt);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
    public static String saltSprinkle(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }


    public static String hashPassword(String password,String salt){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltyPassword = password + salt;
            byte[] hashedBytes = md.digest(saltyPassword.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes){
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException("Hashing error encountered", e);
        }
    }

}
