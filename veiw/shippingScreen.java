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
import org.example.demo6.database.ShippingDataBase;
import org.example.demo6.service.ShippingDetails;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class shippingScreen {

    private TableView<ShippingItem> shippingTable;
    private TextField ShipmentIDField;
    private TextField ProductIDField;
    private TextField DestinationField;
    private TextField ShippingStatusField;
    private DatePicker ShippingDateField;
    private ShippingDetails shippingManager;

    public shippingScreen() {
        shippingManager = new ShippingDetails();
    }

    public Node createShippingContent() {
        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add("border-pane");

        // Create the form
        GridPane addShippingForm = createAddShipmentForm();

        // Create the table
        shippingTable = createShippingTable();

        // Create buttons for table operations
        HBox tableButtons = createTableActionButtons();

        // Center section
        VBox tableSection = new VBox(10);
        tableSection.setPadding(new Insets(10));
        tableSection.getStyleClass().add("section-box");

        Label tableSectionLabel = new Label("Current Shipments");
        tableSectionLabel.getStyleClass().add("section-label");

        // Only add one label, not two
        tableSection.getChildren().addAll(tableSectionLabel, shippingTable, tableButtons);

        // Right section
        VBox formSection = new VBox(10);
        formSection.setPadding(new Insets(10));
        formSection.getStyleClass().add("section-box");

        Label formSectionLabel = new Label("Add Shipping Details");
        formSectionLabel.getStyleClass().add("section-label");

        formSection.getChildren().addAll(formSectionLabel, addShippingForm);

        // Set Layout
        borderPane.setCenter(tableSection);
        borderPane.setRight(formSection);

        // Load CSS once (not twice)
        try {
            URL cssFile = getClass().getResource("/shipping-screen.css");
            if (cssFile != null) {
                borderPane.getStylesheets().add(cssFile.toExternalForm());
            } else {
                System.out.println("Warning: CSS file not found! Running without styles.");
            }
        } catch (Exception e) {
            System.out.println("Error loading CSS: " + e.getMessage());
        }




        // Load initial data after creating the table
        refreshShippingTable();

        return borderPane;
    }

    private TableView<ShippingItem> createShippingTable() {
        TableView<ShippingItem> table = new TableView<>();

        TableColumn<ShippingItem, String> shipmentIDCol = new TableColumn<>("Shipment ID");
        shipmentIDCol.setCellValueFactory(new PropertyValueFactory<>("shipmentID"));

        TableColumn<ShippingItem, String> productIDCol = new TableColumn<>("Product ID");
        productIDCol.setCellValueFactory(new PropertyValueFactory<>("productID"));

        TableColumn<ShippingItem, String> destinationCol = new TableColumn<>("Destination");
        destinationCol.setCellValueFactory(new PropertyValueFactory<>("destination"));

        TableColumn<ShippingItem, String> shippingStatusCol = new TableColumn<>("Shipping Status");
        shippingStatusCol.setCellValueFactory(new PropertyValueFactory<>("shippingStatus"));

        shippingStatusCol.setCellFactory(column -> {
            return new TableCell<ShippingItem, String>() {
                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);

                    if (status == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(status);

                        // diff colours based on status
                        if (status.equalsIgnoreCase("pending") || status.equalsIgnoreCase("in transit")) {
                            setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                        } else if (status.equalsIgnoreCase("delivered")) {
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        } else if (status.equalsIgnoreCase("cancelled")) {
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
        });


        TableColumn<ShippingItem, String> shippingDateCol = new TableColumn<>("Shipping Date");
        shippingDateCol.setCellValueFactory(new PropertyValueFactory<>("shippingDate"));

        table.getColumns().addAll(shipmentIDCol, productIDCol, destinationCol, shippingStatusCol, shippingDateCol);

        // Responsiveness
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Allow user to select a row
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        return table;
    }

    public TableView<ShippingItem> getShippingTable() {
        return shippingTable;
    }

    public TextField getShipmentIDField() {
        return ShipmentIDField;
    }

    public TextField getProductIDField() {
        return ProductIDField;
    }

    public TextField getDestinationField() {
        return DestinationField;
    }

    public TextField getShippingStatusField() {
        return ShippingStatusField;
    }

    public DatePicker getShippingDateField() {
        return ShippingDateField;
    }

    private GridPane createAddShipmentForm() {
        GridPane form = new GridPane();
        form.getStyleClass().add("form-grid");
        form.setPadding(new Insets(10));
        form.setHgap(10);
        form.setVgap(10);

        // Fields for forms
        ShippingDateField = new DatePicker(LocalDate.now());
        ShippingDateField.setPromptText("Shipping Date");

        ShipmentIDField = new TextField();
        ShipmentIDField.setPromptText("Shipment ID");

        ProductIDField = new TextField();
        ProductIDField.setPromptText("Product ID");

        DestinationField = new TextField();
        DestinationField.setPromptText("Destination");

        ShippingStatusField = new TextField();
        ShippingStatusField.setPromptText("Shipping Status");

        // buttons
        Button addButton = new Button("Add");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> handleAddShipping());

        Button clearButton = new Button("Clear Form");
        clearButton.getStyleClass().add("secondary-button");
        clearButton.setOnAction(e -> handleClearForm());

        Button updateButton = new Button("Update Status");
        updateButton.getStyleClass().add("secondary-button");
        updateButton.setOnAction(e -> handleUpdateStatus());

        Button selectButton = new Button("Select Shipment");
        selectButton.setOnAction(actionEvent -> handleSelectShipment());



        // some fields to form
        Label dateLabel = new Label("Shipping Date:");
        dateLabel.getStyleClass().add("form-label");
        form.add(dateLabel, 0, 0);
        form.add(ShippingDateField, 1, 0);

        Label shipmentIDLabel = new Label("Shipment ID:");
        shipmentIDLabel.getStyleClass().add("form-label");
        form.add(shipmentIDLabel, 0, 1);
        form.add(ShipmentIDField, 1, 1);

        Label productIDLabel = new Label("Product ID:");
        productIDLabel.getStyleClass().add("form-label");
        form.add(productIDLabel, 0, 2);

        form.add(ProductIDField, 1, 2);
        Label destinationLabel = new Label("Destination:");
        destinationLabel.getStyleClass().add("form-label");

        form.add(destinationLabel, 0, 3);
        form.add(DestinationField, 1, 3);
        Label shippingStatusLabel = new Label("Shipping Status:");

        shippingStatusLabel.getStyleClass().add("form-label");
        form.add(shippingStatusLabel, 0, 4);
        form.add(ShippingStatusField, 1, 4);

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.getChildren().addAll(addButton, updateButton, clearButton, selectButton);

        form.add(buttonBox, 0, 5, 2, 1);
        return form;
    }

    private HBox createTableActionButtons() {
        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("success-button");
        refreshButton.setOnAction(e -> refreshShippingTable());

        Button selectButton = new Button("Select for Edit");
        selectButton.getStyleClass().add("info-button");
        selectButton.setOnAction(e -> handleSelectShipment());

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(refreshButton, selectButton);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

        return buttonBox;
    }

    private void handleSelectShipment() {
        ShippingItem selectedItem = shippingTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("No Selection", "Please select a shipment to edit.");
            return;
        }

        ProductIDField.setText(selectedItem.getProductID());
        ShippingDateField.setValue(LocalDate.parse(selectedItem.getShippingDate()));
        ShippingStatusField.setText(selectedItem.getShippingStatus());
        DestinationField.setText(selectedItem.getDestination());
        ShipmentIDField.setText(selectedItem.getShipmentID());
    }

    private void handleAddShipping() {
        try {
            String shipmentID = ShipmentIDField.getText();
            String shippingStatus = ShippingStatusField.getText();
            String shippingDestination = DestinationField.getText();
            String productID = ProductIDField.getText();

            LocalDate date = ShippingDateField.getValue();
            if (date == null) {
                showAlert("Invalid Date", "Please select a valid date.");
                return;
            }
            String shippingDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if (shipmentID.isEmpty() || shippingStatus.isEmpty() || shippingDestination.isEmpty() || productID.isEmpty()) {
                showAlert("Incomplete Form", "Please fill in all the fields");
                return;
            }

            shippingManager.addShipment(shipmentID, productID, shippingDestination, shippingStatus, shippingDate);
            refreshShippingTable();
            handleClearForm();

            showSuccessAlert("Shipment added successfully");
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid data for all fields");
        }
    }

    private void handleUpdateStatus() {
        try {
            String shipmentID = ShipmentIDField.getText();
            String shippingStatus = ShippingStatusField.getText();
            String destination = DestinationField.getText();

            LocalDate date = ShippingDateField.getValue();
            if (date == null) {
                showAlert("Invalid Date", "Please select a valid date.");
                return;
            }
            String shippingDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if (shipmentID.isEmpty() || shippingStatus.isEmpty() || destination.isEmpty()) {
                showAlert("Incomplete Form", "Please fill in all required fields");
                return;
            }

            shippingManager.updateShipment(shipmentID, shippingStatus);
            refreshShippingTable();
            handleClearForm();

            showSuccessAlert("Shipment updated successfully!");
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid data for Shipment ID.");
        }
    }

    private void refreshShippingTable() {
        ObservableList<ShippingItem> data = FXCollections.observableArrayList();

        // Querying the database
        String sql = "SELECT * FROM ShipmentsTable";
        try (Connection conn = ShippingDataBase.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String shipmentID = rs.getString("ShipmentID");
                String productID = rs.getString("ProductID");
                String destination = rs.getString("Destination");
                String status = rs.getString("Status");
                String shipmentDate = rs.getString("ShipmentDate");

                data.add(new ShippingItem(shipmentID, productID, destination, status, shipmentDate));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving shipments: " + e.getMessage());
            showAlert("Database Error", "Could not retrieve shipments from database.");
        }

        shippingTable.setItems(data);
    }

    private void handleClearForm() {
        ShippingDateField.setValue(LocalDate.now());
        ShipmentIDField.clear();
        ShippingStatusField.clear();
        DestinationField.clear();
        ProductIDField.clear();
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

    public static class ShippingItem {
        private String shipmentID;
        private String productID;
        private String destination;
        private String shippingStatus;
        private String shippingDate;

        public ShippingItem(String shipmentID, String productID, String destination, String shippingStatus, String shippingDate) {
            this.shipmentID = shipmentID;
            this.productID = productID;
            this.destination = destination;
            this.shippingStatus = shippingStatus;
            this.shippingDate = shippingDate;
        }

        public String getShipmentID() {
            return shipmentID;
        }

        public String getProductID() {
            return productID;
        }

        public String getDestination() {
            return destination;
        }

        public String getShippingStatus() {
            return shippingStatus;
        }

        public String getShippingDate() {
            return shippingDate;
        }
    }
}