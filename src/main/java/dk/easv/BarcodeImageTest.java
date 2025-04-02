package dk.easv;
import controllers.BarcodeController;
import dal.BarcodeDAO;
import dal.DBAccess;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;

public class BarcodeImageTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BarcodeImageTest.fxml"));
        Parent root = loader.load();

        // Get controller
        BarcodeController controller = loader.getController();

        // Set up DB connection and BarcodeDAO
        DBAccess dbAccess = new DBAccess();
        Connection conn = dbAccess.DBConnection();
        BarcodeDAO barcodeDAO = new BarcodeDAO(conn);

        // Inject the BarcodeDAO into the controller
        controller.setBarcodeDAO(barcodeDAO);

        // Set up stage
        primaryStage.setTitle("Barcode Test");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}