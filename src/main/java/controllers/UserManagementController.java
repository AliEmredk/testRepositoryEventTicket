package controllers;

import be.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class UserManagementController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilter;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Void> actionsColumn;

    private ObservableList<User> masterUserList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getUser_Id()).asObject());
        nameColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUsername()));
        roleColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getRole()));

        masterUserList.addAll(
                new User(1, "Eve Wilder", "pass", "Admin"),
                new User(2, "Zayne Frost", "frosty", "Event Coordinator")
        );

        userTable.setItems(masterUserList);
        roleFilter.setOnAction(e -> applyFilters());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        addActionButtons();
    }

    private void applyFilters() {
        String search = searchField.getText().toLowerCase();
        String selectedRole = roleFilter.getValue();

        ObservableList<User> filtered = FXCollections.observableArrayList();

        for (User user : masterUserList) {
            boolean matchesSearch = user.getUsername().toLowerCase().contains(search);
            boolean matchesRole = selectedRole == null || selectedRole.equals("All Roles") || user.getRole().equals(selectedRole);

            if (matchesSearch && matchesRole) {
                filtered.add(user);
            }
        }

        userTable.setItems(filtered);
    }

    private void addActionButtons() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑");

            {
                editBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    System.out.println("Edit user: " + user.getUsername());
                });

                deleteBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    System.out.println("Delete user: " + user.getUsername());
                    masterUserList.remove(user);
                    applyFilters();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10, editBtn, deleteBtn);
                    setGraphic(hbox);
                }
            }
        });
    }

    @FXML
    private void handleAddUser() {
        System.out.println("Add User Clicked");
    }
}