package controllers;

import be.Event;
import dal.EventDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.Month;

import java.util.List;
import java.util.stream.Collectors;

public class EventMainController {

    private EventDAO eventDAO = new EventDAO();
    private ObservableList<Event> events = FXCollections.observableArrayList();
    private int currentPage = 0;
    private final int itemsPerPage = 5;

    // Constructor to initialize events
    public EventMainController() {
        loadEvents();
    }

    // Load all events from the database
    public void loadEvents() {
        List<Event> allEvents = eventDAO.getAllEvents();
        events.clear();
        events.addAll(allEvents);
    }

    // Get the events displayed on the current page
    public ObservableList<Event> getEventsForCurrentPage() {
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, events.size());
        return FXCollections.observableArrayList(events.subList(startIndex, endIndex));
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

    // Pagination: next page
    public void nextPage() {
        if ((currentPage + 1) * itemsPerPage < events.size()) {
            currentPage++;
        }
    }

    // Pagination: previous page
    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
        }
    }

    // Get filtered events
    public ObservableList<Event> getFilteredEvents() {
        return getEventsForCurrentPage();
    }

    // Get the total number of pages
    public int getTotalPages() {
        return (int) Math.ceil((double) events.size() / itemsPerPage);
    }

    // Get all events for testing purposes
    public ObservableList<Event> getAllEvents() {
        return events;
    }
}
