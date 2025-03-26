package bll;

import dal.LoginDAO;
import be.User;

public class LoginCheck {
    private LoginDAO loginDAO;

    public LoginCheck() {
        loginDAO = new LoginDAO();
    }

    public String checkLogin(String username, String password) {
        User user = loginDAO.getUserByUserName(username);

        if (user == null) {
            return "User not found";
        }

        if (!user.getPassword().equals(password)) {
            return "Wrong password";
        }

        String role = user.getRole();
        if (String.valueOf(role).equals("Admin")) {
            return "Admin";
        } else if (String.valueOf(role).equals("Event Coordinator")) {
            return "Event Coordinator";
        }
        return "Unknown";
    }
}
