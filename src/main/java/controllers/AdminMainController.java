package controllers;

import be.Event;
import be.User;
import bll.EventManagement;
import bll.UserManagement;
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

public class AdminMainController {



    private final UserManagement userManagement = new UserManagement();
    private final EventManagement eventManagement = new EventManagement();

    @FXML
    private ListView<String> eventsListView;
    @FXML
    private ListView<String> eventCordListView;

    @FXML
    private ListView<User> eventCoordsListView;
    @FXML
    private Button logoutButtonAdmin;


    @FXML
    private void initialize() {
        loadEventCoordinators();
        loadEvents();
    }

    private void loadEventCoordinators() {

        List<User> coordinators = userManagement.getAllEventCoordinators();

        List<String> coordinatorNames = coordinators.stream()
                .map(User::getUsername) // Assuming "getUsername()" returns the name
                .collect(Collectors.toList());


        ObservableList<String> observableList = FXCollections.observableArrayList(coordinatorNames);
        eventCordListView.setItems(observableList);
    }

    public void loadEvents() {

        List<Event> events = eventManagement.getAllEvents();

        List<String> eventNames = events.stream()
                .map(Event::getEventName)
                .collect(Collectors.toList());

        ObservableList<String> observableList = FXCollections.observableArrayList(eventNames);
        eventsListView.setItems(observableList);

        logoutButtonAdmin.setOnAction(event -> logoutMainScreen());
    }

    private void switchScene(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButtonAdmin.getScene().getWindow();
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


    // we better have realistic event coordinators' names before presantation and we should be able to
    //open the admin page after login as a admin
//    private void loadEventCoordinators() {
//        List<User> coordinators = userManagement.getAllEventCoordinators();
//        ObservableList<User> observableList = FXCollections.observableArrayList(coordinators);
//        eventCoordsListView.setItems(observableList);
//    }


}
