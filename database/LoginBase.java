package org.example.demo6.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginBase {

    public static Connection connection() {
        Connection loginConn = null;
        try {
            loginConn = DriverManager.getConnection("jdbc:sqlite:LoginBase.db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } return loginConn;
    }
}
