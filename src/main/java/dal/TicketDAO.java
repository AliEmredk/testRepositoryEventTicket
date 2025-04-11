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
        String sql = "SELECT t.TicketId, tt.TicketTypeId, tt.Name AS TicketTypeName, " +
                "c.CustomerId, c.FirstName, c.LastName, c.Email, " +
                "e.EventId, e.EventName, e.Location, e.Date, e.StartTime, e.EndTime, e.Note, " +
                "b.BarcodeId, b.BarcodeImage, b.BarcodeString " +
                "t.Discount " +
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

                Ticket ticket = new Ticket(
                        ticketId, ticketTypeName, barcodeId, barcodeImage, barcodeString,
                        eventId, eventName, location, date, startTime, endTime, eventNote,
                        customerId, firstName, lastName, email, discount
                );
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    public boolean saveTicket(Ticket ticket) {
        int ticketTypeId = getTicketTypeId(ticket.getTicketType());

        if (ticketTypeId == -1) {
            System.out.println("Invalid Ticket Type.");
            return false;
        }
        String sql = "INSERT INTO Ticket (BarcodeId, CustomerId, TicketTypeId, EventId, Discount) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticket.getBarcodeId());
            stmt.setInt(2, ticket.getCustomerId());
            stmt.setInt(3, ticketTypeId);
            stmt.setInt(4, ticket.getEventId());
            stmt.setInt(5, ticket.getDiscount());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getTicketTypeId(String ticketTypeName) {
        String sql = "SELECT TicketTypeId FROM TicketType WHERE Name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ticketTypeName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("TicketTypeId");
            } else {
                return -1;  // Return -1 if no matching ticket type found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;  // Return -1 on error
        }
    }
}