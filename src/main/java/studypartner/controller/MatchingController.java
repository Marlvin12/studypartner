package studypartner.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import studypartner.model.Student;
import studypartner.model.Subject;
import studypartner.service.ChatService;
import studypartner.service.StudentService;
import studypartner.service.StudentService.MatchResult;
import studypartner.util.DialogUtils;
import studypartner.util.NavigationManager;
import studypartner.util.SessionManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the matching view.
 */
public class MatchingController {
    
    @FXML private ComboBox<Subject> subjectFilterComboBox;
    @FXML private ComboBox<String> availabilityFilterComboBox;
    @FXML private Button applyFiltersButton;
    @FXML private ScrollPane matchesScrollPane;
    @FXML private VBox matchesContainer;
    @FXML private Label noMatchesLabel;
    
    private Student currentStudent;
    private List<MatchResult> allMatches;
    
    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // Get current student
        currentStudent = SessionManager.getInstance().getCurrentStudent();
        if (currentStudent == null) {
            return;
        }
        
        // Set up subject filter
        List<Subject> allSubjects = StudentService.getInstance().getAllSubjects();
        subjectFilterComboBox.setItems(FXCollections.observableArrayList(allSubjects));
        subjectFilterComboBox.setPromptText("Filter by subject");
        
        // Set up availability filter
        ObservableList<String> dayOptions = FXCollections.observableArrayList(
                "Any Day", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        availabilityFilterComboBox.setItems(dayOptions);
        availabilityFilterComboBox.setValue("Any Day");
        
        // Load matches
        loadMatches();
    }
    
    /**
     * Loads all potential matches.
     */
    private void loadMatches() {
        // Get matches from service
        allMatches = StudentService.getInstance().findMatches(currentStudent.getId());
        
        // Apply filters and display
        applyFilters();
    }
    
    /**
     * Applies the selected filters and updates the display.
     */
    private void applyFilters() {
        Subject subjectFilter = subjectFilterComboBox.getValue();
        String dayFilter = availabilityFilterComboBox.getValue();
        
        // Filter matches
        List<MatchResult> filteredMatches = allMatches;
        
        if (subjectFilter != null) {
            final Subject targetSubject = subjectFilter;
            filteredMatches = filteredMatches.stream()
                    .filter(match -> match.getStudent().getSubjects().stream()
                            .anyMatch(s -> s.getCode().equals(targetSubject.getCode())))
                    .collect(Collectors.toList());
        }
        
        if (dayFilter != null && !dayFilter.equals("Any Day")) {
            final String day = dayFilter.toUpperCase();
            filteredMatches = filteredMatches.stream()
                    .filter(match -> match.getStudent().getAvailability().stream()
                            .anyMatch(ts -> ts.getDay().toString().equals(day)))
                    .collect(Collectors.toList());
        }
        
        // Display filtered matches
        displayMatches(filteredMatches);
    }
    
    /**
     * Displays the filtered matches.
     * 
     * @param matches The matches to display
     */
    private void displayMatches(List<MatchResult> matches) {
        // Clear container
        matchesContainer.getChildren().clear();
        
        if (matches.isEmpty()) {
            // Show no matches label
            noMatchesLabel.setVisible(true);
            matchesScrollPane.setVisible(false);
        } else {
            // Hide no matches label
            noMatchesLabel.setVisible(false);
            matchesScrollPane.setVisible(true);
            
            // Create a card for each match
            for (MatchResult match : matches) {
                VBox matchCard = createMatchCard(match);
                matchesContainer.getChildren().add(matchCard);
            }
        }
    }
    
    /**
     * Creates a card for a match.
     * 
     * @param match The match
     * @return The match card
     */
    private VBox createMatchCard(MatchResult match) {
        Student student = match.getStudent();
        int compatibility = match.getCompatibility();
        
        // Create card container
        VBox card = new VBox();
        card.getStyleClass().add("match-card");
        
        // Create header with profile image and name
        GridPane header = new GridPane();
        header.getStyleClass().add("match-card-header");
        
        // Profile image
        ImageView profileImage = new ImageView();
        profileImage.setFitWidth(60);
        profileImage.setFitHeight(60);
        try {
            Image image = new Image(getClass().getResourceAsStream(student.getProfileImageUrl()));
            profileImage.setImage(image);
        } catch (Exception e) {
            // Use default image
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
                profileImage.setImage(defaultImage);
            } catch (Exception ex) {
                System.err.println("Error loading default profile image: " + ex.getMessage());
            }
        }
        
        // Name and compatibility
        Label nameLabel = new Label(student.getName());
        nameLabel.getStyleClass().add("match-name");
        
        ProgressBar compatibilityBar = new ProgressBar(compatibility / 100.0);
        compatibilityBar.getStyleClass().add("compatibility-bar");
        compatibilityBar.setPrefWidth(150);
        
        Label compatibilityLabel = new Label(compatibility + "% Match");
        compatibilityLabel.getStyleClass().add("compatibility-label");
        
        // Add to header
        header.add(profileImage, 0, 0, 1, 2);
        header.add(nameLabel, 1, 0);
        header.add(compatibilityBar, 1, 1);
        header.add(compatibilityLabel, 2, 1);
        
        // Create details section
        VBox details = new VBox();
        details.getStyleClass().add("match-details");
        
        // Major and year
        Label majorLabel = new Label("Major: " + student.getMajor());
        Label yearLabel = new Label("Year: " + student.getYearOfStudy());
        
        // Subjects
        Label subjectsTitle = new Label("Subjects:");
        subjectsTitle.getStyleClass().add("detail-title");
        
        ListView<Subject> subjectsList = new ListView<>();
        subjectsList.setItems(FXCollections.observableArrayList(student.getSubjects()));
        subjectsList.setPrefHeight(80);
        
        // Bio
        Label bioTitle = new Label("Bio:");
        bioTitle.getStyleClass().add("detail-title");
        
        TextArea bioText = new TextArea(student.getBio());
        bioText.setWrapText(true);
        bioText.setEditable(false);
        bioText.setPrefHeight(80);
        
        // Add to details
        details.getChildren().addAll(
                majorLabel, yearLabel, 
                subjectsTitle, subjectsList,
                bioTitle, bioText);
        
        // Create action buttons
        Button startChatButton = new Button("Start Chat");
        startChatButton.getStyleClass().add("action-button");
        startChatButton.setOnAction(event -> startChat(student));
        
        // Add all components to card
        card.getChildren().addAll(header, details, startChatButton);
        
        return card;
    }
    
    /**
     * Starts a chat with a student.
     * 
     * @param student The student to chat with
     */
    private void startChat(Student student) {
        // Create or get existing conversation
        ChatService.getInstance().getOrCreateConversation(
                currentStudent.getId(), student.getId());
        
        // Navigate to chat view
        NavigationManager.setData("selectedStudentId", student.getId());
        NavigationManager.navigateTo("/views/dashboard-view.fxml");
        
        // Select chat tab
        // Note: This is handled in the dashboard controller
    }
    
    /**
     * Handles the apply filters button click.
     * 
     * @param event The action event
     */
    @FXML
    private void handleApplyFilters(ActionEvent event) {
        applyFilters();
    }
}