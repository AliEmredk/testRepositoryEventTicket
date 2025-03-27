package controllers;

import be.Event;
import bll.EventManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class EventMainController {

    @FXML
    private ListView eventsListView;
    @FXML
    private Button logoutButtonEC;
    @FXML
    private Button btnEventCAdd;

    private final EventManagement eventManagement = new EventManagement();


    @FXML
    public void initialize() {
        loadEvents();
        logoutButtonEC.setOnAction(event -> logoutMainScreen());
    }

    public void loadEvents() {
        List<Event> events = eventManagement.getAllEvents();

        List<String> eventNames = events.stream()
                .map(Event::getEventName)
                .collect(Collectors.toList());

        ObservableList<String> observableList = FXCollections.observableArrayList(eventNames);
        eventsListView.setItems(observableList);
    }

    private void switchScene(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButtonEC.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void logoutMainScreen() {
        switchScene("LoginMain.fxml", "Login Screen");
    }

}
