package dk.easv;

import be.Ticket;
import bll.TicketManager;
import dal.BarcodeDAO;
import dal.DBAccess;
import dal.TicketDAO;

import java.sql.Connection;

public class TicketTest {

    public static void main(String[] args) {
        try {
            DBAccess db = new DBAccess();
            Connection conn = db.DBConnection();
            TicketDAO ticketDAO = new TicketDAO(conn);
            BarcodeDAO barcodeDAO = new BarcodeDAO(conn);

            TicketManager ticketManager = new TicketManager(ticketDAO, barcodeDAO);

            Ticket newTicket = ticketManager.generateTicket(1, 2, "Normal");

            if (newTicket != null) {
                System.out.println("Ticket generated");
                System.out.println(newTicket);
            } else {
                System.out.println("Ticket could not be generated");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}