package dal;

import be.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

    public class TicketDAO {
        private Connection connection;

        // Constructor
        public void TicketDAO(Connection connection) {
            this.connection = connection;
        }

        public void saveTicket(Ticket ticket) throws SQLException {
            if (connection == null) {
                throw new SQLException("it is not possible to connect: conection NULL");
            }

            String sql = "INSERT INTO tickets (id, user_id, barcode) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, ticket.getId());
                stmt.setString(2, ticket.getUserId());
                stmt.setString(3, ticket.getBarcode());
                stmt.executeUpdate();
            }
        }
    }

