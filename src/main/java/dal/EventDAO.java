package dal;

import be.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    private final DBAccess dbAccess = new DBAccess();

    public void createEvent(Event event) {
        String sql = "INSERT INTO Event (Location, Date, StartTime, EndTime, Note, Price, Location_Guidance, EventName) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getLocation());
            stmt.setString(2, event.getDate());
            stmt.setString(3, event.getStartTime());
            stmt.setString(4, event.getEndTime());
            stmt.setString(5,event.getNote());
            stmt.setInt(6,event.getPrice());
            stmt.setString(7,event.getLocation_Guidance());
            stmt.setString(8,event.getEventName());

            stmt.executeUpdate();
            System.out.println("Event added successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(String eventName){

        String sql = "DELETE FROM Event WHERE EventName = ?";

        try(Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1,eventName);
            int rowsAffected = stmt.executeUpdate();


            if (rowsAffected > 0) {
                logChange("Event", "DELETE",null, null);
                System.out.println("Event deleted successfully :)");
            } else {
                System.out.println("No event found with that name.");
            }
        }   catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        String sql = "SELECT * FROM Event";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Event event = new Event(
                rs.getString("Location"),
                rs.getString("Date"),
                rs.getString("StartTime"),
                rs.getString("EndTime"),
                rs.getString("Note"),
                rs.getInt("Price"),
                rs.getString("Location_Guidance"),
                rs.getString("EventName")
                );
                eventList.add(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return eventList;
    }

    public void assignCoordinatorToEvent(int eventId, int userId) {
        // to check EventId and UserId before assigning
        if (!eventExists(eventId)) {
            System.out.println("Error: EventId " + eventId + " does not exist");
            return;
        }

        if (!userExists(userId)) {
            System.out.println("Error: UserId " + userId + " does not exist");
            return;
        }

        if (isCoordinatorAssigned(eventId, userId)) {
            System.out.println("Warning: UserId " + userId + " is already assigned to EventId " + eventId);
            return;
        }

        String sql = "INSERT INTO EventUser (EventId, UserId) VALUES (?, ?)";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

            logChange("EventUser", "INSERT", eventId, userId);
            System.out.println("Assigned coordinator to event successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeCoordinatorFromEvent(int eventId, int userId) {
        // check if the coordinator is assigned
        if (!isCoordinatorAssigned(eventId, userId)) {
            System.out.println("Warning: UserId " + userId + " is not assigned to EventId " + eventId);
            return;
        }

        String sql = "DELETE FROM EventUser WHERE EventId = ? AND UserId = ?";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logChange("EventUser", "DELETE", eventId, userId);
                System.out.println("Successfully removed UserId " + userId + " from EventId " + eventId);
            } else {
                System.out.println("Failed to remove UserId " + userId + " from EventId " + eventId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //These three methods belong to assignCoordinatorToEvent
    // DON'T TOUCH THESE -_- I'll get mad!!!!
    //------------------------------------------------------------------------------------------
    private boolean eventExists(int eventId) {
        String sql = "SELECT 1 FROM Event WHERE EventId = ?";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            return stmt.executeQuery().next(); //If the rows exits, returns true

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean userExists(int userId) {
        String sql = "SELECT 1 FROM LoginInfo WHERE UserId = ?";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeQuery().next(); //returns if the row exists

        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isCoordinatorAssigned(int eventId, int userId) {
        String sql = "SELECT 1 FROM EventUser WHERE EventId = ? AND UserId = ?";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1,eventId);
            stmt.setInt(2,userId);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
    }
    }
    //--------------------------------------------------------------------------------------------------------
    // DON'T TOUCH THESE -_-


    //This method created to follow change on events. This is for ChangeLog table on the database
    //Please don't touch this :)
    private void logChange(String tableName, String actionType, Integer eventId, Integer userId) {
        String sql = "INSERT INTO ChangeLog (TableName, ActionType, EventId, UserId) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tableName);
            stmt.setString(2, actionType);
            if (eventId != null) {
                stmt.setInt(3, eventId);
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            if (userId != null) {
                stmt.setInt(4, userId);
            } else {
                stmt.setNull(4,java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();
            System.out.println("Changed logged: " + actionType + " on " + tableName);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //to view all the changes have been done
    public void getAllChanges() {
        String sql = "SELECT * FROM ChangeLog ORDER BY ChangeTimestamp DESC";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            System.out.println("=== Change History ===");
            while (rs.next()) {
                int logId = rs.getInt("LogId");
                String tableName = rs.getString("TableName");
                String actionType = rs.getString("ActionType");
                int eventId = rs.getInt("EventId");
                int userId = rs.getInt("UserId");
                Timestamp timestamp = rs.getTimestamp("ChangeTimestamp");

                System.out.println(logId + " | " + tableName + " | " + actionType + " | EventId: " + eventId + " | UserId: " + userId + " | " + timestamp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
