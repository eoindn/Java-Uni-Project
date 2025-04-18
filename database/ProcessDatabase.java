package org.example.demo6.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ProcessDatabase {
    public static Connection connect(){
        Connection ProcessConn = null;
        try {
            ProcessConn = DriverManager.getConnection("jdbc:sqlite:ProcessBase.db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ProcessConn;
    }
}
