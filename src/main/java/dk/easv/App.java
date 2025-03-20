package dk.easv;

import DAL.UserDAO;
import be.User;
import bll.UserManagement;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new  FXMLLoader(getClass().getResource("view/EASV Ticket - Login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Login Screen");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    
    UserDAO userDAO = new UserDAO();

    public static void main(String[] args) {
        launch(args);
    }
}