package src.main.java.studypartner.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import studypartner.model.Student;
import studypartner.service.ChatService;
import studypartner.util.DialogUtils;
import studypartner.util.NavigationManager;
import studypartner.util.SessionManager;

import java.io.IOException;

/**
 * Controller for the dashboard view that contains the navigation and content panes.
 */
public class DashboardController {
    
    @FXML private BorderPane dashboardPane;
    @FXML private HBox topBar;
    @FXML private Label usernameLabel;
    @FXML private ImageView profileImage;
    @FXML private Button profileButton;
    @FXML private Button matchingButton;
    @FXML private Button chatButton;
    @FXML private Button logoutButton;
    @FXML private StackPane contentPane;
    @FXML private Label unreadMessageCounter;
    
    private Student currentStudent;
    
    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // Get current student
        currentStudent = SessionManager.getInstance().getCurrentStudent();
        if (currentStudent == null) {
            // Redirect to login if not logged in
            NavigationManager.navigateTo("/views/login-view.fxml");
            return;
        }
        
        // Set username
        usernameLabel.setText(currentStudent.getName());
        
        // Set profile image
        if (currentStudent.getProfileImageUrl() != null) {
            try {
                Image image = new Image(getClass().getResourceAsStream(currentStudent.getProfileImageUrl()));
                profileImage.setImage(image);
            } catch (Exception e) {
                System.err.println("Error loading profile image: " + e.getMessage());
            }
        }
        
        // Load profile view as default
        loadProfileView();
        
        // Update unread message counter
        updateUnreadMessageCounter();
    }
    
    /**
     * Updates the unread message counter.
     */
    private void updateUnreadMessageCounter() {
        int unreadCount = ChatService.getInstance().getUnreadMessageCount(currentStudent.getId());
        if (unreadCount > 0) {
            unreadMessageCounter.setText(String.valueOf(unreadCount));
            unreadMessageCounter.setVisible(true);
        } else {
            unreadMessageCounter.setVisible(false);
        }
    }
    
    /**
     * Loads the profile view.
     */
    private void loadProfileView() {
        try {
            // Load profile view into content pane
            Pane profileView = FXMLLoader.load(getClass().getResource("/views/profile-view.fxml"));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(profileView);
            
            // Highlight profile button
            setActiveButton(profileButton);
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.showError("Navigation Error", "Error Loading Profile View", 
                    "Could not load the profile view. Please try again.");
        }
    }
    
    /**
     * Loads the matching view.
     */
    private void loadMatchingView() {
        try {
            // Load matching view into content pane
            Pane matchingView = FXMLLoader.load(getClass().getResource("/views/matching-view.fxml"));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(matchingView);
            
            // Highlight matching button
            setActiveButton(matchingButton);
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.showError("Navigation Error", "Error Loading Matching View", 
                    "Could not load the matching view. Please try again.");
        }
    }
    
    /**
     * Loads the chat view.
     */
    private void loadChatView() {
        try {
            // Load chat view into content pane
            Pane chatView = FXMLLoader.load(getClass().getResource("/views/chat-view.fxml"));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(chatView);
            
            // Highlight chat button
            setActiveButton(chatButton);
            
            // Reset unread counter
            updateUnreadMessageCounter();
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.showError("Navigation Error", "Error Loading Chat View", 
                    "Could not load the chat view. Please try again.");
        }
    }
    
    /**
     * Sets the active navigation button.
     * 
     * @param activeButton The active button
     */
    private void setActiveButton(Button activeButton) {
        // Reset all buttons
        profileButton.getStyleClass().remove("active-nav-button");
        matchingButton.getStyleClass().remove("active-nav-button");
        chatButton.getStyleClass().remove("active-nav-button");
        
        // Set active button
        activeButton.getStyleClass().add("active-nav-button");
    }
    
    /**
     * Handles the profile button click.
     * 
     * @param event The action event
     */
    @FXML
    private void handleProfileButton(ActionEvent event) {
        loadProfileView();
    }
    
    /**
     * Handles the matching button click.
     * 
     * @param event The action event
     */
    @FXML
    private void handleMatchingButton(ActionEvent event) {
        loadMatchingView();
    }
    
    /**
     * Handles the chat button click.
     * 
     * @param event The action event
     */
    @FXML
    private void handleChatButton(ActionEvent event) {
        loadChatView();
    }
    
    /**
     * Handles the logout button click.
     * 
     * @param event The action event
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        if (DialogUtils.showConfirmation("Logout", "Confirm Logout", 
                "Are you sure you want to logout?")) {
            // Clear session
            SessionManager.getInstance().logout();
            
            // Navigate to login view
            NavigationManager.navigateTo("/views/login-view.fxml");
        }
    }
}