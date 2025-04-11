package controllers;

import be.User;
import bll.UserManagement;
import dal.UserDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

public class UserManagementController {

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> roleFilter;
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, User> profileColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TableColumn<User, Void> actionsColumn;

    private final ObservableList<User> masterUserList = FXCollections.observableArrayList();
    private final UserDAO userDAO = new UserDAO();
    private final String DEFAULT_AVATAR_PATH = "/images/profileImageTest.png";
    private final UserManagement userManagement = new UserManagement();

    private static UserManagementController instance;

    public UserManagementController() {
        instance = this;
    }

    public static void refreshIfOpen() {
        if (instance != null) {
            instance.refreshUserList();
        }
    }

    @FXML
    public void initialize() {
        setupProfileColumn();

        profileColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue()));
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsername()));
        roleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRole()));

        roleFilter.getItems().clear();
        roleFilter.getItems().addAll("All Roles", "Admin", "Event Coordinator");
        roleFilter.setValue("All Roles");

        refreshUserList();

        roleFilter.setOnAction(e -> applyFilters());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        addActionButtons();
    }

    private void setupProfileColumn() {
        profileColumn.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            private final StackPane centeredBox = new StackPane(imageView);

            {
                imageView.setFitWidth(32);
                imageView.setFitHeight(32);
                centeredBox.setPrefSize(50, 40);
            }

            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setGraphic(null);
                    return;
                }

                Image img;

                try {
                    if (user.getProfileImage() != null) {
                        img = new Image(new ByteArrayInputStream(user.getProfileImage()), 32, 32, true, true);
                    } else {
                        URL imageUrl = getClass().getResource(DEFAULT_AVATAR_PATH);
                        img = new Image(imageUrl.toExternalForm(), 32, 32, true, true);
                    }

                    imageView.setImage(img);
                    imageView.setClip(new Circle(16, 16, 16));
                    setGraphic(centeredBox);

                } catch (Exception e) {
                    e.printStackTrace();
                    setGraphic(null);
                }
            }
        });
    }

    private void applyFilters() {
        String search = searchField.getText().toLowerCase();
        String selectedRole = roleFilter.getValue();

        ObservableList<User> filtered = FXCollections.observableArrayList();

        for (User user : masterUserList) {
            boolean matchesSearch = user.getUsername().toLowerCase().contains(search);
            boolean matchesRole = selectedRole.equals("All Roles") || user.getRole().equalsIgnoreCase(selectedRole);

            if (matchesSearch && matchesRole) {
                filtered.add(user);
            }
        }

        userTable.setItems(filtered);
        addActionButtons();
    }

    private void addActionButtons() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑");

            {
                editBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    openEditUserWindow(user);
                });

                deleteBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    masterUserList.remove(user);
                    userDAO.deleteUser(user.getUsername());
                    applyFilters();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(10, editBtn, deleteBtn));
                }
            }
        });
    }

    public void refreshUserList() {
        masterUserList.setAll(userDAO.getAllUsers());
        applyFilters();
        userTable.refresh();
    }

    @FXML
    private void handleAddUser() {
        openAddUserWindow();
    }

    private void openAddUserWindow() {
        Stage addUserStage = new Stage();
        addUserStage.initModality(Modality.APPLICATION_MODAL);
        addUserStage.setTitle("Add User");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "Event Coordinator");
        roleBox.setPromptText("Select Role");

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleBox.getValue();

            if (username.isEmpty() || password.isEmpty() || role == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all fields", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            boolean success = userDAO.addUser(new User(username, password, role));
            if (success) {
                refreshUserList();
                addUserStage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Username already exists", ButtonType.OK);
                alert.showAndWait();
            }


        });

        vbox.getChildren().addAll(new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                new Label("Role:"), roleBox,
                saveBtn);

        Scene scene = new Scene(vbox, 300, 250);
        addUserStage.setScene(scene);
        addUserStage.show();
    }

    private void openEditUserWindow(User user) {
        Stage editStage = new Stage();
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.setTitle("Edit User: " + user.getUsername());

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);

        String originalUsername = user.getUsername(); // Store before editing

        TextField usernameField = new TextField(user.getUsername());

        PasswordField passwordField = new PasswordField();
        passwordField.setText(user.getPassword());
        passwordField.setEditable(false);

        TextField passwordTextField = new TextField(user.getPassword());
        passwordTextField.setManaged(false);
        passwordTextField.setVisible(false);

        StackPane passwordStack = new StackPane(passwordField, passwordTextField);
        passwordStack.setMaxWidth(Double.MAX_VALUE);

        CheckBox showPassword = new CheckBox("Show Password");
        showPassword.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            passwordField.setVisible(!isSelected);
            passwordField.setManaged(!isSelected);
            passwordTextField.setVisible(isSelected);
            passwordTextField.setManaged(isSelected);
        });

        Button saveBtn = new Button("Save Changes");
        saveBtn.setOnAction(e -> {
            String newUsername = usernameField.getText().trim();
            String password = showPassword.isSelected() ? passwordTextField.getText().trim() : passwordField.getText().trim();

            if (!newUsername.isEmpty() && password != null) {
                user.setUsername(newUsername);
                user.setPassword(password); // Optional: update only if you allow it
                boolean success = userManagement.isUsernameAlreadyUsed(user.getUsername());
                if (!success) {
                    refreshUserList();
                    editStage.close();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Username already exists", ButtonType.OK);
                    alert.showAndWait();
                }

            }
        });

        vbox.getChildren().addAll(
                new Label("Edit Username:"), usernameField,
                new Label("Password:"), passwordStack,
                showPassword,
                saveBtn
        );

        Scene scene = new Scene(vbox, 300, 250);
        editStage.setScene(scene);
        editStage.show();
    }
}