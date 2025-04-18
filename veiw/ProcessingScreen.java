package org.example.demo6.veiw;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.demo6.database.ProcessDatabase;
import org.example.demo6.service.Process;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProcessingScreen {

    private TableView<ProcessItem> processTable;
    private DatePicker orderDateField;
    private TextField orderIDField;
    private TextField orderStatusField;
    private TextField customerNameField;
    private TextField productIDField;
    private org.example.demo6.service.Process processingManager;

    public ProcessingScreen() {
        processingManager = new Process();
    }

    public Node createProcessContent() {
        BorderPane borderPane = new BorderPane();

        // Create the form
        GridPane addOrderForm = createAddOrderForm();

        // Create the table
        processTable = createProcessTable();

        // Create buttons for table operations
        HBox tableButtons = createTableActionButtons();

        //  (center)
        VBox tableSection = new VBox(10);
        tableSection.setPadding(new Insets(10));
        tableSection.getChildren().addAll(new Label("Current state of orders"), processTable, tableButtons);

        //  (right side)
        VBox formSection = new VBox(10);
        formSection.setPadding(new Insets(10));
        formSection.getChildren().addAll(new Label("Order Management"), addOrderForm);

        // Set layout
        borderPane.setCenter(tableSection);
        borderPane.setRight(formSection);

        // Load initial data
        refreshProcessTable();

        return borderPane;
    }

    private TableView<ProcessItem> createProcessTable() {
        TableView<ProcessItem> table = new TableView<>();

        // Defining columns
        TableColumn<ProcessItem, String> orderIDCol = new TableColumn<>("Order ID");
        orderIDCol.setCellValueFactory(new PropertyValueFactory<>("orderID"));

        TableColumn<ProcessItem, String> orderDateCol = new TableColumn<>("Order Date");
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        TableColumn<ProcessItem, String> orderStatusCol = new TableColumn<>("Order Status");
        orderStatusCol.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

        TableColumn<ProcessItem, String> customerNameCol = new TableColumn<>("Customer Name");
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<ProcessItem, String> productIDCol = new TableColumn<>("Product ID");
        productIDCol.setCellValueFactory(new PropertyValueFactory<>("productID"));

        table.getColumns().addAll(orderIDCol, orderDateCol, orderStatusCol, customerNameCol, productIDCol);

        // Responsiveness
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Allow selecting rows
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        return table;
    }

    private GridPane createAddOrderForm() {
        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(10);
        form.setVgap(10);

        // Form fields
        orderDateField = new DatePicker(LocalDate.now());
        orderDateField.setPromptText("Order Date");

        orderIDField = new TextField();
        orderIDField.setPromptText("Order ID");

        orderStatusField = new TextField();
        orderStatusField.setPromptText("Order Status");

        customerNameField = new TextField();
        customerNameField.setPromptText("Customer Name");

        productIDField = new TextField();
        productIDField.setPromptText("Product ID");

        //  buttons
        Button addButton = new Button("Add Order");
        addButton.setOnAction(e -> handleAddOrder());

        Button clearButton = new Button("Clear Form");
        clearButton.setOnAction(e -> handleClearForm());

        Button updateButton = new Button("Update Order");
        updateButton.setOnAction(e -> handleUpdateOrder());

        Button selectButton = new Button("Select Order");
        selectButton.setOnAction(e -> handleSelectOrder());

        // some fields to form
        form.add(new Label("Order Date:"), 0, 0);
        form.add(orderDateField, 1, 0);

        form.add(new Label("Order ID:"), 0, 1);
        form.add(orderIDField, 1, 1);

        form.add(new Label("Order Status:"), 0, 2);
        form.add(orderStatusField, 1, 2);

        form.add(new Label("Customer Name:"), 0, 3);
        form.add(customerNameField, 1, 3);

        form.add(new Label("Product ID:"), 0, 4);
        form.add(productIDField, 1, 4);


        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.getChildren().addAll(addButton, updateButton, clearButton);

        form.add(buttonBox, 0, 5, 2, 1);
        return form;
    }

    private HBox createTableActionButtons() {
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> refreshProcessTable());

        Button selectButton = new Button("Select for Edit");
        selectButton.setOnAction(e -> handleSelectOrder());

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(refreshButton, selectButton);
        buttonBox.setPadding(new Insets(5, 0, 0, 0)); // Add some padding

        return buttonBox;
    }

    private void handleSelectOrder() {
        ProcessItem selectedItem = processTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("No Selection", "Please select an order to edit.");
            return;
        }

        // Populate the form with selected order details
        orderIDField.setText(selectedItem.getOrderID());
        orderDateField.setValue(LocalDate.parse(selectedItem.getOrderDate()));
        orderStatusField.setText(selectedItem.getOrderStatus());
        customerNameField.setText(selectedItem.getCustomerName());
        productIDField.setText(selectedItem.getProductID());
    }

    private void handleAddOrder() {
        try {
            String orderID = orderIDField.getText();
            String orderStatus = orderStatusField.getText();
            String customerName = customerNameField.getText();
            String productID = productIDField.getText();

            LocalDate date = orderDateField.getValue();
            if (date == null) {
                showAlert("Invalid Date", "Please select a valid date.");
                return;
            }
            String orderDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if (orderID.isEmpty() || customerName.isEmpty() || orderStatus.isEmpty() || productID.isEmpty()) {
                showAlert("Incomplete Form", "Please fill in all fields");
                return;
            }

            processingManager.makeOrder(orderID, orderDate, orderStatus, customerName, productID);
            refreshProcessTable();
            handleClearForm();

            showSuccessAlert("Order added successfully!");
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid data for Order ID and Product ID.");
        }
    }

    private void handleUpdateOrder() {
        try {
            String orderID = orderIDField.getText();
            String orderStatus = orderStatusField.getText();
            String customerName = customerNameField.getText();

            LocalDate date = orderDateField.getValue();
            if (date == null) {
                showAlert("Invalid Date", "Please select a valid date.");
                return;
            }
            String orderDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if (orderID.isEmpty() || customerName.isEmpty() || orderStatus.isEmpty()) {
                showAlert("Incomplete Form", "Please fill in all required fields");
                return;
            }

            processingManager.updateOrder(orderID, orderDate, orderStatus, customerName);
            refreshProcessTable();
            handleClearForm();

            showSuccessAlert("Order updated successfully!");
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid data for Order ID.");
        }
    }

    private void refreshProcessTable() {
        ObservableList<ProcessItem> data = FXCollections.observableArrayList();

        // Querying the database bec Process class doesn't have a method to get orders
        String sql = "SELECT * FROM ProcessTable";
        try (Connection conn = ProcessDatabase.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String orderID = rs.getString("OrderID");
                String orderDate = rs.getString("OrderDate");
                String orderStatus = rs.getString("OrderStatus");
                String customerName = rs.getString("CustomerName");
                String productID = rs.getString("ProductID");

                data.add(new ProcessItem(orderDate, orderID, orderStatus, customerName, productID));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving orders: " + e.getMessage());
            showAlert("Database Error", "Could not retrieve orders from database.");
        }

        processTable.setItems(data);
    }

    private void handleClearForm() {
        orderDateField.setValue(LocalDate.now());
        orderIDField.clear();
        orderStatusField.clear();
        customerNameField.clear();
        productIDField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public static class ProcessItem {
        private String orderDate;
        private String orderID;
        private String orderStatus;
        private String customerName;
        private String productID;

        public ProcessItem(String orderDate, String orderID, String orderStatus, String customerName, String productID) {
            this.orderDate = orderDate;
            this.orderID = orderID;
            this.orderStatus = orderStatus;
            this.customerName = customerName;
            this.productID = productID;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public String getOrderID() {
            return orderID;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getProductID() {
            return productID;
        }
    }
}