package dal;

import be.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final DBAccess dbAccess = new DBAccess();

    public void addUser(User user) {
        String sql = "INSERT INTO LoginInfo (Username, Password, Role) VALUES (?, ?, ?)";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());

            stmt.executeUpdate();
            System.out.println("User added successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM LoginInfo WHERE Username = ?";

        try (Connection conn = dbAccess.DBConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getString("Username"),
                                rs.getString("Password"),
                                rs.getString("Role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Retrieve all users
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM LoginInfo";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                userList.add(new User(rs.getString("Username"), rs.getString("Password"), rs.getString("Role")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    
    public void deleteUser(String username) {
        String sql = "DELETE FROM LoginInfo WHERE Username = ?";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.executeUpdate();
            System.out.println("User deleted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //list all the event coordinators
    public List<User> getAllEventCoordinators() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM LoginInfo WHERE Role = 'Event Coordinator'";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                userList.add(new User(rs.getString("Username"), rs.getString("Password"),rs.getString("Role")));
            }
        }   catch (SQLException e) {
            e.printStackTrace();
        }
            return userList;

    }
}
