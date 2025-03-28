package bll;

import be.Ticket;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import dal.TicketDAO;


    public class TicketManager {
        private TicketDAO ticketDAO; // Corrected

        public TicketManager(TicketDAO ticketDAO) {
            this.ticketDAO = ticketDAO;
        }

        public List<Ticket> getAllTickets() {
            return ticketDAO.getAllTickets();
        }




//        public Ticket generateTicket(int eventId, String eventName, String location, String ticketType, int price, int barcodeId) {
//            String uniqueId = UUID.randomUUID().toString();
//
//            String barcode = "";
//            Ticket ticket = new Ticket(eventId, eventName, location, ticketType, price, barcodeId);
//            try {
//                ticketDAO.saveTicket(ticket); //Class
//            } catch (SQLException e) {
//                e.printStackTrace(); // Manage errors
//            }
//            return ticket;
//        }

        public boolean saveTicket(Ticket ticket) {
            return ticketDAO.saveTicket(ticket);
        }

    }

