package org.example.demo6;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerifyUser extends Login {
    String username;
    String password;



    public VerifyUser(){
        this.username = null;
        this.password = null;

    }



    public static boolean verifyUser(String username, String password){
        String sql = "SELECT * FROM LoginTable WHERE Username = ?";

        try (Connection con = LoginBase.connection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1,username);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                String  storedPassword = rs.getString("Password");
                String storedSalt = rs.getString("Salt");

                String hashedPassword = hashPassword(password,storedSalt);

                return storedPassword.equals(hashedPassword);
            }else {
                System.out.println("User not found");
                return false;
            }

            }catch (SQLException e){
            System.out.println(e.getMessage());
            return false;
        }


    }

}
