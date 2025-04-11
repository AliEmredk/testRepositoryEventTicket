package controllers;

import be.*;
import bll.BarCodeGenerator;
import dal.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class TicketManagementController {

    @FXML private Slider discountSlider;
    @FXML private Label discountValueLabel;
    @FXML private VBox discountBox;
    @FXML private ComboBox<TicketType> ticketTypeCombo;
    @FXML private ComboBox<Event> eventCombo;
    @FXML private Spinner<Integer> amountSpinner;
    @FXML private TextArea detailsField;
    @FXML private TextField emailField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;

    @FXML private Label ticketIdLabel;
    @FXML private Label eventTitleLabel;
    @FXML private Label eventDateLabel;
    @FXML private Label eventTimeLabel;
    @FXML private Label eventLocationLabel;
    @FXML private Label ticketAmountLabel;
    @FXML private Label ticketDetailsLabel;
    @FXML private ImageView qrCodeImageView;
    @FXML private VBox ticketPreviewBox;
    @FXML private VBox vipOptionsBox;
    @FXML private VBox specialOptionsBox;
    @FXML private RadioButton allEventsRadio;
    @FXML private RadioButton selectedEventRadio;
    @FXML private VBox emailContainer;

    // VIP Perks
    @FXML private CheckBox snackCheck;
    @FXML private CheckBox skipLineCheck;
    @FXML private CheckBox meetGuestCheck;
    @FXML private CheckBox foodIncludedCheck;

    // Special Perks
    @FXML private CheckBox freeDrinkCheck;
    @FXML private CheckBox freeSnackCheck;
    @FXML private CheckBox halfOffItemCheck;
    @FXML private CheckBox bogoCheck;
    @FXML private CheckBox bonusActivityCheck;
    @FXML private CheckBox oneTimeCouponCheck;
    @FXML private CheckBox raffleEntryCheck;
    @FXML private CheckBox exclusiveMerchCheck;

    private ToggleGroup specialScopeToggle = new ToggleGroup();

    private final EventDAO eventDAO = new EventDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private Connection connection;
    private TicketTypeDAO ticketTypeDAO;

    private final Map<CheckBox, String> specialDescriptions = new LinkedHashMap<>();

    public TicketManagementController() {
        try {
            this.connection = new DBAccess().DBConnection();
            this.ticketTypeDAO = new TicketTypeDAO(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        loadEvents();
        loadTicketTypes();
        allEventsRadio.setToggleGroup(specialScopeToggle);
        selectedEventRadio.setToggleGroup(specialScopeToggle);

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        amountSpinner.setValueFactory(valueFactory);

        discountSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            discountValueLabel.setText(newVal.intValue() + "% off");
        });

        ticketTypeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateUIForTicketType(newVal.getTicketTypeName());
            }
        });

        specialDescriptions.put(freeDrinkCheck, "🥤 Free Drink — Includes one soft drink or beer");
        specialDescriptions.put(freeSnackCheck, "🍪 Free Snack — Get a snack item at the event");
        specialDescriptions.put(halfOffItemCheck, "🤑 50% Off — Valid for any one item at food booth");
        specialDescriptions.put(bogoCheck, "🎟 Buy 1 Get 1 Free — Use at any eligible stand");
        specialDescriptions.put(bonusActivityCheck, "🎲 Bonus Activity — Join a side activity or mini-game");
        specialDescriptions.put(oneTimeCouponCheck, "🎫 One-Time Use Coupon — Redeemable once for any perk");
        specialDescriptions.put(raffleEntryCheck, "🎁 Raffle Entry — Automatically enters you in the prize draw");
        specialDescriptions.put(exclusiveMerchCheck, "🧢 Free Merchandise — Get club stickers or pins");
    }

    private void loadEvents() {
        List<Event> events = eventDAO.getAllEvents();
        eventCombo.setItems(FXCollections.observableArrayList(events));
    }

    private void loadTicketTypes() {
        try {
            List<TicketType> ticketTypes = ticketTypeDAO.getAllTicketTypes();
            ticketTypeCombo.setItems(FXCollections.observableArrayList(ticketTypes));

            ticketTypeCombo.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(TicketType item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((item == null || empty) ? null : item.getTicketTypeName());
                }
            });

            ticketTypeCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(TicketType item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((item == null || empty) ? null : item.getTicketTypeName());
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not load ticket types.");
        }
    }

    private void updateUIForTicketType(String type) {
        boolean isVIP = "VIP".equalsIgnoreCase(type);
        boolean isSpecial = "Special Ticket".equalsIgnoreCase(type);

        vipOptionsBox.setVisible(isVIP);
        vipOptionsBox.setManaged(isVIP);

        specialOptionsBox.setVisible(isSpecial);
        specialOptionsBox.setManaged(isSpecial);

        emailContainer.setVisible(!isSpecial);
        emailContainer.setManaged(!isSpecial);

        discountBox.setVisible(!isSpecial);
        discountBox.setManaged(!isSpecial);
    }

    @FXML
    private void updatePreview() {
        Event selectedEvent = eventCombo.getValue();
        TicketType selectedType = ticketTypeCombo.getValue();

        if (selectedEvent == null || selectedType == null) {
            showAlert("Missing Data", "Please select both an event and a ticket type.");
            return;
        }

        String ticketId = generateTicketId();
        ticketIdLabel.setText("Ticket ID: " + ticketId);

        eventTitleLabel.setText(selectedEvent.getEventName());
        eventDateLabel.setText(selectedEvent.getDate());
        eventTimeLabel.setText(selectedEvent.getStartTime() + " – " + selectedEvent.getEndTime());
        eventLocationLabel.setText(selectedEvent.getLocation());
        ticketAmountLabel.setText("Amount: " + amountSpinner.getValue());

        String finalDetails;

        if ("Special Ticket".equalsIgnoreCase(selectedType.getTicketTypeName())) {
            List<String> specialPerks = new ArrayList<>();
            for (Map.Entry<CheckBox, String> entry : specialDescriptions.entrySet()) {
                if (entry.getKey().isSelected()) {
                    specialPerks.add(entry.getValue());
                }
            }
            String manualText = detailsField.getText().trim();
            if (!manualText.isEmpty()) {
                specialPerks.add(manualText);
            }
            finalDetails = String.join("\n", specialPerks);
        } else {
            List<String> perks = new ArrayList<>();
            if ("VIP".equalsIgnoreCase(selectedType.getTicketTypeName())) {
                if (snackCheck.isSelected()) perks.add("🍿 Snack");
                if (skipLineCheck.isSelected()) perks.add("⏩ Skip Line");
                if (meetGuestCheck.isSelected()) perks.add("🎤 Meet Guest");
                if (foodIncludedCheck.isSelected()) perks.add("🍽 Food");
            }

            String joinedPerks = String.join(" | ", perks);
            finalDetails = buildCombinedText(joinedPerks);
        }

        ticketDetailsLabel.setText(finalDetails);

        try {
            byte[] barcodeBytes = BarCodeGenerator.generateBarcode(ticketId);
            Image barcodeImage = new Image(new ByteArrayInputStream(barcodeBytes));
            qrCodeImageView.setImage(barcodeImage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Barcode Error", "Could not generate barcode.");
        }
    }

    private String buildCombinedText(String perks) {
        int discount = (int) discountSlider.getValue();
        String extra = detailsField.getText().trim();
        String discountText = (discount > 0) ? discount + "% discount applied" : "";

        return (!perks.isEmpty() ? perks + ((extra.isEmpty() && discountText.isEmpty()) ? "" : " | ") : "") +
                (!extra.isEmpty() && !discountText.isEmpty() ? discountText + " | " + extra :
                        (!extra.isEmpty() ? extra : discountText));
    }

    private String generateTicketId() {
        return "#TCK" + (int) (Math.random() * 100000);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }

    @FXML
    private void generateAndPrintTickets() {
        Event selectedEvent = eventCombo.getValue();
        String email = emailField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        int discount = (int) discountSlider.getValue();
        String details = detailsField.getText().trim();
        int amount = amountSpinner.getValue();

        Customer customer = customerDAO.getOrCreateCustomerByEmail(email, firstName, lastName);
        if (customer == null) {
            showAlert("Error", "Could not get or create customer.");
        }

        if (selectedEvent == null || email.isEmpty()) {
            showAlert("Missing Info", "Please select an event and enter a valid email address.");
        }

        if (!emailField.isDisabled() && !isValidEmail(emailField.getText())) {
            showAlert("Invalid Email", "Please enter a valid email address.");
            return;
        }

        try {
            int customerId = customer.getCustomerId();

            BarcodeDAO barcodeDAO = new BarcodeDAO(connection);
            TicketDAO ticketDAO = new TicketDAO(connection);

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
                ticket.setDetails(details);
                ticket.setTicketType(ticketTypeCombo.getValue());

                ticketDAO.saveTicket(ticket);
            }
            showAlert("Success", amount + " Tickets generated and saved");
            printNode(ticketPreviewBox);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not generate tickets.");
        }
    }

    private void printNode(Node node) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            if (job.showPrintDialog(node.getScene().getWindow())) {
                if (job.printPage(node)) {
                    job.endJob();
                } else {
                    System.out.println("Printing failed");
                }
            }
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}