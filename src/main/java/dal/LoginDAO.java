package dal;

import be.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {

    private DBAccess con = new DBAccess();

    public User getUserByUserName(String username) {
        User user = null;

        try (Connection c = con.DBConnection();) {
            String sql = "SELECT Username, Password, Role FROM LoginInfo WHERE Username = ?";
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String dbUsername = rs.getString("Username");
                String dbPassword = rs.getString("Password");
                String dbRole = rs.getString("Role");

                user = new User(dbUsername, dbPassword, dbRole);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}