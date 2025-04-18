package org.example.demo6.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ShippingTable {

    public static void createTable(){
        String sql = "CREATE TABLE IF NOT EXISTS ShipmentsTable (\n"
                + "	ShipmentID integer PRIMARY KEY,\n"
                + "ProductID INTEGER NOT NULL,\n"
                + "	Destination text NOT NULL,\n"
                + "	Status text NOT NULL,\n"
                + "	ShipmentDate text NOT NULL,\n"
                + "FOREIGN KEY (ProductID) REFERENCES ProductsTable(ProductID)"
                + ");";

        try (Connection ShipConn = ShippingDataBase.connect();
             Statement stmt = ShipConn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
