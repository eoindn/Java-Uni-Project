package org.example.demo6.veiw;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.example.demo6.database.ShippingDataBase;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.example.demo6.database.DatabaseManager;
import org.example.demo6.database.ProcessDatabase;

public class DashboardScreen {

    public static Node createDashboardContent() {
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(20));
        mainPane.setStyle("-fx-background-color: #f0f0f0;");


        Label title = new Label("Inventory Management Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));
        mainPane.setTop(title);

        // gird layoutfor charts
        GridPane chartGrid = new GridPane();
        chartGrid.setHgap(20);
        chartGrid.setVgap(20);
        chartGrid.setPadding(new Insets(10));
        chartGrid.setAlignment(Pos.CENTER);

        // add da charts to the grid
        chartGrid.add(createStatusPieChart(), 0, 0);
        chartGrid.add(createInventoryBarChart(), 1, 0);
        chartGrid.add(createOrderTimeSeriesChart(), 0, 1);
        chartGrid.add(createTopDestinationsChart(), 1, 1);


        HBox summaryCards = createSummaryCards();


        VBox centerContent = new VBox(20);
        centerContent.getChildren().addAll(summaryCards, chartGrid);
        mainPane.setCenter(centerContent);

        return mainPane;
    }

    private static PieChart createStatusPieChart() {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Shipment Status Distribution");

        // Query database
        Map<String, Integer> statusCount = new HashMap<>();

        String sql = "SELECT Status, COUNT(*) as count FROM ShipmentsTable GROUP BY Status";
        try (Connection conn = ShippingDataBase.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String statusName = rs.getString("Status");
                int count = rs.getInt("count");
                statusCount.put(statusName, count);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving shipment status data: " + e.getMessage());
            // fall back
            statusCount.put("Pending", 0);
            statusCount.put("In Transit", 0);
            statusCount.put("Delivered", 0);
            statusCount.put("Cancelled", 0);
        }

        // fall back if no data found
        if (statusCount.isEmpty()) {
            statusCount.put("No Data", 1);
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : statusCount.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        pieChart.setData(pieChartData);
        pieChart.setLabelLineLength(10);
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);

        pieChart.setStyle("-fx-background-color: white; -fx-padding: 10px;");
        pieChart.setPrefSize(400, 300);

        return pieChart;
    }

    private static BarChart<String, Number> createInventoryBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Product");
        yAxis.setLabel("Quantity");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top Inventory Items");
        barChart.setLegendVisible(false);

        // hit up db for top 5 products by quantity
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Product Quantities");

        String sql = "SELECT ProductName, Quantity FROM ProductsTable ORDER BY Quantity DESC LIMIT 5";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String productName = rs.getString("ProductName");
                int quantity = rs.getInt("Quantity");
                series.getData().add(new XYChart.Data<>(productName, quantity));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving inventory data: " + e.getMessage());
            // default fallback
            series.getData().add(new XYChart.Data<>("Sample Product", 0));
        }

        // placeholder if no data found
        if (series.getData().isEmpty()) {
            series.getData().add(new XYChart.Data<>("No Data", 0));
        }

        barChart.getData().add(series);
        barChart.setStyle("-fx-background-color: white; -fx-padding: 10px;");
        barChart.setPrefSize(400, 300);

        return barChart;
    }

    private static LineChart<String, Number> createOrderTimeSeriesChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Orders");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Recent Order Trends");


        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Orders");

        //orders from last 7 days
        Map<String, Integer> ordersByDate = new HashMap<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            ordersByDate.put(formattedDate, 0);
        }


        String sql = "SELECT OrderDate, COUNT(*) as count FROM ProcessTable " +
                "WHERE OrderDate >= date('now', '-7 days') " +
                "GROUP BY OrderDate";
        try (Connection conn = ProcessDatabase.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String orderDate = rs.getString("OrderDate");
                int count = rs.getInt("count");
                ordersByDate.put(orderDate, count);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving order trend data: " + e.getMessage());
        }

        //add data to series in date order
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String displayDate = date.format(DateTimeFormatter.ofPattern("MM-dd"));
            series.getData().add(new XYChart.Data<>(displayDate, ordersByDate.getOrDefault(formattedDate, 0)));
        }

        lineChart.getData().add(series);
        lineChart.setStyle("-fx-background-color: white; -fx-padding: 10px;");
        lineChart.setPrefSize(400, 300);

        return lineChart;
    }

    private static BarChart<String, Number> createTopDestinationsChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Destination");
        yAxis.setLabel("Shipments");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top Shipping Destinations");
        barChart.setLegendVisible(false);


        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Shipment Count");

        String sql = "SELECT Destination, COUNT(*) as count FROM ShipmentsTable " +
                "GROUP BY Destination ORDER BY count DESC LIMIT 5";
        try (Connection conn = ShippingDataBase.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String destination = rs.getString("Destination");
                int count = rs.getInt("count");
                series.getData().add(new XYChart.Data<>(destination, count));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving destination data: " + e.getMessage());
            //fall back
            series.getData().add(new XYChart.Data<>("Sample Destination", 0));
        }

        //placeholder
        if (series.getData().isEmpty()) {
            series.getData().add(new XYChart.Data<>("No Data", 0));
        }

        barChart.getData().add(series);
        barChart.setStyle("-fx-background-color: white; -fx-padding: 10px;");
        barChart.setPrefSize(400, 300);

        return barChart;
    }

    private static HBox createSummaryCards() {
        HBox cardContainer = new HBox(20);
        cardContainer.setAlignment(Pos.CENTER);
        cardContainer.setPadding(new Insets(10));

        //summary statistics
        int totalProducts = getTotalProducts();
        int totalOrders = getTotalOrders();
        int pendingShipments = getPendingShipments();
        double totalInventoryValue = getTotalInventoryValue();

        //cards for each statistic
        VBox productCard = createSummaryCard("Total Products", String.valueOf(totalProducts), "#4285F4");
        VBox orderCard = createSummaryCard("Total Orders", String.valueOf(totalOrders), "#EA4335");
        VBox shipmentCard = createSummaryCard("Pending Shipments", String.valueOf(pendingShipments), "#FBBC05");
        VBox valueCard = createSummaryCard("Inventory Value", String.format("$%.2f", totalInventoryValue), "#34A853");

        cardContainer.getChildren().addAll(productCard, orderCard, shipmentCard, valueCard);
        return cardContainer;
    }

    private static VBox createSummaryCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        card.setPrefHeight(120);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #616161;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }


    private static int getTotalProducts() {
        String sql = "SELECT COUNT(*) as count FROM ProductsTable";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving product count: " + e.getMessage());
        }
        return 0;
    }

    private static int getTotalOrders() {
        String sql = "SELECT COUNT(*) as count FROM ProcessTable";
        try (Connection conn = ProcessDatabase.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving order count: " + e.getMessage());
        }
        return 0;
    }

    private static int getPendingShipments() {
        String sql = "SELECT COUNT(*) as count FROM ShipmentsTable WHERE Status = 'Pending' OR Status = 'In Transit'";
        try (Connection conn = ShippingDataBase.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving pending shipments: " + e.getMessage());
        }
        return 0;
    }

    private static double getTotalInventoryValue() {
        String sql = "SELECT SUM(Quantity * Price) as total_value FROM ProductsTable";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("total_value");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving inventory value: " + e.getMessage());
        }
        return 0.0;
    }
}