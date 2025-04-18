package org.example.demo6.application;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.demo6.database.DatabaseManager;
import org.example.demo6.database.InventoryTable;
import org.example.demo6.database.ProcessDatabase;
import org.example.demo6.database.ProcessTable;
import org.example.demo6.veiw.InventoryManagementScreen;
import org.example.demo6.veiw.ProcessingScreen;

public class ShipmentManagementApp extends Application {

    private InventoryManagementScreen inventoryScreen;
    private ProcessingScreen processingScreen;

    @Override
    public void start(Stage primaryStage) {
        // Initialise components
        inventoryScreen = new InventoryManagementScreen();
        processingScreen = new ProcessingScreen();


        InventoryTable.createTable();
        ProcessTable.createTable();

        BorderPane root = new BorderPane();

        // menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        //main contet area
        TabPane tabPane = createTabPane();
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Shipment Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // file menu
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exitItem);

        // dab menu
        Menu dbMenu = new Menu("Database");
        MenuItem initInventoryItem = new MenuItem("Initialize Inventory Table");
        initInventoryItem.setOnAction(e -> InventoryTable.createTable());

        MenuItem initProcessItem = new MenuItem("Initialize Process Table");
        initProcessItem.setOnAction(e -> ProcessTable.createTable());

        MenuItem testInventoryConnItem = new MenuItem("Test Inventory Connection");
        testInventoryConnItem.setOnAction(e -> DatabaseManager.connect());

        MenuItem testProcessConnItem = new MenuItem("Test Process Connection");
        testProcessConnItem.setOnAction(e -> ProcessDatabase.connect());





        dbMenu.getItems().addAll(
                initInventoryItem,
                initProcessItem,
                new SeparatorMenuItem(),
                testInventoryConnItem,
                testProcessConnItem,
                new SeparatorMenuItem()


        );

        menuBar.getMenus().addAll(fileMenu, dbMenu);
        return menuBar;
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();


        Tab dashboardTab = new Tab("Dashboard");
        Tab ordersTab = new Tab("Orders");
        Tab inventoryTab = new Tab("Inventory");
        Tab shipmentsTab = new Tab("Shipments");

        dashboardTab.setContent(createDashboardUI());
        ordersTab.setContent(processingScreen.createProcessContent());
        inventoryTab.setContent(inventoryScreen.createInventoryContent());
        shipmentsTab.setContent(createShipmentsUI());

        // non-closable
        dashboardTab.setClosable(false);
        ordersTab.setClosable(false);
        inventoryTab.setClosable(false);
        shipmentsTab.setClosable(false);

        //tabs to the tab pane
        tabPane.getTabs().addAll(dashboardTab, ordersTab, inventoryTab, shipmentsTab);

        return tabPane;
    }


    private Node createDashboardUI() {
        Label welcomeLabel = new Label("Welcome to the Shipment Management System!");
        welcomeLabel.setStyle("-fx-font-size: 20; -fx-padding: 20;");
        return welcomeLabel;
    }

    private Node createShipmentsUI() {
        return new Label("Shipments UI - To be implemented");
    }

    public static void main(String[] args) {
        launch(args);
    }
}