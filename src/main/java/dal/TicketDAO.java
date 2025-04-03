package dal;

import be.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {
        private final DBAccess dbAccess = new DBAccess();

        private Connection connection;

        public TicketDAO(Connection connection) {
            this.connection = connection;
        }

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.TicketId, t.TicketType, c.CustomerId, c.FirstName, c.LastName, c.Email," +
                "e.EventId, e.EventName, e.Location, e.Date, e.StartTime, e.EndTime, e.Note, " +
                "b.BarcodeId, b.BarcodeImage, b.BarcodeString " +
                "FROM Ticket t " +
                "JOIN Customer c ON t.CustomerId = c.CustomerId " +
                "JOIN Event e ON t.EventId = e.EventId " +
                "JOIN Barcode b ON t.BarcodeId = b.BarcodeId ";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int ticketId = rs.getInt("TicketId");
                String ticketType = rs.getString("TicketType");
                int barcodeId = rs.getInt("BarcodeId");
                byte[] barcodeImage = rs.getBytes("BarcodeImage");
                String barcodeString = rs.getString("BarcodeString");
                int eventId = rs.getInt("EventId");
                String eventName = rs.getString("EventName");
                String location = rs.getString("Location");
                String date = rs.getString("Date");
                String startTime = rs.getString("StartTime");
                String endTime = rs.getString("EndTime");
                String eventNote = rs.getString("Note");
                int customerId = rs.getInt("CustomerId");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String email = rs.getString("Email");

                Ticket ticket = new Ticket(
                        ticketId, ticketType, barcodeId, barcodeImage, barcodeString,
                        eventId, eventName, location, date, startTime, endTime, eventNote,
                        customerId, firstName, lastName, email
                );
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public boolean saveTicket(Ticket ticket) {
        String sql = "INSERT INTO Ticket (BarcodeId, CustomerId, TicketType, EventId) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1,ticket.getBarcodeId());
            stmt.setInt(2,ticket.getCustomerId());
            stmt.setString(3,ticket.getTicketType());
            stmt.setInt(4,ticket.getEventId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}