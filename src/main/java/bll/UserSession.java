package bll;

import be.User;

public class UserSession {
    private static String role;
    private static User loggedInUser;

    public static void setRole(String userRole) {
        role = userRole;
    }

    public static String getRole() {
        return role;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void clearSession() {
        role = null;
    }
}
