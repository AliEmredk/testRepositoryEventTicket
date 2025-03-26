package bll;

import be.User;
import dal.UserDAO;

import java.util.List;

public class UserManagement {

    private final UserDAO userDAO = new UserDAO();

    public void createAdmin(String username, String password) {
        User admin = new User(username, password, "Admin");
        userDAO.addUser(admin);
    }

    public void createEventCoordinator(String username, String password) {
        User coordinator = new User(username, password, "Event Coordinator");
        userDAO.addUser(coordinator);
    }

    public User getUser(String username) {
        return userDAO.getUserByUsername(username);
    }

    public void deleteUser(String username) {
        userDAO.deleteUser(username);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public List<User> getAllEventCoordinators() {
        return userDAO.getAllEventCoordinators();
    }


}
