package controllers;

import be.Event;
import be.User;
import dal.CoordinatorEventDAO;
import dal.EventDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EventMainController {

    
    private EventDAO eventDAO = new EventDAO();
    private ObservableList<Event> events = FXCollections.observableArrayList();
    private int currentUserId;
    private String currentUserRole;
    private User loggedInUser;

    public void setCurrentUser(int userId, String role) {
        this.currentUserId = userId;
        this.currentUserRole = role;
        loadEvents();
    }

    // Constructor to initialize events
    public EventMainController(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    // Load all events from the database
    public void loadEvents() {
        if(loggedInUser == null) {
            System.out.println("⚠ User is null");
            return;
        }

        List<Event> events;
        if("Admin".equals(loggedInUser.getRole())) {
            events = eventDAO.getAllEvents();
        } else if ("Event Coordinator".equals(loggedInUser.getRole())) {
            CoordinatorEventDAO coordinatorEventDAO = new CoordinatorEventDAO();
            events = coordinatorEventDAO.getEventsByCoordinatorId(loggedInUser.getUser_Id());
        } else {
            events = new ArrayList<>();
        }

        System.out.println("Loading " + events.size() + " events for " + loggedInUser.getRole());
        this.events.setAll(events);
    }

    // Handle searching
    public void searchEvents(String query) {
        List<Event> filteredEvents = eventDAO.getAllEvents().stream()
                .filter(event -> event.getEventName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        events.clear();
        events.addAll(filteredEvents);
    }

    // Handle sorting
    public void sortEvents(String criteria) {
        switch (criteria) {
            case "Sort by Date":
                events.sort((e1, e2) -> e1.getDate().compareTo(e2.getDate()));
                break;
            case "Sort by Price":
                events.sort((e1, e2) -> Integer.compare(e1.getPrice(), e2.getPrice()));
                break;
            case "Sort by Location":
                events.sort((e1, e2) -> e1.getLocation().compareTo(e2.getLocation()));
                break;
        }
    }

    // Handle filtering
    public void filterEvents(String filter) {
        List<Event> allEvents = eventDAO.getAllEvents(); // Start from the entire list
        LocalDate today = LocalDate.now(); // Get today's date
        Month currentMonth = today.getMonth(); // Get what month it is today

        List<Event> filteredList = allEvents.stream()
                .filter(event -> {
                    switch (filter) {
                        case "Free Events":
                            return event.getPrice() == 0;
                        case "Paid Events":
                            return event.getPrice() > 0;
                        case "Today’s Events":
                            return event.getDate().equals(today.toString()); // Match today's date
                        case "This Month's Events":
                            return LocalDate.parse(event.getDate()).getMonth() == currentMonth; // Match the month
                        default:
                            return true; // No filter applied

                    }
                })
                .collect(Collectors.toList());

        events.setAll(filteredList); // Update observable list with the filter results
    }

    // Get filtered events
    public ObservableList<Event> getFilteredEvents() {
        return events;
    }

    // Get all events for testing purposes
    public ObservableList<Event> getAllEvents() {
        return events;
    }


}
