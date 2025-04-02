package controllers;


import be.Ticket;
import bll.TicketManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;

        public class TicketController {
            private TicketManager ticketManager;

            @FXML
            private Label lblTicketId; // Label to display the ticket ID

            @FXML
            private ImageView imgBarcode; // ImageView to display the barcode image

            @FXML
            public void initialize() {
                // Initialization logic (if needed)
            }

            /**
             * Sets the TicketManager instance.
             *
             * @param ticketManager the TicketManager instance to be assigned.
             */
            public void setTicketManager(TicketManager ticketManager) {
                this.ticketManager = ticketManager;
            }

            /**
             * Generates a ticket and updates the UI with the ticket details.
             */
//            @FXML
//            public void generateTicket() {
//                if (ticketManager == null) {
//                    System.out.println("Error: TicketManager not found.");
//                    return;
//                }
//
//                String userId = "Event"; // Static user ID (this could be dynamic if needed)
//                Ticket ticket = ticketManager.generateTicket(userId);
//
//                if (ticket == null) {
//                    System.out.println("Error: Generated ticket is null.");
//                    return;
//                }
//
//                lblTicketId.setText("Ticket ID: " + ticket.getId());
//
//                // Load the barcode image using getBarcodeImageURL()
//                try {
//                    Image barcodeImage = new Image(ticket.getBarcodeImage()); // Updated as per TODO comment
//                    imgBarcode.setImage(barcodeImage);
//                } catch (Exception e) {
//                    System.out.println("Error loading barcode image: " + e.getMessage());
//                }
//            }

            /**
             * Displays the ticket window.
             */
            public void showTicketWindow() {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TicketView.fxml"));
                    Parent root = loader.load();

                    TicketController controller = loader.getController();
                    controller.setTicketManager(ticketManager);

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Ticket Window");
                    stage.show();
                } catch (IOException e) {
                    System.out.println("Error loading Ticket window: " + e.getMessage());
                }
            }
        }


