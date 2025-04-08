package dk.easv;

import be.Ticket;
import bll.BarCodeGenerator;
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
//        TicketDAO ticketDAO = new TicketDAO();
//        Ticket myTicket = new Ticket(0,"Normal Ticket", 34,"44ffef",5,"Don't Know", "Sakarya","Today","Tomorrow","Have fun!", "Come plz", 10, "Emre", "Uzunoglu","emre@gjail.com");
//        int ticketId = ticketDAO.saveTicket(myTicket);

//        if (ticketId != -1) {
//            ticketDAO.saveBarcode(ticketId, barcodeNumber);
//        } else {
//            System.out.println("Failed to save ticket");
//        }
    }
}
