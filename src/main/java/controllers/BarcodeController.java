package controllers;

import bll.BarCodeGenerator;
import dal.BarcodeDAO;
import be.Barcode;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import java.io.ByteArrayInputStream;
import java.util.UUID;

public class BarcodeController {

    @FXML
    private ImageView barcodeImageView;

    private BarcodeDAO barcodeDAO;

    public void setBarcodeDAO(BarcodeDAO barcodeDAO) {
        this.barcodeDAO = barcodeDAO;
    }

    @FXML
    private void loadBarcodeAction() {
        try {
            if (barcodeDAO == null) {
                showAlert("Barcode DAO not set");
                return;
            }

            String barcodeString = UUID.randomUUID().toString().substring(0, 12).toUpperCase();

            // Generate a new barcode
            byte[] barcodeImage = BarCodeGenerator.generateBarcode("Test");

            Barcode newBarcode = new Barcode(0, barcodeImage, barcodeString);

            // Save the barcode to the database
            int generatedId = barcodeDAO.saveBarcode(newBarcode);
            System.out.println("Barcode saved with ID: " + generatedId);

            // Retrieve the barcode from the database to verify
            Barcode retrievedBarcode = barcodeDAO.getBarcodeById(generatedId);

            if (retrievedBarcode != null && retrievedBarcode.getBarcodeImage() != null) {
                ByteArrayInputStream bis = new ByteArrayInputStream(retrievedBarcode.getBarcodeImage());
                Image barcodeImageFx = new Image(bis);

                barcodeImageView.setImage(barcodeImageFx);
                System.out.println("Barcode successfully loaded and displayed!");
            } else {
                showAlert("No barcode found for the given ID.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("An error occurred while loading the barcode.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}