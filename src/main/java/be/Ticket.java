package be;

import java.util.Random;
import java.util.UUID;

public class Ticket {
    private String id; //Unique Id
    private String userId; // User to generated ticket
    private String barcode; // BarCode format 128

    // Constructor
    public Ticket(String id, String userId) {
        this.id = generateUniqueId();
        this.userId = userId;
        this.barcode = generateBarcode();
    }

    // One unique Code Bar with Random number
    private String generateUniqueId() {
        String uuidPart = UUID.randomUUID().toString().substring(0, 8); // 8 typos UUID
        int randomPart = new Random().nextInt(10000); // Random
        return uuidPart + "-" + randomPart;
    }

    // BarCode format E128
    private String generateBarcode() {
        Random random = new Random();
        StringBuilder barcodeBuilder = new StringBuilder();

        // Code 128 Alphanumeric
        for (int i = 0; i < 12; i++) { // Code 128 used 12 caractheres
            int digit = random.nextInt(10); // Number from 0 to 9
            barcodeBuilder.append(digit);
        }
        return barcodeBuilder.toString();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getBarcode() {
        return barcode; // Valid format CodeBar
    }
}