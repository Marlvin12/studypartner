package studypartner.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import studypartner.service.StudentService;
import studypartner.util.DialogUtils;
import studypartner.util.NavigationManager;
import studypartner.util.SessionManager;
import studypartner.util.ValidationUtils;

public class LoginController {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;
    
    @FXML
    public void initialize() {
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            emailField.setStyle("");
        });
        
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            passwordField.setStyle("");
        });
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        if (!ValidationUtils.validateAll(
                ValidationUtils.validateNotEmpty(emailField, "Email"),
                ValidationUtils.validateEmail(emailField),
                ValidationUtils.validateNotEmpty(passwordField, "Password"))) {
            return;
        }
        
        String email = emailField.getText().trim();
        String password = passwordField.getText();


        try {
            loginButton.setDisable(true);

            StudentService.getInstance().authenticateAsync(email, password).thenAccept(student -> {
                javafx.application.Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    if (student != null) {
                        SessionManager.getInstance().setCurrentStudent(student);
                        NavigationManager.navigateTo("/views/dashboard-view.fxml");
                    } else {
                        DialogUtils.showError("Login Failed", "Invalid Credentials",
                                "The email or password you entered is incorrect.");
                    }
                });
            }).exceptionally(ex -> {
                javafx.application.Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    DialogUtils.showError("Login Error", "Something went wrong", ex.getMessage());
                });
                return null;
            });
        } catch (Exception e) {
            System.err.println("Root cause:");
            Throwable rootCause = e;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            rootCause.printStackTrace();

            // Optionally show error to user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error: " + rootCause.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    private void handleRegister(ActionEvent event) {
        NavigationManager.navigateTo("/views/registration-view.fxml");
    }
}