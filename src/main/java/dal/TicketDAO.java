package dal;

import be.Ticket;
import be.TicketType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {
    private final DBAccess dbAccess = new DBAccess();
    private final Connection connection;

    public TicketDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.TicketId, t.Discount, t.Details, tt.TicketTypeId, tt.Name AS TicketTypeName, " +
                "c.CustomerId, c.FirstName, c.LastName, c.Email, " +
                "e.EventId, e.EventName, e.Location, e.Date, e.StartTime, e.EndTime, e.Note, " +
                "b.BarcodeId, b.BarcodeImage, b.BarcodeString " +
                "FROM Ticket t " +
                "JOIN TicketType tt ON t.TicketTypeId = tt.TicketTypeId " +
                "JOIN Customer c ON t.CustomerId = c.CustomerId " +
                "JOIN Event e ON t.EventId = e.EventId " +
                "JOIN Barcode b ON t.BarcodeId = b.BarcodeId";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int ticketId = rs.getInt("TicketId");

                // Create TicketType object
                int ticketTypeId = rs.getInt("TicketTypeId");
                String ticketTypeName = rs.getString("TicketTypeName");
                TicketType ticketType = new TicketType(ticketTypeId, ticketTypeName);

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

                int discount = rs.getInt("Discount");
                String details = rs.getString("Note");

                Ticket ticket = new Ticket(
                        ticketId, ticketType, barcodeId, barcodeImage, barcodeString,
                        eventId, eventName, location, date, startTime, endTime, eventNote,
                        customerId, firstName, lastName, email,
                        discount, details
                        );
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    public boolean saveTicket(Ticket ticket) {
        String sql = "INSERT INTO Ticket (BarcodeId, CustomerId, TicketTypeId, EventId, Discount, Details) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticket.getBarcodeId());
            stmt.setInt(2, ticket.getCustomerId());
            stmt.setInt(3, ticket.getTicketType().getTicketTypeId()); // Using object
            stmt.setInt(4, ticket.getEventId());
            stmt.setInt(5, ticket.getDiscount());
            stmt.setString(6, ticket.getDetails());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}