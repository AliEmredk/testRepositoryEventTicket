package controllers;

import be.*;
import bll.BarCodeGenerator;
import dal.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.sql.Connection;

public class TicketManagementController {

    @FXML
    private Slider discountSlider;
    @FXML
    private Label discountValueLabel;
    @FXML
    private ComboBox<String> ticketTypeCombo;
    @FXML
    private ComboBox<Event> eventCombo;
    @FXML
    private Spinner<Integer> amountSpinner;
    @FXML
    private TextArea detailsField;

    @FXML
    private Label ticketIdLabel;
    @FXML
    private Label eventTitleLabel;
    @FXML
    private Label eventDateLabel;
    @FXML
    private Label eventTimeLabel;
    @FXML
    private Label eventLocationLabel;
    @FXML
    private Label ticketAmountLabel;
    @FXML
    private Label ticketDetailsLabel;
    @FXML
    private ImageView qrCodeImageView;
    @FXML
    private VBox ticketPreviewBox;
    @FXML
    private Button addCustomerButton;
    @FXML
    private ComboBox<Customer> customerComboBox;
    @FXML
    private TextField emailField;

    private final EventDAO eventDAO = new EventDAO();

    private final CustomerDAO customerDAO = new CustomerDAO();


    @FXML
    public void initialize() {
        loadEvents();
        loadCustomers();
        ticketTypeCombo.setItems(FXCollections.observableArrayList("VIP", "Standard", "Backstage"));

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        amountSpinner.setValueFactory(valueFactory);

        discountSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int discount = newVal.intValue();
            discountValueLabel.setText(discount + "% off");
        });
        addCustomerButton.setOnAction(event -> openAddCustomerWindow());
    }

    private void loadEvents() {
        List<Event> events = eventDAO.getAllEvents();
        eventCombo.setItems(FXCollections.observableArrayList(events));
    }

    private void loadCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();
        customerComboBox.setItems(FXCollections.observableArrayList(customers));
    }

    private void openAddCustomerWindow() {
        Stage addCustomerStage = new Stage();
        addCustomerStage.initModality(Modality.APPLICATION_MODAL);
        addCustomerStage.setTitle("Add Customer");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);

        // Customer Details Fields
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        emailField.setPromptText("Email");

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(event -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();

            if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all fields", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            if (!isValidEmail(email)) {
                showAlert("Invalid Email", "Please enter a valid email address.");
                return;
            }

            // Create the new customer
            Customer newCustomer = new Customer(firstName, lastName, email);
            customerDAO.addCustomer(newCustomer);
            loadCustomers();

            // Show success message
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Customer added successfully with ID: " + newCustomer.getCustomerId(), ButtonType.OK);
            successAlert.showAndWait();

            // Close the window after saving
            addCustomerStage.close();
        });

        vbox.getChildren().addAll(firstNameField, lastNameField, emailField, saveBtn);

        Scene scene = new Scene(vbox, 400, 200);
        addCustomerStage.setScene(scene);
        addCustomerStage.show();
    }

    @FXML
    private void updatePreview() {
        Event selectedEvent = eventCombo.getValue();
        if (selectedEvent == null) {
            showAlert("No Event Selected", "Please select an event to preview.");
            return;
        }

        // Generate new ticket ID and assign
        String ticketId = generateTicketId();
        ticketIdLabel.setText("Ticket ID: " + ticketId);

        // Update ticket preview fields
        eventTitleLabel.setText(selectedEvent.getEventName());
        eventDateLabel.setText(selectedEvent.getDate());
        eventTimeLabel.setText(selectedEvent.getStartTime() + " – " + selectedEvent.getEndTime());
        eventLocationLabel.setText(selectedEvent.getLocation());
        ticketAmountLabel.setText("Amount: " + amountSpinner.getValue());

        // Combine discount + custom details
        int discount = (int) discountSlider.getValue();
        String extraDetails = detailsField.getText().trim();
        String discountText = (discount > 0) ? (discount + "% discount applied") : "";
        String combinedDetails;

        if (!extraDetails.isEmpty() && !discountText.isEmpty()) {
            combinedDetails = discountText + " | " + extraDetails;
        } else if (!extraDetails.isEmpty()) {
            combinedDetails = extraDetails;
        } else if (!discountText.isEmpty()) {
            combinedDetails = discountText;
        } else {
            combinedDetails = "";
        }

        ticketDetailsLabel.setText(combinedDetails);

        // Generate barcode
        try {
            byte[] barcodeBytes = BarCodeGenerator.generateBarcode(ticketId);
            ByteArrayInputStream bis = new ByteArrayInputStream(barcodeBytes);
            Image barcodeImage = new Image(bis);
            qrCodeImageView.setImage(barcodeImage);

            Barcode barcode = new Barcode(0, barcodeBytes, ticketId);
            // Optional: save to DB -> barcodeDAO.saveBarcode(barcode);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Barcode Error", "Could not generate barcode.");
        }
    }

    private String generateTicketId() {
        return "#TCK" + (int) (Math.random() * 100000);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Pane loadTicketManagerView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TicketPreviewPage.fxml"));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return new StackPane(new Label("Failed to load Ticket Manager view."));
        }
    }

    @FXML
    private void printTicket() {
        Event selectedEvent = eventCombo.getValue();
        Customer selectedCustomer = customerComboBox.getValue();
        String email = selectedCustomer.getEmail();
        int discount = (int) discountSlider.getValue();
        int amount = amountSpinner.getValue();

        if (selectedEvent == null || selectedCustomer == null) {
            showAlert("Missing Info", "Please select an event, a customer, and enter a valid email address.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Invalid Email", "Please enter a valid email address.");
            return;
        }
        try {
            DBAccess dbAccess = new DBAccess();
            Connection conn = dbAccess.DBConnection();

            BarcodeDAO barcodeDAO = new BarcodeDAO(conn);
            TicketDAO ticketDAO = new TicketDAO(conn);

            int customerId = selectedCustomer.getCustomerId();

            String selectedTicketType = ticketTypeCombo.getValue();

            for (int i = 0; i < amount; i++) {
                String ticketId = generateTicketId();

                byte[] barcodeBytes = BarCodeGenerator.generateBarcode(ticketId);
                Barcode barcode = new Barcode(0, barcodeBytes, ticketId);
                int barcodeId = barcodeDAO.saveBarcode(barcode);

                if (barcodeId == -1) {
                    System.out.println("Barcode Error");
                    continue;
                }

                Ticket ticket = new Ticket();
                ticket.setBarcodeId(barcodeId);
                ticket.setCustomerId(customerId);
                ticket.setEventId(selectedEvent.getEventId());
                ticket.setDiscount(discount);
                ticket.setTicketType(selectedTicketType);
                ticket.setDetails(detailsField.getText());
                ticketDAO.saveTicket(ticket);
            }
            showAlert("Success", amount + " Tickets generated and saved.");
            printNode(ticketPreviewBox);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not generate tickets.");
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }

    private void printNode(Node node) {
        PrinterJob printJob = PrinterJob.createPrinterJob();
        if (printJob != null) {
            Window window = node.getScene().getWindow();
            if (printJob.showPrintDialog(window)) {
                boolean success = printJob.printPage(node);
                if (success) {
                    printJob.endJob();
                } else {
                    System.out.println("Printing failed");
                }
            }
        }
    }
}