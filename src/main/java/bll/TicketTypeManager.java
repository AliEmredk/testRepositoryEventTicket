package bll;

import be.TicketType;
import dal.TicketTypeDAO;

import java.sql.SQLException;
import java.util.List;

public class TicketTypeManager {
    private final TicketTypeDAO ticketTypeDAO;

    public TicketTypeManager(TicketTypeDAO ticketTypeDAO) {
        this.ticketTypeDAO = ticketTypeDAO;
    }

    public void createTicketType(TicketType ticketType) throws SQLException {
        ticketTypeDAO.createTicketType(ticketType);
    }

    public List<TicketType> getAllTicketTypes() throws SQLException {
        return ticketTypeDAO.getAllTicketTypes();
    }

    public void updateTicketType(TicketType ticketType) throws SQLException {
        ticketTypeDAO.updateTicketType(ticketType);
    }

    public void deleteTicketType(TicketType ticketType) throws SQLException {
        ticketTypeDAO.deleteTicketType(ticketType);
    }
}