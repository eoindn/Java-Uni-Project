package org.example.demo6;

//String orderDate;
//String orderID;
//String oderStatus;
//String CustomerName;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ProcessTable {


    public static void createTable(){
        String sql = "CREATE TABLE IF NOT EXISTS ProcessTable (\n"
                + "	OrderID integer PRIMARY KEY,\n"
                + "	OrderDate text NOT NULL,\n"
                + "	OrderStatus text NOT NULL,\n"
                + "	CustomerName text NOT NULL,\n"
                + "	ProductID INTEGER,\n"
                + "	FOREIGN KEY (ProductID) REFERENCES ProductsTable(ProductID)"
                + ");";

        try (Connection ProcessConn = ProcessDatabase.connect();
             Statement stmt = ProcessConn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
