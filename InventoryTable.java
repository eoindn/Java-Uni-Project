package org.example.demo6;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InventoryTable {

    public static void createTable(){
        String sql = "CREATE TABLE IF NOT EXISTS ProductsTable (\n"
                + "	ProductID integer PRIMARY KEY,\n"
                + "	ProductName text NOT NULL,\n"
                + "	Quantity integer NOT NULL,\n"
                + "	Price real NOT NULL,\n"
                + "	StorageLocation text NOT NULL,\n"
                + "	entryDate text NOT NULL\n"
                + ");";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
