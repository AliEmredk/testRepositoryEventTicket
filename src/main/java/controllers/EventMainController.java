package controllers;

import be.Event;
import bll.EventManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EventMainController {

    public Button btnEventCEdit;
    @FXML
    private ListView<String> eventsListView;
    @FXML
    private Button logoutButtonEC, getBtnEventCAdd, btnEventCSearch, btnNextPage,btnPrevPage;
    @FXML
    private TextField txtEventCSearch;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private TextField filterValueField;
    @FXML
    private Button btnFilter;

    private final EventManagement eventManagement = new EventManagement();
    private ObservableList<String> allEvents = FXCollections.observableArrayList();
    private ObservableList<String> filteredEvents = FXCollections.observableArrayList();

    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;


    @FXML
    public void initialize() {
        loadEvents();
        logoutButtonEC.setOnAction(event -> logoutMainScreen());
        btnEventCSearch.setOnAction(event -> searchEvents());
        btnNextPage.setOnAction(event -> nextPage());
        btnPrevPage.setOnAction(event -> previousPage());
        sortComboBox.getItems().addAll(
                "Sort by Date",
                "Sort by Price",
                "Sort by Location");
        sortComboBox.setOnAction(event -> sortEvents());
        filterComboBox.getItems().addAll(
                "Free Events",
                "Paid Events",
                "This Month's Events",
                "By Location",
                "By Price"
        );
        btnFilter.setOnAction(event -> filterEvents());
    }

    public void loadEvents() {
        List<Event> events = eventManagement.getAllEvents();
        allEvents.setAll(events.stream().map(Event::getEventName).collect(Collectors.toList()));
        searchEvents();
    }

    private void searchEvents() {
        String query = txtEventCSearch.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            filteredEvents.setAll(allEvents);
        } else {
            filteredEvents.setAll(allEvents.stream()
                    .filter(event -> event.toLowerCase().contains(query))
                    .collect(Collectors.toList()));
        }
        currentPage = 0;
        updatePagination();
    }

    private void updatePagination() {
        int fromIndex = currentPage * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, filteredEvents.size());

        if (fromIndex >= filteredEvents.size()) {
            currentPage = Math.max(0, currentPage - 1);
            fromIndex = currentPage * PAGE_SIZE;
            toIndex = Math.min(fromIndex + PAGE_SIZE, filteredEvents.size());
        }

        eventsListView.setItems(FXCollections.observableArrayList(filteredEvents.subList(fromIndex, toIndex)));
        btnPrevPage.setDisable(currentPage == 0);
        btnNextPage.setDisable(toIndex >= filteredEvents.size());
    }

    private void nextPage() {
        if ((currentPage + 1) * PAGE_SIZE < filteredEvents.size()) {
            currentPage++;
            updatePagination();
        }
    }

    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            updatePagination();
        }
    }

    private void sortEvents() {
        String selectedOption = sortComboBox.getValue();
        List<Event> events = eventManagement.getAllEvents(); // Fetch events from database

        if (selectedOption == null) return; // If no option selected, do nothing

        switch (selectedOption) {
            case "Sort by Date":
                events.sort(Comparator.comparing(Event::getDate)); // Assuming date is stored as String
                break;
            case "Sort by Price":
                events.sort(Comparator.comparingInt(Event::getPrice));
                break;
            case "Sort by Location":
                events.sort(Comparator.comparing(Event::getLocation));
                break;
            default:
                return;
        }

        // Update the ListView
        List<String> eventNames = events.stream()
                .map(Event::getEventName)
                .collect(Collectors.toList());
        ObservableList<String> observableList = FXCollections.observableArrayList(eventNames);
        eventsListView.setItems(observableList);
    }

    private void filterEvents() {
        String filterOption = filterComboBox.getValue();
        String filterValue = filterValueField.getText().trim();

        if (filterOption == null) return; // No filter selected

        List<Event> events = eventManagement.getAllEvents(); // Load all events again

        switch (filterOption) {
            case "Free Events":
                events = events.stream()
                        .filter(event -> event.getPrice() == 0)
                        .collect(Collectors.toList());
                break;
            case "Paid Events":
                events = events.stream()
                        .filter(event -> event.getPrice() > 0)
                        .collect(Collectors.toList());
                break;
            case "This Month's Events":
                events = events.stream()
                        .filter(event -> event.getDate().equals(getTodayDate()))
                        .collect(Collectors.toList());
                break;
            case "By Location":
                if (!filterValue.isEmpty()) {
                    events = events.stream()
                            .filter(event -> event.getLocation().toLowerCase().contains(filterValue.toLowerCase()))
                            .collect(Collectors.toList());
                }
                break;
            case "By Price":
                try {
                    int priceFilter = Integer.parseInt(filterValue);
                    events = events.stream()
                            .filter(event -> event.getPrice() <= priceFilter)
                            .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price input");
                    return;
                }
                break;
        }

        // Update ListView
        List<String> eventNames = events.stream()
                .map(Event::getEventName)
                .collect(Collectors.toList());
        eventsListView.setItems(FXCollections.observableArrayList(eventNames));
    }

    // Helper method to get today's date in the correct format
    private String getTodayDate() {
        return java.time.LocalDate.now().toString(); // Adjust format if needed
    }

    private void logoutMainScreen() {
        switchScene("LoginMain.fxml", "Login Screen");
    }

    private void switchScene(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButtonEC.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
