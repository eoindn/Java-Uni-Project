package org.example.demo6;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ShippingDataBase {

    public static Connection connect(){
        Connection ShipConn = null;
        try {
            ShipConn = DriverManager.getConnection("jdbc:sqlite:ShippingBase.db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ShipConn;
    }
}

