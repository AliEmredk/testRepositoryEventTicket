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

    public boolean addUser(User user) {
        String checkSql = "SELECT COUNT(*) FROM LoginInfo WHERE Username = ?";
        String insertSql = "INSERT INTO LoginInfo (Username, Password, Role, ProfileImagePath) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbAccess.DBConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, user.getUsername());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Username already exists");
                    return false;
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, user.getUsername());
                insertStmt.setString(2, user.getPassword());
                insertStmt.setString(3, user.getRole());
                insertStmt.setString(4, user.getProfileImagePath());

                insertStmt.executeUpdate();
                System.out.println("User added successfully");
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM LoginInfo WHERE Username = ?";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getString("ProfileImagePath")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM LoginInfo";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                userList.add(new User(
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getString("ProfileImagePath")
                ));
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

    public List<User> getAllEventCoordinators() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM LoginInfo WHERE Role = 'Event Coordinator'";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                userList.add(new User(
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getString("ProfileImagePath")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    public void updatePassword(User user) {
        String sql = "UPDATE LoginInfo SET Password = ? WHERE Username = ?";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getPassword());
            stmt.setString(2, user.getUsername());

            stmt.executeUpdate();
            System.out.println("Password updated for user: " + user.getUsername());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateUser(User updatedUser, String originalUsername) {
        String checkSql = "SELECT COUNT(*) FROM LoginInfo WHERE Username = ? AND Username != ?";
        String updateSql = "UPDATE LoginInfo SET Username = ?, Password = ?, Role = ?, ProfileImagePath = ? WHERE Username = ?";

        try (Connection conn = dbAccess.DBConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, updatedUser.getUsername());
                checkStmt.setString(2, originalUsername);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Username already exists");
                    return false;
                }
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, updatedUser.getUsername());
                updateStmt.setString(2, updatedUser.getPassword());
                updateStmt.setString(3, updatedUser.getRole());
                updateStmt.setString(4, updatedUser.getProfileImagePath());
                updateStmt.setString(5, originalUsername);

                updateStmt.executeUpdate();
                System.out.println("User updated in database: " + originalUsername + " → " + updatedUser.getUsername());
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

