package bll;

import be.Barcode;
import be.SpecialTicket;
import com.google.zxing.WriterException;
import dal.BarcodeDAO;
import dal.SpecialTicketDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class SpecialTicketManager {
    private SpecialTicketDAO specialTicketDAO;
    private BarcodeDAO barcodeDAO;

    public SpecialTicketManager(SpecialTicketDAO specialTicketDAO, BarcodeDAO barcodeDAO) {
        this.specialTicketDAO = specialTicketDAO;
        this.barcodeDAO = barcodeDAO;
    }

    public SpecialTicket generateSpecialTicket(Optional<Integer> eventId, String specialTicketDetails) throws SQLException, IOException, WriterException {
        String barcodeString = UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        byte[] barcodeImage = BarCodeGenerator.generateBarcode(barcodeString);

        Barcode barcode = new Barcode(0, barcodeImage, barcodeString);
        int barcodeId = barcodeDAO.saveBarcode(barcode);

        if (barcodeId <= 0) {
            throw new SQLException("Failed to save barcode, can't generate special ticket");
        }

        System.out.println("Barcode " + barcodeId + " generated");

        SpecialTicket specialTicket = new SpecialTicket(
                null,
                specialTicketDetails,
                eventId.orElse(null),
                barcodeImage,
                barcodeId,
                barcodeString
        );

        specialTicketDAO.saveSpecialTicket(specialTicket);

        return specialTicket;
    }
}