package bll;

import be.User;

public class UserSession {
    private static String role;

    public static void setRole(String userRole) {
        role = userRole;
    }

    public static String getRole() {
        return role;
    }

    public static void clearSession() {
        role = null;
    }
}
