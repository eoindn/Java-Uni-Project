package org.example.demo6;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    public static Connection connect() {
        Connection conn = null;
        try {
            // Explicitly load the driver
            Class.forName("org.sqlite.JDBC");

            // Establish connection
            String dbPath = "jdbc:sqlite:inventory1.db";
            System.out.println("Attempting to connect to: " + dbPath);

            conn = DriverManager.getConnection(dbPath);

            if (conn == null) {
                System.err.println("Connection failed: Connection is null");
            } else {
                System.out.println("✅ Database connection successful!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
        return conn;
    }
}