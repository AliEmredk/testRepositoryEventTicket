package dk.easv;

import be.Event;
import be.User;
import dal.EventDAO;
import dal.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class EventsView extends StackPane {

    private String role;
    private TilePane eventContainer;
    private EventDAO eventDAO = new EventDAO();
    private Event selectedEvent;
    private Button editEventBtn;
    private Button deleteEventBtn;

    public EventsView(String role) {
        this.role = role;

        VBox eventsPane = new VBox(10);
        eventsPane.setPadding(new Insets(15));
        eventsPane.setStyle("-fx-background-color: white;");

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button addEventBtn = new Button("Add Event");
        addEventBtn.setOnAction(e -> openAddEventWindow());

        editEventBtn = new Button("Edit Event");
        editEventBtn.setDisable(true);
        editEventBtn.setOnAction(e -> openEditEventWindow());

        deleteEventBtn = new Button("Delete Event");
        deleteEventBtn.setDisable(true);
        deleteEventBtn.setOnAction(e -> openDeleteEventWindow());

        TextField searchField = new TextField();
        searchField.setPromptText("Search events...");
        Button searchBtn = new Button("\uD83D\uDD0E"); //Magnifying Glass Emoji/Icon

        if(this.role.equals("Admin")) {
            addEventBtn.setVisible(false);
            addEventBtn.setManaged(false);
        }

        if(this.role.equals("Admin")) {
            editEventBtn.setVisible(false);
            editEventBtn.setManaged(false);
        }

        topBar.getChildren().addAll(addEventBtn, editEventBtn, deleteEventBtn, searchField, searchBtn);

        eventContainer = new TilePane();
        eventContainer.setHgap(10);
        eventContainer.setVgap(10);
        eventContainer.setPrefColumns(3);
        eventContainer.setPadding(new Insets(10));

        EventDAO eventDAO = new EventDAO();
        for (Event event : eventDAO.getAllEvents()) {
            VBox eventCard = createEventCard(event);
            eventContainer.getChildren().add(eventCard);
        }

        ScrollPane scrollPane = new ScrollPane(eventContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(600);
        scrollPane.setStyle("-fx-background: white;");

        eventsPane.getChildren().addAll(topBar, scrollPane);

        this.getChildren().add(eventsPane);
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox();
        card.setPadding(new Insets(10));
        card.setSpacing(5);
        card.setAlignment(Pos.CENTER);
        card.setStyle("""
                -fx-background-color: #FFECB3;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-padding: 10;
                fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 3);
                """);

        Label nameLabel = new Label(event.getEventName());
        nameLabel.setFont(new Font(16));

        Label locationLabel = new Label(event.getLocation());
        locationLabel.setFont(new Font(12));
        locationLabel.setTextFill(Color.DARKGRAY);

        Label dateLabel = new Label(event.getDate());
        dateLabel.setFont(new Font(12));
        dateLabel.setTextFill(Color.DARKGRAY);

        Button editBtn = new Button("Edit");

        if(this.role.equals("Admin")) {
            editBtn.setVisible(false);
            editBtn.setManaged(false);
        }

        card.getChildren().addAll(nameLabel, locationLabel, dateLabel);

        card.setOnMouseClicked(e -> setSelectedEvent(event, card));

        return card;
    }

    private void refreshEventList() {
        eventContainer.getChildren().clear();
        for(Event event : eventDAO.getAllEvents()) {
            VBox eventCard = createEventCard(event);
            eventContainer.getChildren().add(eventCard);
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
        ListView<User> coordinatorListView = new ListView<>();
        ObservableList<User> coordinatorObservableList = FXCollections.observableArrayList(coordinators);
        coordinatorListView.setItems(coordinatorObservableList);

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

            eventDAO.createEvent(newEvent);
            refreshEventList();

            for(User selectedUser : coordinatorListView.getSelectionModel().getSelectedItems()) {
                eventDAO.assignCoordinatorToEvent(newEvent.getEventId(), selectedUser.getUser_Id());
            }
            addEventsStage.close();
        });

        vbox.getChildren().addAll(nameField, locationField, datePicker, startTimeField, endTimeField, noteField, priceField, locationGuidanceField, new Label("Assign Coordinators:"), coordinatorListView, saveBtn);

        Scene scene = new Scene(vbox, 400, 500);
        addEventsStage.setScene(scene);
        addEventsStage.show();
    }

    private void setSelectedEvent(Event event, VBox eventCard) {
        this.selectedEvent = event;
        for (var node : eventContainer.getChildren()) {
            node.setStyle("-fx-background-color: #FFECB3; -fx-border-radius: 10; -fx-background-radius: 10;");
        }
        eventCard.setStyle("-fx-background-color: #FFD54F; -fx-border-radius: 10; -fx-background-radius: 10;");

        editEventBtn.setDisable(false);
        deleteEventBtn.setDisable(false);
    }

    private void openEditEventWindow() {
        if(selectedEvent == null) {
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

        Button saveBtn = new Button("Save Changes");
        saveBtn.setOnAction(e -> {
            if(nameField.getText().isEmpty() || locationField.getText().isEmpty()) {
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

            eventDAO.updateEvent(selectedEvent.getEventId(), selectedEvent);
            refreshEventList();
            editEventsStage.close();
            editEventBtn.setDisable(true);
            deleteEventBtn.setDisable(true);
        });

        vbox.getChildren().addAll(nameField, locationField, datePicker, startTimeField, endTimeField, noteField, priceField, locationGuidanceField, saveBtn);

        Scene scene = new Scene(vbox, 400, 500);
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
                eventDAO.deleteEvent(selectedEvent.getEventName());
                refreshEventList();
            }
            deleteEventBtn.setDisable(true);
        });
    }
}