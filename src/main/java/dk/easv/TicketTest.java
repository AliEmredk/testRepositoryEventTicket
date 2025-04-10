package dk.easv;

import be.Ticket;
import be.TicketType;
import bll.TicketManager;
import dal.BarcodeDAO;
import dal.DBAccess;
import dal.TicketDAO;
import dal.TicketTypeDAO;

import java.sql.Connection;
import java.util.List;

public class TicketTest {

    public static void main(String[] args) {
        try {
            DBAccess db = new DBAccess();
            Connection conn = db.DBConnection();

            TicketDAO ticketDAO = new TicketDAO(conn);
            BarcodeDAO barcodeDAO = new BarcodeDAO(conn);
            TicketTypeDAO ticketTypeDAO = new TicketTypeDAO(conn);

            TicketManager ticketManager = new TicketManager(ticketDAO, barcodeDAO);

            // Get all ticket types and find one named "Normal"
            List<TicketType> ticketTypes = ticketTypeDAO.getAllTicketTypes();
            TicketType normalType = ticketTypes.stream()
                    .filter(t -> t.getTicketTypeName().equalsIgnoreCase("Normal"))
                    .findFirst()
                    .orElse(null);

            if (normalType == null) {
                System.out.println("Ticket type 'Normal' not found.");
                return;
            }

            // Generate ticket with that TicketType object
            Ticket newTicket = ticketManager.generateTicket(1, 2, normalType);

            if (newTicket != null) {
                System.out.println("Ticket generated:");
                System.out.println(newTicket);
            } else {
                System.out.println("Ticket could not be generated.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}