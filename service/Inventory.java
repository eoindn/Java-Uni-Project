package org.example.demo6.service;

import org.example.demo6.database.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {

    String ProductName;
    int ProductID;
    int Quantity;
    double Price;
    String StorageLocation;
    String entryDate;

    public Inventory(String ProductName, int ProductID, int Quantity, double Price, String StorageLocation, String entryDate) {
        this.ProductName = ProductName;
        this.ProductID = ProductID;
        this.Quantity = Quantity;
        this.Price = Price;
        this.StorageLocation = StorageLocation;
        this.entryDate = entryDate;
    }

    public Inventory(){
        this.ProductName = "Not assigned";
        this.Price = 0.0;
        this.StorageLocation = "None";
        this.entryDate = "Not provided";
        this.Quantity = 0;
        this.ProductID = 0;
    }

    // Add a product to da database
    public void addProduct(String name, int productID, int quantity, double price, String storageLocation, String entryDate) {
        String sql = "INSERT INTO ProductsTable (ProductID, ProductName, Quantity, Price, StorageLocation, entryDate) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productID);
            pstmt.setString(2, name);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, price);
            pstmt.setString(5, storageLocation);
            pstmt.setString(6, entryDate);
            pstmt.executeUpdate();
            System.out.println("✅ Product added to database!");
        } catch (SQLException e) {
            System.out.println("Error adding product: " + e.getMessage());
        }
    }

    public void decreaseQuantity(int productID, int quantity) {
        String sql = "UPDATE ProductsTable SET Quantity = Quantity - ? WHERE ProductID = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productID);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Quantity updated successfully.");
            } else {
                System.out.println("Product not found or insufficient quantity.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating quantity: " + e.getMessage());
        }
    }

    // Remove a product from the database by ProductID
    public void removeProduct(int productID) {
        String sql = "DELETE FROM ProductsTable WHERE ProductID = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productID);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Product removed from database.");
            } else {
                System.out.println("Product not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error removing product: " + e.getMessage());
        }
    }

    // Get product information by ProductID
    public Map<String, Object> getProductInfo(int productID) {
        String sql = "SELECT * FROM ProductsTable WHERE ProductID = ?";
        Map<String, Object> product = new HashMap<>();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                product.put("ProductID", rs.getInt("ProductID"));
                product.put("ProductName", rs.getString("ProductName"));
                product.put("Quantity", rs.getInt("Quantity"));
                product.put("Price", rs.getDouble("Price"));
                product.put("StorageLocation", rs.getString("StorageLocation"));
                product.put("entryDate", rs.getString("entryDate"));
            } else {
                System.out.println("Product not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving product info: " + e.getMessage());
        }

        return product;
    }

    // Get everything from the products table NOW
    public List<Map<String, Object>> getAllProducts() {
        List<Map<String, Object>> products = new ArrayList<>();
        String sql = "SELECT * FROM ProductsTable";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("ProductID", rs.getInt("ProductID"));
                product.put("ProductName", rs.getString("ProductName"));
                product.put("Quantity", rs.getInt("Quantity"));
                product.put("Price", rs.getDouble("Price"));
                product.put("StorageLocation", rs.getString("StorageLocation"));
                product.put("entryDate", rs.getString("entryDate"));
                products.add(product);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving products: " + e.getMessage());
        }

        return products;
    }
}
