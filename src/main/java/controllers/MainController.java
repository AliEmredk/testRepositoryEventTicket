package controllers;

import be.Event;
import bll.EventManagement;
import bll.LoginCheck;
import dal.EventDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class MainController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private Button loginButton;

    @FXML
    private Button testButton;

    private LoginCheck loginCheck = new LoginCheck();

    @FXML
    public void initialize() {
        // Sync the visible password field with the actual password field
        visiblePasswordField.managedProperty().bind(showPasswordCheckBox.selectedProperty());
        visiblePasswordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty());
        passwordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty().not());

        // Mirror text between PasswordField and TextField
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        // Login button action (basic example)
        loginButton.setOnAction(event -> handleLogin());

    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        String result = loginCheck.checkLogin(username, password);

        if (result.equals("Admin")) {
            loadAdminScreen();
            System.out.println("Login for Admin successful");
        } else if (result.equals("Event Coordinator")) {
            loadECScreen();
            System.out.println("Login for Event Coordinator successful");
        } else {
            System.out.println("Login not successful");
        }
    }

    private void loadAdminScreen() {
        System.out.println("Loading Admin Screen");
    }

    private void loadECScreen() {
        System.out.println("Loading Event Coordinator Screen");
    }

    public void actionTestBtn(ActionEvent actionEvent) {
//        EventDAO eventDAO = new EventDAO();
//        eventDAO.assignCoordinatorToEvent(3,8);
//        System.out.println("Button clicked - attempting to assign coordinator");
//        EventDAO eventDAO = new EventDAO();
//        eventDAO.assignCoordinatorToEvent(4, 20);
//        System.out.println("Method assignCoordinatorToEvent executed");
        //works fine

        EventDAO eventDAO = new EventDAO();
        EventManagement eventManagement = new EventManagement();
//        eventDAO.deleteEvent("OMG!!");
        List<Event> events = eventManagement.getAllEvents();
//        for (Event event : events) {
//            System.out.println(event.getEventName() + " at " + event.getLocation());
//        }
    }
}
