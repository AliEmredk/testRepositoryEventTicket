package bll;

import be.Barcode;
import be.Ticket;
import be.TicketType;
import com.google.zxing.WriterException;
import dal.BarcodeDAO;
import dal.TicketDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class TicketManager {
    private final TicketDAO ticketDAO;
    private final BarcodeDAO barcodeDAO;

    public TicketManager(TicketDAO ticketDAO, BarcodeDAO barcodeDAO) {
        this.ticketDAO = ticketDAO;
        this.barcodeDAO = barcodeDAO;
    }

    public List<Ticket> getAllTickets() {
        return ticketDAO.getAllTickets();
    }

    // Updated to accept TicketType object instead of String
    public Ticket generateTicket(int customerId, int eventId, TicketType ticketType) {
        try {
            String barcodeString = UUID.randomUUID().toString().substring(0, 12).toUpperCase();
            byte[] barcodeImage = BarCodeGenerator.generateBarcode(barcodeString);

            Barcode barcode = new Barcode(0, barcodeImage, barcodeString);
            int barcodeId = barcodeDAO.saveBarcode(barcode);

            if (barcodeId <= 0) {
                System.out.println("Failed to save barcode, can't generate ticket");
                return null;
            }

            System.out.println("Barcode " + barcodeId + " generated");

            Ticket ticket = new Ticket();
            ticket.setBarcodeImage(barcodeImage);
            ticket.setBarcodeId(barcodeId);
            ticket.setBarcodeString(barcodeString);
            ticket.setCustomerId(customerId);
            ticket.setEventId(eventId);
            ticket.setTicketType(ticketType);

            ticketDAO.saveTicket(ticket);
            return ticket;

        } catch (SQLException | IOException | WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean saveTicket(Ticket ticket) {
        return ticketDAO.saveTicket(ticket);
    }
}