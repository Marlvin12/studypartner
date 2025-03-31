package studypartner.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import studypartner.model.Student;
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
        
        Student student = StudentService.getInstance().authenticate(email, password);
        
        if (student != null) {
            SessionManager.getInstance().setCurrentStudent(student);
            NavigationManager.navigateTo("/views/dashboard-view.fxml");
        } else {
            DialogUtils.showError("Login Failed", "Invalid Credentials", 
                    "The email or password you entered is incorrect.");
        }
    }
    
    @FXML
    private void handleRegister(ActionEvent event) {
        NavigationManager.navigateTo("/views/registration-view.fxml");
    }
}