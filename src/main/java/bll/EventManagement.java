package bll;

import dal.EventDAO;
import be.Event;

import java.util.List;

public class EventManagement {

    private final EventDAO eventDAO = new EventDAO();

    public void createEvent(String location, String date, String startTime, String endTime, String note, int price, String location_Guidance, String eventName) {

        Event event = new Event(location, date, startTime, endTime, note, price, location_Guidance, eventName);
        eventDAO.createEvent(event);
    }

    public void deleteEvent(String eventName) {
        eventDAO.deleteEvent(eventName);
    }

    public void assignCoordinatorToEvent(int eventId, int userId) {
        eventDAO.assignCoordinatorToEvent(eventId, userId);
    }

    public void removeCoordinatorFromEvent(int eventId, int userId) {
        eventDAO.removeCoordinatorFromEvent(eventId, userId);
    }

    public void getAllChanges() {
        eventDAO.getAllChanges();
    }

    public List<Event> getAllEvents() {
        return eventDAO.getAllEvents();
    }

}
