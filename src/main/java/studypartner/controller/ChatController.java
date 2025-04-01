package studypartner.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import studypartner.model.Conversation;
import studypartner.model.Message;
import studypartner.model.Student;
import studypartner.service.ChatService;
import studypartner.service.StudentService;
import studypartner.util.NavigationManager;
import studypartner.util.SessionManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the chat view.
 */
public class ChatController {
    
    @FXML private ListView<Conversation> conversationListView;
    @FXML private VBox chatContainer;
    @FXML private Label chatTitleLabel;
    @FXML private ImageView chatProfileImage;
    @FXML private ListView<Message> messageListView;
    @FXML private TextField messageField;
    @FXML private Button sendButton;
    
    private Student currentStudent;
    private Conversation selectedConversation;
    private ObservableList<Conversation> conversations = FXCollections.observableArrayList();
    private ObservableList<Message> messages = FXCollections.observableArrayList();
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy");
    
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
        
        // Set up conversation list view
        conversationListView.setItems(conversations);
        conversationListView.setCellFactory(param -> new ConversationCell());
        
        // Set up message list view
        messageListView.setItems(messages);
        messageListView.setCellFactory(param -> new MessageCell());
        
        // Load conversations
        loadConversations();
        
        // Check if a student was selected from the matching view
        Object selectedStudentId = NavigationManager.getData("selectedStudentId");
        if (selectedStudentId != null) {
            String studentId = (String) selectedStudentId;
            selectConversationWithStudent(studentId);
            NavigationManager.clearData("selectedStudentId");
        }
        
        // Add listener for conversation selection
        conversationListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectConversation(newValue);
                    }
                });
        
        // Add listener for enter key in message field
        messageField.setOnAction(event -> handleSendMessage());
        
        // Add listener for send button
        sendButton.setOnAction(event -> handleSendMessage());
        
        // Hide chat if no conversation is selected
        if (selectedConversation == null) {
            chatContainer.setVisible(false);
        }
    }
    
    /**
     * Loads all conversations for the current student.
     */
    private void loadConversations() {
        // Get conversations from service
        List<Conversation> studentConversations = 
                ChatService.getInstance().getConversationsForStudent(currentStudent.getId());
        
        // Update observable list
        conversations.clear();
        conversations.addAll(studentConversations);
        
        // Select first conversation if available
        if (!conversations.isEmpty()) {
            conversationListView.getSelectionModel().select(0);
        }
    }
    
    /**
     * Selects a conversation by the other student's ID.
     * 
     * @param studentId The other student's ID
     */
    private void selectConversationWithStudent(String studentId) {
        // Find conversation with student
        for (Conversation conversation : conversations) {
            if (conversation.getStudent1Id().equals(studentId) || 
                conversation.getStudent2Id().equals(studentId)) {
                conversationListView.getSelectionModel().select(conversation);
                return;
            }
        }
        
        // Create new conversation if not found
        Conversation newConversation = 
                ChatService.getInstance().getOrCreateConversation(currentStudent.getId(), studentId);
        
        // Add to list and select
        conversations.add(newConversation);
        conversationListView.getSelectionModel().select(newConversation);
    }
    
    /**
     * Selects a conversation and loads its messages.
     * 
     * @param conversation The conversation to select
     */
    private void selectConversation(Conversation conversation) {
        selectedConversation = conversation;
        
        // Get other student
        String otherStudentId = conversation.getOtherStudentId(currentStudent.getId());
        Student otherStudent = StudentService.getInstance().getStudentById(otherStudentId);
        
        // Add null check here
        if (otherStudent == null) {
            chatTitleLabel.setText("Unknown User");
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
                chatProfileImage.setImage(defaultImage);
            } catch (Exception ex) {
                System.err.println("Error loading default profile image: " + ex.getMessage());
            }
        } else {
            // Update chat header
            chatTitleLabel.setText(otherStudent.getName());
            try {
                Image image = new Image(getClass().getResourceAsStream(otherStudent.getProfileImageUrl()));
                chatProfileImage.setImage(image);
            } catch (Exception e) {
                // Use default image
                try {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
                    chatProfileImage.setImage(defaultImage);
                } catch (Exception ex) {
                    System.err.println("Error loading default profile image: " + ex.getMessage());
                }
            }
        }
        
        // Load messages
        messages.clear();
        messages.addAll(conversation.getMessages());
        
        // Scroll to bottom
        messageListView.scrollTo(messages.size() - 1);
        
        // Mark conversation as read
        ChatService.getInstance().markConversationAsRead(conversation.getId(), currentStudent.getId());
        
        // Show chat container
        chatContainer.setVisible(true);
    }
    
    /**
     * Handles sending a message.
     */
    private void handleSendMessage() {
        if (selectedConversation == null || messageField.getText().trim().isEmpty()) {
            return;
        }
        
        // Create message
        Message message = new Message(currentStudent.getId(), messageField.getText().trim());
        
        // Add to conversation
        ChatService.getInstance().addMessageToConversation(selectedConversation.getId(), message);
        
        // Add to message list
        messages.add(message);
        
        // Scroll to bottom
        messageListView.scrollTo(message);
        
        // Clear message field
        messageField.clear();
    }
    
    /**
     * Custom cell for displaying conversations.
     */
    private class ConversationCell extends ListCell<Conversation> {
        @Override
        protected void updateItem(Conversation conversation, boolean empty) {
            super.updateItem(conversation, empty);
            
            if (empty || conversation == null) {
                setText(null);
                setGraphic(null);
                return;
            }
            
            // Get other student
            String otherStudentId = conversation.getOtherStudentId(currentStudent.getId());
            Student otherStudent = StudentService.getInstance().getStudentById(otherStudentId);
            
            // Handle null student (add this check)
            if (otherStudent == null) {
                // Create simple container with unknown user
                HBox container = new HBox();
                container.getStyleClass().add("conversation-cell");
                
                Label unknownLabel = new Label("Unknown User");
                unknownLabel.getStyleClass().add("conversation-name");
                
                container.getChildren().add(unknownLabel);
                setGraphic(container);
                return;
            }
            
            // Create container
            HBox container = new HBox();
            container.getStyleClass().add("conversation-cell");
            
            // Profile image
            ImageView profileImage = new ImageView();
            profileImage.setFitWidth(40);
            profileImage.setFitHeight(40);
            try {
                Image image = new Image(getClass().getResourceAsStream(otherStudent.getProfileImageUrl()));
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
            
            // Name and last message
            VBox textContainer = new VBox();
            
            Label nameLabel = new Label(otherStudent.getName());
            nameLabel.getStyleClass().add("conversation-name");
            
            String lastMessageText = "";
            if (!conversation.getMessages().isEmpty()) {
                Message lastMessage = conversation.getMessages().get(conversation.getMessages().size() - 1);
                String sender = lastMessage.getSenderId().equals(currentStudent.getId()) ? "You: " : "";
                lastMessageText = sender + lastMessage.getContent();
                
                // Check if unread
                if (!lastMessage.getSenderId().equals(currentStudent.getId()) && !lastMessage.isRead()) {
                    nameLabel.getStyleClass().add("unread");
                }
            }
            Label lastMessageLabel = new Label(lastMessageText);
            lastMessageLabel.getStyleClass().add("conversation-last-message");
            
            textContainer.getChildren().addAll(nameLabel, lastMessageLabel);
            
            // Add to container
            container.getChildren().addAll(profileImage, textContainer);
            
            setGraphic(container);
        }
    }
    
    /**
     * Custom cell for displaying messages.
     */
    private class MessageCell extends ListCell<Message> {
        @Override
        protected void updateItem(Message message, boolean empty) {
            super.updateItem(message, empty);
            
            if (empty || message == null) {
                setText(null);
                setGraphic(null);
                return;
            }
            
            // Create container
            VBox container = new VBox();
            container.getStyleClass().add("message-cell");
            
            // Check if sent by current user
            boolean isSentByMe = message.getSenderId().equals(currentStudent.getId());
            if (isSentByMe) {
                container.getStyleClass().add("message-sent");
            } else {
                container.getStyleClass().add("message-received");
            }
            
            // Message text
            Label textLabel = new Label(message.getContent());
            textLabel.setWrapText(true);
            textLabel.getStyleClass().add("message-text");
            
            // Message time
            Label timeLabel = new Label(message.getTimestamp().format(TIME_FORMATTER));
            timeLabel.getStyleClass().add("message-time");
            
            // Add to container
            container.getChildren().addAll(textLabel, timeLabel);
            
            setGraphic(container);
        }
    }
}