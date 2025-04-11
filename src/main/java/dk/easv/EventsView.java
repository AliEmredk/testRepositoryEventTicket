package dk.easv;

import be.Event;
import be.User;
import dal.CoordinatorEventDAO;
import dal.EventDAO;
import dal.UserDAO;
import controllers.EventMainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;


public class EventsView extends StackPane {

    private String role;
    private TilePane eventContainer;
    private EventDAO eventDAO = new EventDAO();
    private Event selectedEvent;
    private Button editEventBtn, deleteEventBtn, assignCoordinatorBtn;
    private List<Event> masterEventList = new ArrayList<>();
    private EventMainController eventMainController;
    private final User loggedInUser;
    private final CoordinatorEventDAO coordinatorEventDAO = new CoordinatorEventDAO();

    private static final String CARD_STYLE_DEFAULT = """
        -fx-background-color: white;
        -fx-border-color: #ccc;
        -fx-border-width: 1;
        -fx-border-radius: 10;
        -fx-background-radius: 10;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 6, 0, 0, 4);
    """;

    private static final String CARD_STYLE_SELECTED = """
        -fx-background-color: #FFD54F;
        -fx-border-radius: 10;
        -fx-background-radius: 10;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 6, 0, 0, 4);
    """;

    public EventsView(User loggedInUser, EventMainController eventMainController) {

        BorderPane mainLayout = new BorderPane();
        this.loggedInUser = loggedInUser;
        this.eventMainController = eventMainController;
        this.role = loggedInUser.getRole();


        VBox eventsPane = new VBox(10);
        eventsPane.setPadding(new Insets(15));
        eventsPane.setStyle("-fx-background-color: white;");

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0,0,10,0));

        Button addEventBtn = new Button("➕");
        addEventBtn.setTooltip(new Tooltip("Add Event"));
        addEventBtn.setOnAction(e -> openAddEventWindow());

        editEventBtn = new Button("✏");
        editEventBtn.setTooltip(new Tooltip("Edit Selected Event"));
        editEventBtn.setOnAction(e -> openEditEventWindow());

        deleteEventBtn = new Button("🗑");
        deleteEventBtn.setTooltip(new Tooltip("Delete Selected Event"));
        deleteEventBtn.setOnAction(e -> openDeleteEventWindow());

        assignCoordinatorBtn = new Button("Assign Coordinator");
        assignCoordinatorBtn.setDisable(true);
        assignCoordinatorBtn.setOnAction(e -> openAssignCoordinatorWindow());

        if(!this.role.equalsIgnoreCase("Admin")) {
            assignCoordinatorBtn.setVisible(false);
            assignCoordinatorBtn.setManaged(false);
        }

        // Handle Sorting
        ComboBox<String> sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll("None", "Sort by Date", "Sort by Price", "Sort by Location");
        sortComboBox.setOnAction(e -> {
            String sortCriteria = sortComboBox.getValue();
            if (sortCriteria.equals("None")) {
                eventMainController.loadEvents(); // Reset sort by reloading full list
            } else {
                eventMainController.sortEvents(sortCriteria);
            }
            refreshEventList();
        });

        // Handle Filtering
        ComboBox<String> filterComboBox = new ComboBox<>();
        filterComboBox.getItems().addAll("None", "Free Events", "Paid Events", "Today’s Events", "This Month’s Events");
        filterComboBox.setOnAction(e -> {
            String filter = filterComboBox.getValue();
            eventMainController.filterEvents(filter);  // Delegate filter action to the controller
            refreshEventList();  // Refresh list after filtering
        });

        TextField searchField = new TextField();
        searchField.setPromptText("Search events...");
        Button searchBtn = new Button("\uD83D\uDD0E"); //Magnifying Glass Emoji/Icon

        if(this.role.equals("Admin")) {
            addEventBtn.setVisible(false);
            addEventBtn.setManaged(false);
        }

        topBar.getChildren().addAll(addEventBtn, editEventBtn, deleteEventBtn, assignCoordinatorBtn, sortComboBox, filterComboBox, searchField, searchBtn);

        searchBtn.setOnAction(e -> {
            String searchQuery = searchField.getText();
            eventMainController.searchEvents(searchQuery);  // Delegate to the controller
            refreshEventList();  // Refresh the list to display the filtered events
        });
        if(this.role.equals("Admin")) {
            editEventBtn.setVisible(false);
            editEventBtn.setManaged(false);
        }

        eventContainer = new TilePane();
        eventContainer.setHgap(10);
        eventContainer.setVgap(10);
        eventContainer.setPrefColumns(2);
        eventContainer.setPadding(new Insets(10));

        refreshEventList();
        //This part duplicates the Events in the Event list, so it's commented out for now
//        EventDAO eventDAO = new EventDAO();
//        for (Event event : eventDAO.getAllEvents()) {
//            VBox eventCard = createEventCard(event);
//            eventContainer.getChildren().add(eventCard);
//        }

        ScrollPane scrollPane = new ScrollPane(eventContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(600);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        scrollPane.setStyle("-fx-background: white;");

        eventsPane.getChildren().addAll(topBar, scrollPane);
        mainLayout.setCenter(eventsPane);
        this.getChildren().add(mainLayout);
    }

    private VBox createEventCard(Event event) {
        System.out.println("Creating tile for event: " + event.getEventName()); // Debugging line

        VBox card = new VBox();
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("""
        -fx-background-color: white;
        -fx-border-color: #ccc;
        -fx-border-width: 1;
        -fx-border-radius: 10;
        -fx-background-radius: 10;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 6, 0, 0, 4);
    """);
        card.setPadding(new Insets(10));
        card.setSpacing(10);
        card.setPrefWidth(220);

        if (event.getImagePath() != null && !event.getImagePath().isEmpty()) {
            try {
                Image image = new Image(event.getImagePath(), true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(200);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                card.getChildren().add(imageView);
            } catch (Exception e) {
                System.err.println("⚠ Failed to load image for event: " + event.getEventName());
                e.printStackTrace();
            }
        }


        VBox labelBox = new VBox(5);
        labelBox.setAlignment(Pos.CENTER);
        labelBox.setStyle("-fx-padding: 10 0 0 0;");

        Label nameLabel = new Label(event.getEventName());
        nameLabel.setFont(new Font(16));

        Label pinEmoji = new Label("📍");
        pinEmoji.setFont(new Font(12));

        Label locationLabel = new Label(event.getLocation());
        locationLabel.setFont(new Font(12));
        locationLabel.setTextFill(Color.DARKGRAY);

        HBox locationBox = new HBox(5, pinEmoji, locationLabel);
        locationBox.setAlignment(Pos.CENTER);

        Label dateLabel = new Label(event.getDate());
        dateLabel.setFont(new Font(12));
        dateLabel.setTextFill(Color.DARKGRAY);

        labelBox.getChildren().addAll(nameLabel, locationBox, dateLabel);
        card.getChildren().add(labelBox);

        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                BorderPane mainLayout = (BorderPane) this.getChildren().get(0);
                showEventDetailsSideBar(event, mainLayout);
            } else {
                setSelectedEvent(event, card);
            }
        });

        return card;
    }

    private void refreshEventList() {
        eventMainController.loadEvents();
        eventContainer.getChildren().clear();
        List<Event> filteredEvents = eventMainController.getFilteredEvents();  // Get the filtered list from the controller
        for (Event event : filteredEvents) {
            eventContainer.getChildren().add(createEventCard(event));
        /* TODO - check this!
        for(Event event : eventDAO.getAllEvents()) {
            VBox eventCard = createEventCard(event);
            eventContainer.getChildren().add(eventCard);
            */
        }
    }

    private void openAddEventWindow() {
        Stage addEventsStage = new Stage();
        addEventsStage.initModality(Modality.APPLICATION_MODAL);
        addEventsStage.setTitle("Add Event");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);

        TextField nameField = new TextField();
        nameField.setPromptText("Event Name");

        TextField locationField = new TextField();
        locationField.setPromptText("Event Location");

        DatePicker datePicker = new DatePicker();

        TextField startTimeField = new TextField();
        startTimeField.setPromptText("Start Time");

        TextField endTimeField = new TextField();
        endTimeField.setPromptText("End Time");

        TextArea noteField = new TextArea();
        noteField.setPromptText("Notes");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        TextField locationGuidanceField = new TextField();
        locationGuidanceField.setPromptText("Location Guidance");

        UserDAO userDAO = new UserDAO();
        List<User> coordinators = userDAO.getAllEventCoordinators();
        ObservableList<User> coordinatorObservableList = FXCollections.observableArrayList(coordinators);
        ListView<User> coordinatorListView = new ListView<>();
        coordinatorListView.setItems(coordinatorObservableList);
        coordinatorListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //List<Integer> assignedCoordinatorIds = eventDAO.getCoordinatorIdsForEvent(selectedEvent.getEventId());
//        for(User user : coordinators) {
//            if(assignedCoordinatorIds.contains(user.getUser_Id())) {
//                coordinatorListView.getSelectionModel().select(user);
        final String[] selectedEventImagePath = {null};

        Button selectImageBtn = new Button("Choose Event Image");
        Label imageLabel = new Label("No image selected");

        selectImageBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Event Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                imageLabel.setText(selectedFile.getName());
                selectedEventImagePath[0] = selectedFile.toURI().toString(); // Save the file path as URI
            }
        });
//            }
//        }

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(event -> {
            if(nameField.getText().isEmpty() || locationField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all fields", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            int price = 0;
            try {
                price = Integer.parseInt(priceField.getText());
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid price format!", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            Event newEvent = new Event(locationField.getText(), datePicker.getValue().toString(), startTimeField.getText(),
                    endTimeField.getText(), noteField.getText(), price, locationGuidanceField.getText(), nameField.getText(), 0);

            newEvent.setImagePath(selectedEventImagePath[0]); // Set image before saving
            eventDAO.createEvent(newEvent);

            refreshEventList();

            //eventDAO.clearCoordinatorsForEvent(selectedEvent.getEventId());
            for(User selectedUser : coordinatorListView.getSelectionModel().getSelectedItems()) {
                eventDAO.assignCoordinatorToEvent(newEvent.getEventId(), selectedUser.getUser_Id());
            }
            addEventsStage.close();
        });

        vbox.getChildren().addAll(
                nameField, locationField, datePicker, startTimeField, endTimeField,
                noteField, priceField, locationGuidanceField,
                selectImageBtn, imageLabel,
                new Label("Assign Coordinators:"), coordinatorListView, saveBtn
        );

        Scene scene = new Scene(vbox, 400, 500);
        addEventsStage.setScene(scene);
        addEventsStage.show();
    }

    private void setSelectedEvent(Event event, VBox selectedCard) {
        this.selectedEvent = event;

        // Reset style for all event cards
        for (var node : eventContainer.getChildren()) {
            if (node instanceof VBox card) {
                card.setStyle(CARD_STYLE_DEFAULT);
            }
        }

        // Apply selected style to the clicked card
        selectedCard.setStyle(CARD_STYLE_SELECTED);

        // Enable edit and delete buttons
        editEventBtn.setDisable(false);
        deleteEventBtn.setDisable(false);
        assignCoordinatorBtn.setDisable(false);
    }

    private void openEditEventWindow() {
        if (selectedEvent == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an event", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Stage editEventsStage = new Stage();
        editEventsStage.initModality(Modality.APPLICATION_MODAL);
        editEventsStage.setTitle("Edit Event");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);

        TextField nameField = new TextField(selectedEvent.getEventName());
        nameField.setPromptText("Event Name");

        TextField locationField = new TextField(selectedEvent.getLocation());
        locationField.setPromptText("Event Location");

        DatePicker datePicker = new DatePicker(LocalDate.parse(selectedEvent.getDate()));

        TextField startTimeField = new TextField(selectedEvent.getStartTime());
        startTimeField.setPromptText("Start Time");

        TextField endTimeField = new TextField(selectedEvent.getEndTime());
        endTimeField.setPromptText("End Time");

        TextArea noteField = new TextArea(selectedEvent.getNote());
        noteField.setPromptText("Notes");

        TextField priceField = new TextField(String.valueOf(selectedEvent.getPrice()));
        priceField.setPromptText("Price");

        TextField locationGuidanceField = new TextField(selectedEvent.getLocation_Guidance());
        locationGuidanceField.setPromptText("Location Guidance");

        // 🔽 Add image selection logic here
        final String[] selectedEventImagePath = {selectedEvent.getImagePath()};
        Button selectImageBtn = new Button("Change Event Image");
        Label imageLabel = new Label(selectedEvent.getImagePath() != null ? selectedEvent.getImagePath() : "No image selected");

        selectImageBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Event Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                imageLabel.setText(selectedFile.getName());
                selectedEventImagePath[0] = selectedFile.toURI().toString();
            }
        });

        Button saveBtn = new Button("Save Changes");
        saveBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty() || locationField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all fields", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            int price;
            try {
                price = Integer.parseInt(priceField.getText());
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid price format!", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            selectedEvent.setEventName(nameField.getText());
            selectedEvent.setLocation(locationField.getText());
            selectedEvent.setDate(datePicker.getValue().toString());
            selectedEvent.setStartTime(startTimeField.getText());
            selectedEvent.setEndTime(endTimeField.getText());
            selectedEvent.setNote(noteField.getText());
            selectedEvent.setPrice(price);
            selectedEvent.setLocation_Guidance(locationGuidanceField.getText());
            selectedEvent.setImagePath(selectedEventImagePath[0]); // Save image path

            eventDAO.updateEvent(selectedEvent.getEventId(), selectedEvent);
            refreshEventList();
            editEventsStage.close();
            editEventBtn.setDisable(true);
            deleteEventBtn.setDisable(true);
        });

        vbox.getChildren().addAll(
                nameField, locationField, datePicker, startTimeField, endTimeField,
                noteField, priceField, locationGuidanceField,
                selectImageBtn, imageLabel,
                saveBtn
        );

        Scene scene = new Scene(vbox, 400, 550);
        editEventsStage.setScene(scene);
        editEventsStage.show();
    }

    private void openDeleteEventWindow() {
        if(selectedEvent == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an event to delete!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Are you sure you want to delete this event?");
        confirmAlert.setContentText("Event: " + selectedEvent.getEventName() + " will be deleted.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if(response == ButtonType.OK) {
                EventDAO eventDAO = new EventDAO();
                eventDAO.deleteEvent(selectedEvent.getEventId());

                refreshEventList();
            }
            deleteEventBtn.setDisable(true);
        });
    }

    private void showEventDetailsSideBar(Event event, BorderPane mainLayout) {
        VBox sideBar = new VBox(10);
        if (event.getImagePath() != null && !event.getImagePath().isEmpty()) {
            try {
                Image image = new Image(event.getImagePath(), true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(260); // Adjust to fit sidebar width
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setStyle("-fx-border-radius: 10; -fx-background-radius: 10;");
                sideBar.getChildren().add(imageView);
            } catch (Exception e) {
                System.err.println("⚠ Failed to load image in detail view: " + event.getEventName());
            }
        }
        sideBar.setPadding(new Insets(15));
        sideBar.setStyle("-fx-background-color: #FFF8E1;");
        sideBar.setPrefWidth(300);

        HBox header = new HBox();
        Label title = new Label("Event Details");
        title.setFont(new Font("Arial", 18));
        title.setStyle("-fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("❌");
        closeBtn.setStyle("-fx-background-color: #FF6F61; -fx-font-size: 14;");
        closeBtn.setOnAction(e -> mainLayout.setRight(null)); //Removes sidebar

        header.getChildren().addAll(title, spacer, closeBtn);


        // -- DETAIL CARD WRAPPER (this is where your code goes) --
        VBox detailCard = new VBox(10);
        detailCard.setPadding(new Insets(20));
        detailCard.setStyle("""
        -fx-background-color: white;
        -fx-background-radius: 12;
        -fx-border-radius: 12;
        -fx-border-color: #ddd;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 4);
    """);

        // -- DETAIL LABELS --
        Label name = new Label("🎉 " + event.getEventName());
        Label location = new Label("📍 " + event.getLocation());
        Label date = new Label("📅 " + event.getDate());
        Label time = new Label("⏰ " + event.getStartTime() + " - " + event.getEndTime());
        Label price = new Label("💸 " + event.getPrice() + " DKK");
        Label notes = new Label("Notes: " + String.join(" | ", event.getNote().split("\\R+")));
        Label guidance = new Label("Guidance: " + event.getLocation_Guidance());

        for (Label lbl : List.of(name, location, date, time, price, notes, guidance)) {
            lbl.setFont(new Font("Arial", 13));
            lbl.setWrapText(true);
        }

        sideBar.getChildren().addAll(header, name, location, date, time, price, notes, guidance);
        mainLayout.setRight(sideBar);
    }

    private void openAssignCoordinatorWindow() {
        if(selectedEvent == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an event to assign coordinator!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Stage assignStage = new Stage();
        assignStage.setTitle("Assign Coordinator");
        assignStage.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);

        UserDAO userDAO = new UserDAO();
        List<User> coordinators = userDAO.getAllEventCoordinators();
        ObservableList<User> coordinatorsObservableList = FXCollections.observableList(coordinators);

        ListView<User> coordinatorListView = new ListView<>(coordinatorsObservableList);
        coordinatorListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        List<Integer> assignedCoordinatorIds = coordinatorEventDAO.getCoordinatorIdsForEvent(selectedEvent.getEventId());
        for(User user : coordinators) {
            if(assignedCoordinatorIds.contains(user.getUser_Id())) {
                coordinatorListView.getSelectionModel().select(user);
            }
        }

        Button assignBtn = new Button("Assign");
        assignBtn.setOnAction(e -> {
            coordinatorEventDAO.clearCoordinatorsForEvent(selectedEvent.getEventId());
            for(User user : coordinatorListView.getSelectionModel().getSelectedItems()) {
                coordinatorEventDAO.assignCoordinatorToEventById(user.getUser_Id(), selectedEvent.getEventId());
                System.out.println("Assigned UserId " + user.getUser_Id());
            }
            assignStage.close();
        });

        vbox.getChildren().addAll(new Label("Select Coordinator:"), coordinatorListView, assignBtn);
        Scene scene = new Scene(vbox, 400, 400);
        assignStage.setScene(scene);
        assignStage.show();
    }
}


