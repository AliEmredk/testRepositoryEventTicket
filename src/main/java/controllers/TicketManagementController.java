package controllers;

import bll.BarCodeGenerator;
import be.Barcode;
import be.Event;
import dal.EventDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

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
    private TextField discountField;
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

    private final EventDAO eventDAO = new EventDAO();

    @FXML
    public void initialize() {
        loadEvents();
        ticketTypeCombo.setItems(FXCollections.observableArrayList("VIP", "Standard", "Backstage"));

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        amountSpinner.setValueFactory(valueFactory);

        discountSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int discount = newVal.intValue();
            discountValueLabel.setText(discount + "% off");
        });
    }

    private void loadEvents() {
        List<Event> events = eventDAO.getAllEvents();
        eventCombo.setItems(FXCollections.observableArrayList(events));
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
            combinedDetails = "No additional details";
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
        printNode(qrCodeImageView);
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