package dal;

import be.SpecialTicket;

import java.sql.*;

public class SpecialTicketDAO {

    private final Connection connection;

    public SpecialTicketDAO(Connection connection) {
        this.connection = connection;
    }

    public Integer saveSpecialTicket(SpecialTicket specialTicket) throws SQLException {
        String sql = "INSERT INTO SpecialTicket (Details, EventId, barcodeId) "
                + "VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, specialTicket.getSpecialTicketDetails());

            if (specialTicket.getEventId() != null) {
                statement.setInt(2, specialTicket.getEventId());
            } else {
                statement.setNull(2, Types.INTEGER);
            }

            statement.setInt(3, specialTicket.getBarcodeId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating special ticket failed");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    specialTicket.setSpecialTicketId(generatedId);
                    return generatedId;
                } else {
                    throw new SQLException("Creating special ticket failed");
                }
            }
        }
    }
}