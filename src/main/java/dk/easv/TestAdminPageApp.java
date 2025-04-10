package dk.easv;

import be.Ticket;
import bll.BarCodeGenerator;
import bll.CoordinatorEventManager;
import bll.TicketManager;
import dal.TicketDAO;
import dal.UserDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestAdminPageApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new  FXMLLoader(getClass().getResource("/view/AdminMain.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Login Screen");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    UserDAO userDAO = new UserDAO();


    public static void main(String[] args) {
        launch(args);
        CoordinatorEventManager cem = new CoordinatorEventManager();
        cem.assignCoordinatorToOwnEvents("EC4","EC3","Poker Night");
    }
}
