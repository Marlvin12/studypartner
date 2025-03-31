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
import studypartner.util.ValidationUtils;

import java.util.UUID;

/**
 * Controller for the registration view.
 */
public class RegistrationController {
    
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Hyperlink loginLink;
    
    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // Add listeners for validation as the user types
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            nameField.setStyle("");
        });
        
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            emailField.setStyle("");
        });
        
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            passwordField.setStyle("");
        });
        
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmPasswordField.setStyle("");
        });
    }
    
    /**
     * Handles the register button click.
     * 
     * @param event The action event
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        // Validate form fields
        if (!ValidationUtils.validateAll(
                ValidationUtils.validateNotEmpty(nameField, "Name"),
                ValidationUtils.validateNotEmpty(emailField, "Email"),
                ValidationUtils.validateEmail(emailField),
                ValidationUtils.validateNotEmpty(passwordField, "Password"),
                ValidationUtils.validatePassword(passwordField))) {
            return;
        }
        
        // Validate that passwords match
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            confirmPasswordField.setStyle("-fx-border-color: red;");
            DialogUtils.showError("Validation Error", "Passwords Don't Match", 
                    "The passwords you entered do not match.");
            return;
        }
        
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Check if email is already in use
        StudentService studentService = StudentService.getInstance();
        if (studentService.authenticate(email, null) != null) {
            emailField.setStyle("-fx-border-color: red;");
            DialogUtils.showError("Registration Error", "Email Already Registered", 
                    "This email is already registered. Please use a different email.");
            return;
        }
        
        // Create new student
        String id = UUID.randomUUID().toString();
        Student newStudent = new Student(id, name, email, password);
        
        // Add student to service
        studentService.addStudent(newStudent);
        
        // Show success message
        DialogUtils.showInformation("Registration Successful", "Account Created", 
                "Your account has been created successfully. You can now log in.");
        
        // Navigate to login view
        NavigationManager.navigateTo("/views/login-view.fxml");
    }
    
    /**
     * Handles the login link click.
     * 
     * @param event The action event
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        NavigationManager.navigateTo("/views/login-view.fxml");
    }
}