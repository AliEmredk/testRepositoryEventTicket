package controllers;

import bll.UserSession;
import dal.LoginDAO;
import dk.easv.EventsView;
import dk.easv.SettingsView;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public abstract class BaseDashboard {

    protected StackPane contentArea;
    protected Pane userPane, eventsPane, settingsPane;
    protected Button userBtn, eventsBtn, settingsBtn;

    private LoginDAO loginDAO = new LoginDAO();

    protected abstract void addCustomButtons(VBox sidebar, StackPane contentArea);

    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            Parent root = loader.load();

            VBox sidebar = (VBox) ((BorderPane) root).getLeft();
            contentArea = (StackPane) ((BorderPane) root).getCenter();

            ImageView logoView = (ImageView) sidebar.lookup("#logoView");
            logoView.setImage(new Image(getClass().getResourceAsStream("/images/easvlogo.png")));

            eventsBtn = (Button) sidebar.lookup("#eventsBtn");
            settingsBtn = (Button) sidebar.lookup("#settingsBtn");
            Button signOutBtn = (Button) sidebar.lookup("#signOutBtn");

            EventMainController eventMainController = new EventMainController();
            eventsPane = new EventsView(UserSession.getRole(), eventMainController);
            settingsPane = new SettingsView();

            contentArea.getChildren().addAll(eventsPane, settingsPane);

            eventsPane.setVisible(true);
            settingsPane.setVisible(false);

            eventsBtn.setOnAction(e -> {
                switchPane(eventsPane);
                setActiveButton(eventsBtn, eventsBtn, settingsBtn);
            });

            settingsBtn.setOnAction(e -> {
                switchPane(settingsPane);
                setActiveButton(settingsBtn, eventsBtn, settingsBtn);
            });

            signOutBtn.setOnAction(e -> {
                try {
                    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/view/LoginMain.fxml"));
                    Parent loginRoot = loginLoader.load();
                    Scene loginScene = new Scene(loginRoot, 700, 500);

                    URL cssUrl = getClass().getResource("/view/loginstyle");
                    if (cssUrl != null) loginScene.getStylesheets().add(cssUrl.toExternalForm());

                    Stage loginStage = new Stage();
                    loginStage.setTitle("Login");
                    loginStage.setScene(loginScene);
                    loginStage.show();

                    Stage currentStage = (Stage) signOutBtn.getScene().getWindow();
                    currentStage.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS);

            VBox customBtnBox = (VBox) sidebar.lookup("#customButtons");
            addCustomButtons(customBtnBox, contentArea);

            Scene scene = new Scene(root, 950, 600);
            scene.getStylesheets().add(getClass().getResource("/view/dashboard.css").toExternalForm());

            primaryStage.setTitle("Dashboard");
            primaryStage.setScene(scene);
            primaryStage.show();

            // TODO two eventsBtns?
            setActiveButton(eventsBtn, eventsBtn, settingsBtn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void setActiveButton(Button activeBtn, Button... allButtons) {
        for (Button btn : allButtons) {
            btn.getStyleClass().remove("active");
            btn.setTextFill(Color.WHITE);
        }
        activeBtn.setTextFill(Color.web("#3A4B5C"));
        activeBtn.getStyleClass().add("active");
    }

    protected void switchPane(Pane paneToShow) {
        paneToShow.setOpacity(0);
        paneToShow.setVisible(true);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), paneToShow);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();

        contentArea.getChildren().stream()
                .filter(p -> p != paneToShow)
                .forEach(p -> p.setVisible(false));
    }

    protected Pane createContentPane(String content) {
        Label lbl = new Label(content);
        lbl.setFont(new Font(20));
        StackPane pane = new StackPane(lbl);
        pane.setAlignment(Pos.CENTER);
        pane.setStyle("-fx-background-color: white;");
        return pane;
    }

    protected Button createSidebarButton(String icon, String labelText) {
        Button btn = new Button(icon + "  " + labelText);
        btn.setPrefWidth(180);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setFont(Font.font("Arial", 14));
        btn.setTextFill(Color.WHITE);
        btn.setBackground(Background.EMPTY);
        btn.getStyleClass().add("sidebar-button");
        return btn;
    }

    private Button createSignOutButton() {
        Button signOutBtn = new Button("🚪 Sign Out");
        signOutBtn.setPrefWidth(180);
        signOutBtn.setAlignment(Pos.CENTER_LEFT);
        signOutBtn.setFont(Font.font("Arial", 14));
        signOutBtn.setTextFill(Color.WHITE);
        signOutBtn.setBackground(Background.EMPTY);

        signOutBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-padding: 10 20 10 20;
            -fx-cursor: hand;
            -fx-background-radius: 10;
            -fx-text-fill: white;
        """);

        signOutBtn.setOnMouseEntered(e -> signOutBtn.setStyle("""
            -fx-background-color: rgba(255,255,255,0.1);
            -fx-padding: 10 20 10 20;
            -fx-cursor: hand;
            -fx-background-radius: 10;
            -fx-text-fill: white;
        """));

        signOutBtn.setOnMouseExited(e -> signOutBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-padding: 10 20 10 20;
            -fx-cursor: hand;
            -fx-background-radius: 10;
            -fx-text-fill: white;
        """));

        signOutBtn.setOnAction(e -> {
            try {
                UserSession.clearSession();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginMain.fxml"));
                Parent loginRoot = loader.load();

                Scene loginScene = new Scene(loginRoot, 700, 500);

                URL cssUrl = getClass().getResource("/view/loginstyle");
                if (cssUrl != null) {
                    loginScene.getStylesheets().add(cssUrl.toExternalForm());
                }

                Stage loginStage = new Stage();
                loginStage.setTitle("Glassmorphic Login UI");
                loginStage.setScene(loginScene);
                loginStage.show();

                Stage currentStage = (Stage) signOutBtn.getScene().getWindow();
                currentStage.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return signOutBtn;
    }
}
