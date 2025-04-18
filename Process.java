package org.example.demo6;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Process extends Inventory{


    String orderDate;
    String orderID;
    String oderStatus;
    String CustomerName;
    String productID;


    List<Map<String, Object>> orders = new ArrayList<Map<String, Object>>();

    public Process(String ProductName, int ProductID, int Quantity, double Price, String StorageLocation, String entryDate, String orderDate, String orderID, String oderStatus, String CustomerName) {
        super(ProductName, ProductID, Quantity, Price, StorageLocation, entryDate);
        this.orderDate = orderDate;
        this.orderID = orderID;
        this.oderStatus = oderStatus;
        this.CustomerName = CustomerName;

    }
    public Process(){
        this.orderDate = "Not assigned";
        this.orderID = "Not assigned";
        this.oderStatus = "Not assigned";
        this.CustomerName = "Not assigned";

    }


    public void makeOrder(String orderID,String orderDate, String oderStatus, String customerName,String ProductID){
        String sql = "INSERT INTO ProcessTable (OrderID,OrderDate, OrderStatus, CustomerName,ProductID) VALUES (?, ?, ?, ?,?)";
        try (Connection conn = ProcessDatabase.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderID);
            pstmt.setString(2, orderDate);
            pstmt.setString(3, oderStatus);
            pstmt.setString(4, customerName);
            pstmt.setString(5, ProductID);
            pstmt.executeUpdate();
            System.out.println("✅ Order added to database!");
        } catch (SQLException e) {
            System.out.println("Error adding order: " + e.getMessage());
        }

    }
    public void updateOrder(String orderID, String orderDate, String oderStatus, String customerName){
        String sql = "UPDATE ProcessTable SET OrderDate = ? , "
                + "OrderStatus = ? , "
                + "CustomerName = ? "
                + "WHERE OrderID = ?";
        try (Connection conn = ProcessDatabase.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderDate);
            pstmt.setString(2, oderStatus);
            pstmt.setString(3, customerName);
            pstmt.setString(4, orderID);
            pstmt.executeUpdate();
            System.out.println("✅ Order updated in database!");
        } catch (SQLException e) {
            System.out.println("Error updating order: " + e.getMessage());
        }
    }


}
