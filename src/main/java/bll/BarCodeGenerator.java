package bll;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.oned.Code128Writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.UUID;

public class BarCodeGenerator {
    public static String generateBarcode(String number) {
        int width = 400;
        int height = 150;

        // Generate one unique code in UUID
        String barcodeNumber = generateUniqueString();

        // Save the code in resources by now until we create the database
        String directory = "resources/Barcode/";
        String filePath = directory + barcodeNumber + ".png";

        try {
            // directory
            Files.createDirectories(FileSystems.getDefault().getPath(directory));

            // Code Bar
            Code128Writer barcodeWriter = new Code128Writer();
            BitMatrix bitMatrix = barcodeWriter.encode(barcodeNumber, BarcodeFormat.CODE_128, width, height);

            // save the images of the code bar
            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            System.out.println("Generated Barcode: " + barcodeNumber);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return barcodeNumber;
    }

    // method to create a unique code in UUID
    public static String generateUniqueString() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
