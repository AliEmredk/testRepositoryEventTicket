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

        if(isUsernameAlreadyUsed(user.getUsername())) {
            System.out.println("Username already used!");
            return false;
        }

        String sql = "INSERT INTO LoginInfo (Username, Password, Role, ProfileImagePath) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getProfileImagePath());

            stmt.executeUpdate();
            System.out.println("User added successfully");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isUsernameAlreadyUsed(String username) {
        String sql = "SELECT * FROM LoginInfo WHERE Username = ?";

        try (Connection conn = dbAccess.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1,username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM LoginInfo WHERE Username = ?";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("UserId"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getString("ProfileImagePath"),
                        rs.getBytes("ProfileImage")
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
                        rs.getInt("UserId"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getString("ProfileImagePath"),
                        rs.getBytes("ProfileImage")
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
                        rs.getInt("UserId"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getString("ProfileImagePath"),
                        rs.getBytes("ProfileImage")
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

    public void updateProfileImage(int userId, byte[] imageData) throws SQLException {
        String sql = "UPDATE LoginInfo SET ProfileImage = ? WHERE UserId = ?";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBytes(1, imageData);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public byte[] getProfileImage(int userId) throws SQLException {
        String sql = "SELECT ProfileImage FROM LoginInfo WHERE UserId = ?";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBytes("ProfileImage");
            }
        }
        return null;
    }

    public void updateUser(User updatedUser, String originalUsername) {
        String sql = "UPDATE LoginInfo SET Username = ?, Password = ?, Role = ?, ProfileImagePath = ? WHERE Username = ?";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, updatedUser.getUsername());
            stmt.setString(2, updatedUser.getPassword());
            stmt.setString(3, updatedUser.getRole());
            stmt.setString(4, updatedUser.getProfileImagePath());
            stmt.setString(5, originalUsername);

            stmt.executeUpdate();
            System.out.println("User updated in database: " + originalUsername + " → " + updatedUser.getUsername());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

