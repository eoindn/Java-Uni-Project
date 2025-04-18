package org.example.demo6;

import org.example.demo6.database.InventoryTable;
import org.example.demo6.database.LoginTable;
import org.example.demo6.database.ProcessTable;
import org.example.demo6.database.ShippingTable;
import org.example.demo6.logEncrypt2.Login;
import org.example.demo6.logEncrypt2.VerifyUser;
import org.example.demo6.service.Inventory;
import org.example.demo6.service.Process;
import org.example.demo6.service.ShippingDetails;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        InventoryTable.createTable();
        ShippingTable.createTable();
        ProcessTable.createTable();
        LoginTable.createTable();
        run();


    }


    public static void run() {

        boolean flag1 = true;
        Scanner scanner = new Scanner(System.in);
        Inventory inventory = new Inventory();
        org.example.demo6.service.Process process = new Process();
        ShippingDetails shippingDetails = new ShippingDetails();
        Login login = new Login();

        while (flag1) {
            System.out.println("Please login or register an account");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("Enter your choice:");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Enter your username");
                    String usrname = scanner.next();
                    scanner.nextLine();

                    System.out.println("Enter your password");
                    String password = scanner.nextLine();

                    if (VerifyUser.verifyUser(usrname, password)) {
                        System.out.println("✅ Login successful");
                        flag1 = false;
                    } else {
                        System.out.println("Login failed");

                    }
                    break;
                case 2:
                    System.out.println("Please enter username");
                    String newUsername = scanner.next();
                    scanner.nextLine();

                    System.out.println("Please enter password");
                    String passForNewUser = scanner.next();
                    scanner.nextLine();

                    String salt = Login.saltSprinkle();
                    String hashedPassword = Login.hashPassword(passForNewUser, salt);

                    Login.addUser(newUsername, hashedPassword, salt);
                    System.out.println("Successfully registered!");
                    flag1 = false;
                    break;
                default:
                    System.out.println("Please provide a valid input ya knob");
                    break;

            }

            boolean flag = true;
            while (flag) {
                System.out.println("1. Add a product");
                System.out.println("2. Remove a product");
                System.out.println("3. View Products");
                System.out.println("4. Get a product info");
                System.out.println("5. View all products");
                System.out.println("6. Make an oder ");
                System.out.println("7. Process a shipment");
                System.out.println("8. Display a shipment");
                System.out.println("9. Update a shipment");
                System.out.println("10. Remove a shipment");
                System.out.println("Enter your choice: ");

                int choice1 = scanner.nextInt();
                switch (choice1) {
                    case 1:
                        System.out.println("Please provide the following information");
                        System.out.println("Date, Storage location, product name, product id, quantity, price");

                        System.out.println("Enter an id");
                        int id = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("Enter a name for the product");
                        String name = scanner.next();
                        scanner.nextLine();

                        System.out.println("Enter a location for storage");
                        String storage = scanner.next();
                        scanner.nextLine();

                        System.out.println("Enter a price in £");
                        double price = scanner.nextDouble();
                        scanner.nextLine();

                        System.out.println("Provide the quantity of stock available");
                        int quantity = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("Provide the current date");
                        String date = scanner.next();
                        scanner.nextLine();

                        System.out.println("Enter a name for the entry");
                        String entryName = scanner.next();


                        inventory.addProduct(name, id, quantity, price, storage, date);
                        System.out.println("Successfully added!");
                        break;
                    case 2:
                        System.out.println("Enter the id of the product you wish to remove");
                        int IdForRemoval = scanner.nextInt();
                        inventory.removeProduct(IdForRemoval);
                        break;
                    case 3:
                        System.out.println(inventory.getAllProducts());
                        break;
                    case 4:
                        System.out.println("Enter the id of the product you wish to view");
                        int da_id = scanner.nextInt();
                        System.out.println(inventory.getProductInfo(da_id));
                        break;
                    case 5:
                        System.out.println(inventory.getAllProducts());
                        break;
                    case 6:
                        System.out.println("Please provide the following information");
                        System.out.println("Order ID, Order Date, Order Status, Customer Name");

                        System.out.println("Enter an order id");
                        String orderID = scanner.next();
                        scanner.nextLine();

                        System.out.println("Enter an order date");
                        String orderDate = scanner.next();
                        scanner.nextLine();

                        System.out.println("Enter an order status");
                        String orderStatus = scanner.next();
                        scanner.nextLine();

                        System.out.println("Enter a customer name");
                        String customerName = scanner.next();
                        scanner.nextLine();

                        System.out.println("Enter a product id");
                        String productID = scanner.next();
                        scanner.nextLine();

                        process.makeOrder(orderID, orderDate, orderStatus, customerName,productID);
                        System.out.println("Successfully added!");
                        break;
                    case 7:
                        System.out.println("Please provide the following information");
                        System.out.println("Shipment ID,Product ID, Destination, Status");

                        System.out.println("Enter a shipment id");
                        int shipmentID = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("Enter a product id");
                        int ProductID = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("Enter a destination");
                        String destination = scanner.next();
                        scanner.nextLine();

                        System.out.println("Enter a status");
                        String status = scanner.next();

                        System.out.println("Enter a date");
                        String shipping_date = scanner.next();
                        scanner.nextLine();


                        shippingDetails.addShipment(shipmentID, ProductID, destination, status, shipping_date);
                        System.out.println("Successfully added!");
                        break;
                    case 8:
                        System.out.println("Enter the shipment id you wish to view");
                        int shipmentIDForView = scanner.nextInt();
                        System.out.println(shippingDetails.getShipment(shipmentIDForView));
                        break;
                    case 9:
                        System.out.println("Please provide the following information");
                        System.out.println("Shipment ID, New Status");

                        System.out.println("Enter a shipment id");
                        int shipmentIDForUpdate = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("Enter a new status");
                        String newStatus = scanner.next();
                        scanner.nextLine();

                        shippingDetails.updateShipment(shipmentIDForUpdate, newStatus);
                        System.out.println("Successfully updated!");
                        break;
                    case 10:
                        System.out.println("Enter the shipment id you wish to remove");
                        int shipmentIDForRemoval = scanner.nextInt();
                        shippingDetails.deleteShipment(shipmentIDForRemoval);
                        System.out.println("Successfully removed!");
                        break;


                    default:
                        System.out.println("Please provide a valid input");
                        break;

                }

            }
            scanner.close();


        }
    }
}