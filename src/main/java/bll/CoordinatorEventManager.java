package bll;

import dal.CoordinatorEventDAO;

public class CoordinatorEventManager {

    private final CoordinatorEventDAO coordinatorEventDAO = new CoordinatorEventDAO();

    public void assignCoordinatorToEventByNames(String coordinatorName, String eventName) {
        coordinatorEventDAO.assignCoordinatorToEventByNames(coordinatorName, eventName);
    }
}
