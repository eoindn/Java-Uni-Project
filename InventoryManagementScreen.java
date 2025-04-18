package org.example.demo6;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class InventoryManagementScreen {

    private TableView<InventoryItem> inventoryTable;
    private TextField productNameField;
    private TextField productIDField;
    private TextField quantityField;
    private TextField priceField;
    private TextField storageLocationField;
    private DatePicker entryDateField;
    private Inventory inventoryManager;

    public InventoryManagementScreen(){
        inventoryManager = new Inventory();
    }

    public Node createInventoryContent(){
        BorderPane borderPane = new BorderPane();

        // Create form for adding products
        GridPane addProductForm = createAddProductForm();

        // Create table to display inventory
        inventoryTable = createInventoryTable();

        // Create buttons for table actions
        HBox tableButtons = createTableActionButtons();

        // Create the table section (left/center)
        VBox tableSection = new VBox(10);
        tableSection.setPadding(new Insets(10));
        tableSection.getChildren().addAll(new Label("Current Inventory"), inventoryTable, tableButtons);

        // Create the form section (right)
        VBox formSection = new VBox(10);
        formSection.setPadding(new Insets(10));
        formSection.getChildren().addAll(new Label("Add New Product"), addProductForm);

        // Set the layout
        borderPane.setCenter(tableSection);
        borderPane.setRight(formSection);

        // Load initial data
        refreshInventoryTable();

        return borderPane;
    }

    private TableView<InventoryItem> createInventoryTable() {
        TableView<InventoryItem> table = new TableView<>();

        // Define columns
        TableColumn<InventoryItem, Integer> idColumn = new TableColumn<>("Product ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("productId")); // Fixed property name

        TableColumn<InventoryItem, String> nameColumn = new TableColumn<>("Product Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<InventoryItem, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<InventoryItem, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<InventoryItem, String> locationColumn = new TableColumn<>("Storage Location");
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("storageLocation"));

        TableColumn<InventoryItem, String> dateColumn = new TableColumn<>("Entry Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("entryDate"));

        table.getColumns().addAll(idColumn, nameColumn, quantityColumn,
                priceColumn, locationColumn, dateColumn);

        // Make table columns resize with the table
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Allow selecting rows
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        return table;
    }

    private GridPane createAddProductForm() {
        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(10);
        form.setVgap(10);

        // Create form fields
        productNameField = new TextField();
        productNameField.setPromptText("Product Name");

        productIDField = new TextField();
        productIDField.setPromptText("Product ID");

        quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        priceField = new TextField();
        priceField.setPromptText("Price");

        storageLocationField = new TextField();
        storageLocationField.setPromptText("Storage Location");

        entryDateField = new DatePicker(LocalDate.now());
        entryDateField.setPromptText("Entry Date");

        // Create buttons
        Button addButton = new Button("Add Product");
        addButton.setOnAction(e -> handleAddProduct());

        Button clearButton = new Button("Clear Form");
        clearButton.setOnAction(e -> clearForm());

        // Add form fields to the grid
        form.add(new Label("Product Name:"), 0, 0);
        form.add(productNameField, 1, 0);

        form.add(new Label("Product ID:"), 0, 1);
        form.add(productIDField, 1, 1);

        form.add(new Label("Quantity:"), 0, 2);
        form.add(quantityField, 1, 2);

        form.add(new Label("Price:"), 0, 3);
        form.add(priceField, 1, 3);

        form.add(new Label("Storage Location:"), 0, 4);
        form.add(storageLocationField, 1, 4);

        form.add(new Label("Entry Date:"), 0, 5);
        form.add(entryDateField, 1, 5);


        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.getChildren().addAll(addButton, clearButton);

        // Add the buttons t
        form.add(buttonBox, 1, 6);

        return form;
    }

    private HBox createTableActionButtons(){
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> refreshInventoryTable());

        Button removeButton = new Button("Remove");
        removeButton.setOnAction(e -> handleRemoveProduct()); // Fixed method name

        Button decreaseButton = new Button("Decrease Quantity");
        decreaseButton.setOnAction(e -> handleDecreaseQuantity());

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(refreshButton, removeButton, decreaseButton);
        buttonBox.setPadding(new Insets(5, 0, 0, 0)); // Add some padding

        return buttonBox;
    }

    private void handleAddProduct() {
        try{
            String name = productNameField.getText();
            int id = Integer.parseInt(productIDField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            String location = storageLocationField.getText();

            // Get the date
            LocalDate date = entryDateField.getValue();
            if (date == null) {
                date = LocalDate.now(); // Default to today if not selected
            }
            String entryDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if(name.isEmpty() || location.isEmpty()){
                showAlert("Invalid input","Please fill in all fields.");
                return;
            }

            inventoryManager.addProduct(name, id, quantity, price, location, entryDate);

            refreshInventoryTable();
            clearForm();


            showSuccessAlert("Product Added", "Product was successfully added to inventory.");

        }catch (NumberFormatException e){
            showAlert("Invalid input" ,"Please enter valid numbers for ID, quantity, and price.");
        }
    }

    private void handleRemoveProduct(){
        InventoryItem selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if(selectedItem == null){ // Fixed logic
            showAlert("No Selection", "Please select a product to remove.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Remove Product");
        confirmation.setHeaderText("Are you sure you want to remove this product?");
        confirmation.setContentText("This action cannot be undone.");

        confirmation.showAndWait().ifPresent(response -> {
            if(response == ButtonType.OK){
                inventoryManager.removeProduct(selectedItem.getProductId());
                refreshInventoryTable();
                showSuccessAlert("Product Removed", "The product was successfully removed.");
            }
        });
    }

    private void handleDecreaseQuantity() {
        InventoryItem selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("No Selection", "Please select a product to update.");
            return;
        }

        // dialog to get quantity to decrease
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Decrease Quantity");
        dialog.setHeaderText("Decrease Quantity for " + selectedItem.getProductName());
        dialog.setContentText("Enter amount to decrease:");

        dialog.showAndWait().ifPresent(result -> {
            try {
                int amount = Integer.parseInt(result);
                if (amount <= 0) {
                    showAlert("Invalid Amount", "Please enter a positive number.");
                    return;
                }

                inventoryManager.decreaseQuantity(selectedItem.getProductId(), amount);
                refreshInventoryTable();
                showSuccessAlert("Quantity Updated", "The quantity was successfully decreased.");

            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number.");
            }
        });
    }

    private void refreshInventoryTable() {
        // Clear current items
        ObservableList<InventoryItem> items = FXCollections.observableArrayList();

        // Get all products from database
        List<Map<String, Object>> products = inventoryManager.getAllProducts();

        System.out.println("Found " + products.size() + " products in database");

        // Convert to InventoryItem objects
        for (Map<String, Object> product : products) {
            InventoryItem item = new InventoryItem(
                    (String) product.get("ProductName"),
                    (Integer) product.get("ProductID"),
                    (Integer) product.get("Quantity"),
                    (Double) product.get("Price"),
                    (String) product.get("StorageLocation"),
                    (String) product.get("entryDate")
            );
            items.add(item);
        }

        // Update table
        inventoryTable.setItems(items);
    }

    private void clearForm() {
        productNameField.clear();
        productIDField.clear();
        quantityField.clear();
        priceField.clear();
        storageLocationField.clear();
        entryDateField.setValue(LocalDate.now());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public static class InventoryItem {
        private final String productName;
        private final int productId;
        private final int quantity;
        private final double price;
        private final String storageLocation;
        private final String entryDate;

        public InventoryItem(String productName, int productId, int quantity,
                             double price, String storageLocation, String entryDate) {
            this.productName = productName;
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
            this.storageLocation = storageLocation;
            this.entryDate = entryDate;
        }

        // Getters
        public String getProductName() { return productName; }
        public int getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public String getStorageLocation() { return storageLocation; }
        public String getEntryDate() { return entryDate; }
    }
}
