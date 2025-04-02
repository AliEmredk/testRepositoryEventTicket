package dal;

import be.Barcode;
import java.sql.*;

public class BarcodeDAO {

    private final Connection connection;

    public BarcodeDAO(Connection connection) {
        this.connection = connection;
    }

    public int saveBarcode(Barcode barcode) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connection is NULL");
        }

        if (barcode.getBarcodeImage() == null) {
            System.out.println("Error: Barcode data is null.");
        } else {
            System.out.println("Barcode data size: " + barcode.getBarcodeImage().length + " bytes");
        }

        String sql = "INSERT INTO Barcode (BarcodeImage, BarcodeString) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBytes(1, barcode.getBarcodeImage());
            stmt.setString(2, barcode.getBarcodeString());
            stmt.executeUpdate();

            // Retrieve generated keys (BarcodeId)
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    barcode.setBarcodeId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    public Barcode getBarcodeById(int barcodeId) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connection is NULL");
        }

        String sql = "SELECT BarcodeId, BarcodeImage, BarcodeString FROM Barcode WHERE BarcodeId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, barcodeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("BarcodeId");
                    byte[] image = rs.getBytes("BarcodeImage");
                    String string = rs.getString("BarcodeString");

                    return new Barcode(id, image, string);
                }
            }
        }
        return null;
    }
}