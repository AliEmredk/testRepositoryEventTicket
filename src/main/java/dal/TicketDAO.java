package dal;

import be.Ticket;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {
        private final DBAccess dbAccess = new DBAccess();

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.TicketId, t.TicketType, c.CustomerId, c.FirstName, c.LastName, c.Email," +
                "e.EventId, e.EventName, e.Location, e.Date, e.StartTime, e.EndTime, e.Note, " +
                "b.BarcodeId, b.Barcode_Number " +
                "FROM Ticket t " +
                "JOIN Customer c ON t.CustomerId = c.CustomerId " +
                "JOIN Event e ON t.EventId = e.EventId " +
                "JOIN Barcode b ON t.BarcodeId = b.BarcodeId ";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ticket ticket = new Ticket(
                        rs.getInt("TicketId"),
                        rs.getString("TicketType"),
                        rs.getInt("BarcodeId"),
                        rs.getString("Barcode_Number"),
                        rs.getInt("EventId"),
                        rs.getString("EventName"),
                        rs.getString("Location"),
                        rs.getString("Date"),
                        rs.getString("StartTime"),
                        rs.getString("EndTime"),
                        rs.getString("EventNote"),
                        rs.getInt("CustomerId"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email")
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

