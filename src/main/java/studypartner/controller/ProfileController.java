package src.main.java.studypartner.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import studypartner.model.Student;
import studypartner.model.Subject;
import studypartner.model.TimeSlot;
import studypartner.service.StudentService;
import studypartner.util.DialogUtils;
import studypartner.util.SessionManager;
import studypartner.util.ValidationUtils;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the profile view.
 */
public class ProfileController {
    
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField majorField;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private TextArea bioTextArea;
    @FXML private TextField subjectCodeField;
    @FXML private TextField subjectNameField;
    @FXML private Button addSubjectButton;
    @FXML private ListView<Subject> subjectListView;
    @FXML private Button removeSubjectButton;
    @FXML private ComboBox<DayOfWeek> dayComboBox;
    @FXML private ComboBox<String> startTimeComboBox;
    @FXML private ComboBox<String> endTimeComboBox;
    @FXML private Button addTimeSlotButton;
    @FXML private ListView<TimeSlot> timeSlotListView;
    @FXML private Button removeTimeSlotButton;
    @FXML private Button saveButton;
    @FXML private VBox profileContainer;
    
    private Student currentStudent;
    private ObservableList<Subject> subjects = FXCollections.observableArrayList();
    private ObservableList<TimeSlot> timeSlots = FXCollections.observableArrayList();
    
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
        
        // Set up year of study combo box
        yearComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6));
        
        // Set up day combo box
        dayComboBox.setItems(FXCollections.observableArrayList(DayOfWeek.values()));
        
        // Set up time combo boxes
        ObservableList<String> timeOptions = FXCollections.observableArrayList();
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String hourStr = String.format("%02d", hour);
                String minuteStr = String.format("%02d", minute);
                timeOptions.add(hourStr + ":" + minuteStr);
            }
        }
        startTimeComboBox.setItems(timeOptions);
        endTimeComboBox.setItems(timeOptions);
        
        // Set up subject list view
        subjectListView.setItems(subjects);
        subjects.addAll(currentStudent.getSubjects());
        
        // Set up time slot list view
        timeSlotListView.setItems(timeSlots);
        timeSlots.addAll(currentStudent.getAvailability());
        
        // Load student data
        loadStudentData();
        
        // Add validation listeners
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            nameField.setStyle("");
        });
        
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            emailField.setStyle("");
        });
        
        majorField.textProperty().addListener((observable, oldValue, newValue) -> {
            majorField.setStyle("");
        });
    }
    
    /**
     * Loads the student data into the form.
     */
    private void loadStudentData() {
        nameField.setText(currentStudent.getName());
        emailField.setText(currentStudent.getEmail());
        majorField.setText(currentStudent.getMajor());
        yearComboBox.setValue(currentStudent.getYearOfStudy());
        bioTextArea.setText(currentStudent.getBio());
    }
    
    /**
     * Handles the add subject button click.
     * 
     * @param event The action event
     */
    @FXML
    private void handleAddSubject(ActionEvent event) {
        // Validate subject fields
        if (!ValidationUtils.validateAll(
                ValidationUtils.validateNotEmpty(subjectCodeField, "Subject Code"),
                ValidationUtils.validateNotEmpty(subjectNameField, "Subject Name"))) {
            return;
        }
        
        String code = subjectCodeField.getText().trim().toUpperCase();
        String name = subjectNameField.getText().trim();
        
        // Check if subject already exists
        for (Subject subject : subjects) {
            if (subject.getCode().equals(code)) {
                DialogUtils.showError("Error", "Duplicate Subject", 
                        "This subject code is already in your list.");
                return;
            }
        }
        
        // Add subject
        Subject subject = new Subject(code, name);
        subjects.add(subject);
        
        // Clear fields
        subjectCodeField.clear();
        subjectNameField.clear();
    }
    
    /**
     * Handles the remove subject button click.
     * 
     * @param event The action event
     */
    @FXML
    private void handleRemoveSubject(ActionEvent event) {
        Subject selectedSubject = subjectListView.getSelectionModel().getSelectedItem();
        if (selectedSubject != null) {
            subjects.remove(selectedSubject);
        } else {
            DialogUtils.showWarning("Warning", "No Subject Selected", 
                    "Please select a subject to remove.");
        }
    }
    
    /**
     * Handles the add time slot button click.
     * 
     * @param event The action event
     */
    @FXML
    private void handleAddTimeSlot(ActionEvent event) {
        // Validate time slot fields
        DayOfWeek day = dayComboBox.getValue();
        String startTimeStr = startTimeComboBox.getValue();
        String endTimeStr = endTimeComboBox.getValue();
        
        if (day == null || startTimeStr == null || endTimeStr == null) {
            DialogUtils.showError("Error", "Missing Fields", 
                    "Please select day, start time, and end time.");
            return;
        }
        
        // Parse times
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);
        
        // Validate time range
        if (!startTime.isBefore(endTime)) {
            DialogUtils.showError("Error", "Invalid Time Range", 
                    "Start time must be before end time.");
            return;
        }
        
        // Add time slot
        TimeSlot timeSlot = new TimeSlot(day, startTime, endTime);
        timeSlots.add(timeSlot);
        
        // Reset selection
        dayComboBox.getSelectionModel().clearSelection();
        startTimeComboBox.getSelectionModel().clearSelection();
        endTimeComboBox.getSelectionModel().clearSelection();
    }
    
    /**
     * Handles the remove time slot button click.
     * 
     * @param event The action event
     */
    @FXML
    private void handleRemoveTimeSlot(ActionEvent event) {
        TimeSlot selectedTimeSlot = timeSlotListView.getSelectionModel().getSelectedItem();
        if (selectedTimeSlot != null) {
            timeSlots.remove(selectedTimeSlot);
        } else {
            DialogUtils.showWarning("Warning", "No Time Slot Selected", 
                    "Please select a time slot to remove.");
        }
    }
    
    /**
     * Handles the save button click.
     * 
     * @param event The action event
     */
    @FXML
    private void handleSave(ActionEvent event) {
        // Validate form fields
        if (!ValidationUtils.validateAll(
                ValidationUtils.validateNotEmpty(nameField, "Name"),
                ValidationUtils.validateNotEmpty(emailField, "Email"),
                ValidationUtils.validateEmail(emailField),
                ValidationUtils.validateNotEmpty(majorField, "Major"))) {
            return;
        }
        
        // Update student data
        currentStudent.setName(nameField.getText().trim());
        currentStudent.setEmail(emailField.getText().trim());
        currentStudent.setMajor(majorField.getText().trim());
        currentStudent.setYearOfStudy(yearComboBox.getValue());
        currentStudent.setBio(bioTextArea.getText().trim());
        
        // Update subjects
        List<Subject> newSubjects = new ArrayList<>(subjects);
        currentStudent.setSubjects(newSubjects);
        
        // Update time slots
        List<TimeSlot> newTimeSlots = new ArrayList<>(timeSlots);
        currentStudent.setAvailability(newTimeSlots);
        
        // Save to service
        StudentService.getInstance().updateStudent(currentStudent);
        
        // Show success message
        DialogUtils.showInformation("Success", "Profile Updated", 
                "Your profile has been updated successfully.");
    }
}