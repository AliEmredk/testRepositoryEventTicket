package bll;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.oned.Code128Writer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class BarCodeGenerator {
    public static byte[] generateBarcode(String number) throws WriterException, IOException {
        int width = 400;
        int height = 150;

        // Combine the number with a unique UUID to ensure authenticity
        String data = number + "-" + generateUniqueString();

        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(data, BarcodeFormat.CODE_128, width, height);

        BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(barcodeImage, "png", baos);

        return baos.toByteArray();
    }

    // Method to create a unique code in UUID format
    private static String generateUniqueString() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
