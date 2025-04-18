package org.example.demo6.service;

import org.example.demo6.database.ShippingDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class ShippingDetails {

    private Map<Integer, Map<String, Object>> shipments = new HashMap<>();


    public void addShipment(int shipmentId, int productID, String destination, String status,String date) {
        String sql = "INSERT INTO ShipmentsTable (ShipmentID, ProductID, Destination, Status, ShipmentDate) VALUES (?, ?, ?, ?,?)";

        try(Connection conn = ShippingDataBase.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, shipmentId);
            pstmt.setInt(2, productID);
            pstmt.setString(3, destination);
            pstmt.setString(4, status);
            pstmt.setString(5, date);
            pstmt.executeUpdate();
            System.out.println("✅ Shipment added to database!");
        } catch (SQLException e) {
            System.out.println("Error adding shipment: " + e.getMessage());
        }
    }



    public Map<String, Object> getShipment(int shipmentId) {
        return shipments.getOrDefault(shipmentId, null);
    }

    public void displayAllShipments() {
        for (var entry : shipments.entrySet()) {
            System.out.println("Shipment ID: " + entry.getKey() + " -> " + entry.getValue());
        }
    }


    public void updateShipment(int shipmentId, String newStatus) {
        if (shipments.containsKey(shipmentId)) {
            shipments.get(shipmentId).put("status", newStatus);
        }
    }


    public void deleteShipment(int shipmentId) {
        shipments.remove(shipmentId);
    }
}

