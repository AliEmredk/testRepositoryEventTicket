package dal;

import be.CoordinatorAssignment;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
//----------------------- I am not sure we gonna need these two -----------------------------------------------
    public List<String> getCoordinatorUsernamesByEventId(int eventId) {
        List<String> coordinatorUsernames = new ArrayList<>();

        String sql = """
               SELECT li.Username
               FROM CoordinatorEvent ce
               JOIN LoginInfo li ON ce.UserId = li.UserId
               WHERE ce.EventId = ?
    """;

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    coordinatorUsernames.add(rs.getString("Username"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coordinatorUsernames;
    }

    // int the future I might created methods to get id by username

    /* try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                coordinatorUsernames.add(rs.getString("Username"));
            }  ask this*/

    public List<String> getEventNamesByIds (List<Integer> eventIds) {
        List<String> eventNames = new ArrayList<>();

        if (eventIds == null || eventIds.isEmpty()) {
            return eventNames;
        }

        String placeholders = eventIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql = "SELECT EventName FROM Event WHERE EventId IN (" + placeholders + ")";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < eventIds.size(); i++) {
                stmt.setInt(i + 1, eventIds.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    eventNames.add(rs.getString("EventName"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return eventNames;
    }
// --------------------------------------------------------------------------------------------



    public String getCoordinatorUsernameByUserId(int userId) {
        String username = null;
        String sql = """
                SELECT li.Username
                FROM CoordinatorEvent ce
                JOIN LoginInfo li ON ce.UserId = li.UserId
                WHERE ce.UserId = ?
                """;

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1,userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    username = rs.getString("Username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return username;
    }

    // to get all the assigned eventCoordinators usernames
    public List<String> getAllAssignedCoordinatorUsernames() {
        List<String> usernames = new ArrayList<>();

        String sql = """
                SELECT DISTINCT li.Username
                FROM CoordinatorEvent ce
                JOIN LoginInfo li ON ce.UserId = li.UserId
                """;

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usernames.add(rs.getString("Username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usernames;
    }

    // this gonna create a list with event coodinators and events that they are assigned
    public List<CoordinatorAssignment> getAllCoordinatorAssignments() {
        List<CoordinatorAssignment> assignments = new ArrayList<>();

        String sql = """
                SELECT li.Username, e.EventName AS EventName
                FROM CoordinatorEvent ce
                JOIN LoginInfo li ON ce.UserId = li.UserId
                JOIN Event e ON ce.EventId = e.EventId
                """;

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("Username");
                String eventName = rs.getString("Eventname");

                assignments.add(new CoordinatorAssignment(username, eventName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignments;
    }

    //----------- You'll probably need these two methods. I talked with Jennifer and then created these
    // you can assign event coordinator by just using their name
    // this method gonna take the Username and return its id
    public int getUserIdByUsername(String username) {
        String sql = "SELECT UserId FROM LoginInfo WHERE Username = ?";
        int userId = -1;

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                userId = rs.getInt("UserId");
            }
        }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }
    //if you need to reach id of events from their name talk with EMRE
    public void assignCoordinatorToEvent(String username, int eventId) {
        int userId = getUserIdByUsername(username);
        if (userId == -1) {
            System.out.println("User not found: " + username);
        }

        //to prevent duplication of assignments
        if (isCoordinatorAlreadyAssigned(userId, eventId)) {
            System.out.println(username + "is already assigned to event ID " + eventId);
            return;
        }

        String sql = "INSERT INTO CoordinatorEvent (UserId, EventId) VALUES (?, ?)";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2,eventId);

            stmt.executeUpdate();
            System.out.println("Assigned " + username + " to event ID " + eventId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // this is for prevent to duplicate assignments
    private boolean isCoordinatorAlreadyAssigned(int userId, int eventId) {
        String sql = "SELECT * FROM CoordinatorEvent WHERE UserId = ? AND EventId = ?";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getEventIdByEventName(String eventName) {
        String sql = "SELECT EventId from Event WHERE EventName = ?";
        int eventId = -1;

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, eventName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    eventId = rs.getInt("EventId");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventId;
    }

    //YOU SHOULD USE THIS!!!!!!!!!!!!!!!!!!!!!!1
    //example of using it: assignCoordinatorToEventByNames(Erla, Poker Night)
    public void assignCoordinatorToEventByNames(String username, String eventName) {
        int userId = getUserIdByUsername(username);
        int eventId = getEventIdByEventName(eventName);

        if (userId == -1) {
            System.out.println("User not found: " + username);
            return;
        }

        if (eventId == -1) {
            System.out.println("Event not found " + eventName);
            return;
        }

        if (isCoordinatorAlreadyAssigned(userId, eventId)) {
            System.out.println(username + " is already assigned to '" + eventName + "'");
            return;
        }

        String sql = "INSERT INTO CoordinatorEvent (UserId, EventId) VALUES (?, ?)";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            stmt.executeUpdate();

            System.out.println("Assigned " + username + " to event: " + eventName);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
