package dal;

import be.TicketType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketTypeDAO {
    private Connection conn;

    public TicketTypeDAO(Connection conn) {
        this.conn = conn;
    }

    public void createTicketType(TicketType ticketType) throws SQLException {
        String sql = "INSERT INTO TicketType (Name) VALUES (?)";
        try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, ticketType.getTicketTypeName());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    ticketType.setTicketTypeId(generatedId);
                }
            }
        }
    }

    public List<TicketType> getAllTicketTypes() throws SQLException {
        List<TicketType> ticketTypes = new ArrayList<TicketType>();
        String sql = "SELECT * FROM TicketType";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int ticketTypeId = rs.getInt("TicketTypeId");
                String ticketTypeName = rs.getString("Name");

                TicketType ticketType = new TicketType(ticketTypeId, ticketTypeName);
                ticketTypes.add(ticketType);
            }
        }
        return ticketTypes;
    }

    public void updateTicketType(TicketType ticketType) throws SQLException {
        String sql = "UPDATE TicketType SET Name = ? WHERE TicketTypeId = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, ticketType.getTicketTypeName());
            statement.setInt(2, ticketType.getTicketTypeId());
            statement.executeUpdate();
        }
    }

    public void deleteTicketType(TicketType ticketType) throws SQLException {
        String sql = "DELETE FROM TicketType WHERE TicketTypeId = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, ticketType.getTicketTypeId());
            statement.executeUpdate();
        }
    }
}