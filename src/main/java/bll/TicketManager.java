package bll;

import be.Ticket;
import java.sql.SQLException;
import java.util.UUID;
import dal.TicketDAO;


    public class TicketManager {
        private TicketDAO ticketDAO; // Corrected

        public TicketManager(TicketDAO ticketDAO) {
            this.ticketDAO = ticketDAO;
        }


        public Ticket generateTicket(String userId) {
            String uniqueId = UUID.randomUUID().toString();

            String barcode = "";
            Ticket ticket = new Ticket(userId,barcode);
            try {
                ticketDAO.saveTicket(ticket); //Class
            } catch (SQLException e) {
                e.printStackTrace(); // Manage errors
            }
            return ticket;
        }
    }

