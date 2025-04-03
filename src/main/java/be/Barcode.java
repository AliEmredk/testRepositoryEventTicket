package be;

public class Barcode {

    private int barcodeId; // Auto-incremented ID from the database
    private byte[] barcodeImage; // Barcode image stored as a byte array (VARBINARY)
    private String barcodeString;

    public Barcode(byte[] barcodeImage) {
        this.barcodeImage = barcodeImage;
    }

    public Barcode(int barcodeId, byte[] barcodeImage, String barcodeString) {
        this.barcodeId = barcodeId;
        this.barcodeImage = barcodeImage;
        this.barcodeString = barcodeString;
    }

    // Getters and Setters
    public int getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(int barcodeId) {
        this.barcodeId = barcodeId;
    }

    public byte[] getBarcodeImage() {
        return barcodeImage;
    }

    public void setBarcodeImage(byte[] barcodeImage) {
        this.barcodeImage = barcodeImage;
    }

    public String getBarcodeString() {
        return barcodeString;
    }

    public void setBarcodeString(String barcodeString) {
        this.barcodeString = barcodeString;
    }

    @Override
    public String toString() {
        return "Barcode{" +
                "barcodeId=" + barcodeId +
                ", barcodeString='" + barcodeString + '\'' +
                ", barcodeSize=" + (barcodeImage != null ? barcodeImage.length : 0) + " bytes" +
                '}';
    }
}
