package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CoordinatorEventDAO {
    private final DBAccess dbAccess = new DBAccess();

    public void assignCoordinatorToEvent(int userId, int eventId) {
        String sql = "INSERT INTO CoordinatorEvent (UserId, EventId) VALUES (?, ?)";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2,eventId);
            stmt.executeUpdate();
            System.out.println("Coordinator assigned to event successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
