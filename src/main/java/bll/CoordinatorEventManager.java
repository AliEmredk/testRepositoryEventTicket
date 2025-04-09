package bll;

import dal.CoordinatorEventDAO;

public class CoordinatorEventManager {

    private final CoordinatorEventDAO coordinatorEventDAO = new CoordinatorEventDAO();

    public void assignCoordinatorToEventByNames(String coordinatorName, String eventName) {
        coordinatorEventDAO.assignCoordinatorToEventByNames(coordinatorName, eventName);
    }

    public void assignCoordinatorToEventById(int userId, int eventId) {
        coordinatorEventDAO.assignCoordinatorToEventById(userId, eventId);
    }

    public void assignCoordinatorToOwnEvents(String assigningUsername, String targetUsername, String eventName) {
        coordinatorEventDAO.assignCoordinatorToOwnEvents(assigningUsername, targetUsername, eventName);
    }
}
