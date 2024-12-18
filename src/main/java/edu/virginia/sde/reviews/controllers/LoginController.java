package edu.virginia.sde.reviews.controllers;

import edu.virginia.sde.reviews.services.DatabaseService;
import edu.virginia.sde.reviews.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.application.Platform;
import java.io.IOException;

public class LoginController {
    private final DatabaseService database;

    @FXML
    private Label messageLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label error;

    public LoginController() {
        this.database = new DatabaseService();
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            editErrorMsg("Username and password cannot be empty.", true);
            return;
        }

        if (database.validateUser(username, password)) {
            editErrorMsg("", false);
            messageLabel.setText("Successfully logged in!");
            try {
                SceneManager.switchToScene(usernameField, "/edu/virginia/sde/reviews/courseSearch.fxml", username);
            } catch (IOException e) {
                editErrorMsg("Error loading course search page.", true);
                e.printStackTrace();
            }
        } else {
            editErrorMsg("Invalid username or password.", true);
        }
    }

    @FXML
    public void handleNewAccount() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            editErrorMsg("Username and password cannot be empty.", true);
            return;
        }

        if (password.length() < 8) {
            editErrorMsg("Password must be at least 8 characters long.", true);
            return;
        }

        if (database.createUser(username, password)) {
            editErrorMsg("", false);
            messageLabel.setText("Account created successfully! Please log in.");
        } else {
            editErrorMsg("Username already exists.", true);
        }
    }

    @FXML
    public void handleClose() {
        Platform.exit();
    }

    private void editErrorMsg(String message, boolean visibility) {
        error.setText(message);
        error.setVisible(visibility);
    }
} 