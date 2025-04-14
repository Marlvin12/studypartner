package studypartner.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import studypartner.model.Conversation;
import studypartner.model.Message;
import studypartner.model.Student;
import studypartner.service.ChatService;
import studypartner.service.StudentService;
import studypartner.util.NavigationManager;
import studypartner.util.SessionManager;

import java.time.format.DateTimeFormatter;
import java.util.Date;
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

    private final ChatService chatService = ChatService.getInstance();
    private final StudentService studentService = StudentService.getInstance();

    private Timeline chatUpdateTimeline;
    private String lastMessageId;
    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // Get current student
        currentStudent = SessionManager.getInstance().getCurrentStudent();
        if (currentStudent == null) {
            System.err.println("No current student found in session");
            return;
        }

        setupListViews();
        setupEventHandlers();
        setupChatUpdates();
        loadConversations();
        checkForSelectedStudent();
    }

    private void setupChatUpdates() {
        // Create a timeline that runs every 2 seconds
        chatUpdateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5), event -> checkForNewMessages())
        );
        chatUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        chatUpdateTimeline.play();
    }

    private void checkForNewMessages() {
        if (selectedConversation != null) {
            try {
                // Get the latest version of the conversation
                Conversation updatedConversation =
                        chatService.getConversationById(selectedConversation.getId());

                if (updatedConversation != null) {
                    // Check if there are new messages
                    List<Message> currentMessages = messages;
                    List<Message> newMessages = updatedConversation.getMessages();

                    if (newMessages != null &&
                            (currentMessages.isEmpty() ||
                                    newMessages.size() > currentMessages.size())) {

                        // Update the messages list
                        Platform.runLater(() -> {
                            messages.clear();
                            messages.addAll(newMessages);
                            messageListView.scrollTo(messages.size() - 1);

                            // Mark messages as read if the chat is visible
                            if (chatContainer.isVisible()) {
                                chatService.markConversationAsRead(
                                        selectedConversation.getId(),
                                        currentStudent.getId()
                                );
                            }
                        });
                    }
                }

                // Also check for new conversations
                updateConversationsList();

            } catch (Exception e) {
                System.err.println("Error checking for new messages: " + e.getMessage());
            }
        }
    }

    private void updateConversationsList() {
        List<Conversation> updatedConversations =
                chatService.getConversationsForStudent(currentStudent.getId());

        // Check if there are any new conversations
        if (updatedConversations.size() != conversations.size()) {
            Platform.runLater(() -> {
                // Remember selected conversation
                Conversation selected = conversationListView.getSelectionModel().getSelectedItem();

                conversations.clear();
                conversations.addAll(updatedConversations);

                // Restore selection
                if (selected != null) {
                    for (Conversation conv : conversations) {
                        if (conv.getId().equals(selected.getId())) {
                            conversationListView.getSelectionModel().select(conv);
                            break;
                        }
                    }
                }
            });
        }
    }
    private void setupListViews() {
        // Set up conversation list
        conversationListView.setItems(conversations);
        conversationListView.setCellFactory(lv -> new ConversationCell());

        // Set up message list
        messageListView.setItems(messages);
        messageListView.setCellFactory(lv -> new MessageCell());
    }

    private void setupEventHandlers() {
        // Conversation selection handler
        conversationListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        System.out.println("Conversation selected: " + newValue.getId());
                        selectConversation(newValue);
                    }
                });

        // Message sending handlers
        messageField.setOnAction(event -> handleSendMessage());
        sendButton.setOnAction(event -> handleSendMessage());
    }

    private void checkForSelectedStudent() {
        Object selectedStudentId = NavigationManager.getData("selectedStudentId");
        if (selectedStudentId != null) {
            String studentId = (String) selectedStudentId;
            System.out.println("Selected student from navigation: " + studentId);
            createOrSelectConversation(studentId);
            NavigationManager.clearData("selectedStudentId");
        }
    }

    private void createOrSelectConversation(String otherStudentId) {
        try {
            System.out.println("Creating/selecting conversation with student: " + otherStudentId);

            Conversation conversation = chatService.getOrCreateConversation(
                    currentStudent.getId(),
                    otherStudentId
            );

            if (conversation != null) {
                if (!conversations.contains(conversation)) {
                    conversations.add(conversation);
                }
                conversationListView.getSelectionModel().select(conversation);
                selectConversation(conversation);
            } else {
                System.err.println("Failed to create/get conversation");
            }
        } catch (Exception e) {
            System.err.println("Error creating/selecting conversation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Loads all conversations for the current student.
     */
    private void loadConversations() {
        try {
            List<Conversation> studentConversations =
                    chatService.getConversationsForStudent(currentStudent.getId());

            System.out.println("Loaded " + studentConversations.size() +
                    " conversations for student: " + currentStudent.getId());

            conversations.clear();
            conversations.addAll(studentConversations);

            if (!conversations.isEmpty()) {
                conversationListView.getSelectionModel().select(0);
            } else {
                chatContainer.setVisible(false);
            }
        } catch (Exception e) {
            System.err.println("Error loading conversations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void selectConversation(Conversation conversation) {
        try {
            selectedConversation = conversation;

            String otherStudentId = conversation.getOtherStudentId(currentStudent.getId());
            Student otherStudent = studentService.getStudentById(otherStudentId);

            updateChatHeader(otherStudent);
            loadMessages(conversation);

            chatContainer.setVisible(true);

            // Mark messages as read
            chatService.markConversationAsRead(conversation.getId(), currentStudent.getId());
        } catch (Exception e) {
            System.err.println("Error selecting conversation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateChatHeader(Student otherStudent) {
        if (otherStudent != null) {
            chatTitleLabel.setText(otherStudent.getName());
            if (otherStudent.getProfileImageUrl() != null) {
                try {
                    Image image = new Image(otherStudent.getProfileImageUrl());
                    chatProfileImage.setImage(image);
                } catch (Exception e) {
                    setDefaultProfileImage();
                }
            } else {
                setDefaultProfileImage();
            }
        } else {
            chatTitleLabel.setText("Unknown User");
            setDefaultProfileImage();
        }
    }

    private void loadMessages(Conversation conversation) {
        messages.clear();
        if (conversation.getMessages() != null) {
            messages.addAll(conversation.getMessages());
            messageListView.scrollTo(messages.size() - 1);
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
     * Handles sending a message.
     */
    private void handleSendMessage() {
        try {
            if (selectedConversation == null || messageField.getText().trim().isEmpty()) {
                return;
            }

            Message message = new Message(
                    currentStudent.getId(),
                    messageField.getText().trim(),
                    new Date(),
                    false
            );

            // Store the currently selected conversation
            Conversation currentConversation = selectedConversation;

            chatService.addMessageToConversation(selectedConversation.getId(), message);
            messages.add(message);
            messageListView.scrollTo(message);
            messageField.clear();

            // Reload conversation to ensure consistency
            loadConversations();

            // Restore the selected conversation
            conversationListView.getSelectionModel().select(
                    conversations.stream()
                            .filter(conv -> conv.getId().equals(currentConversation.getId()))
                            .findFirst()
                            .orElse(null)
            );
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
            // Optionally show error to user
            showError("Failed to send message");
        }
    }

    private void setDefaultProfileImage() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
            chatProfileImage.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Error loading default profile image: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
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
            String name = otherStudent != null ? otherStudent.getName() : "Unknown User";

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

            // Create header with sender name and time
            HBox header = new HBox(10); // 10px spacing

            String senderName;
            if (isSentByMe) {
                senderName = "Me";
                container.getStyleClass().add("message-sent");
                header.setStyle("-fx-alignment: center-right;");
            } else {
                Student sender = StudentService.getInstance().getStudentById(message.getSenderId());
                senderName = sender != null ? sender.getName() : "Unknown User";
                container.getStyleClass().add("message-received");
                header.setStyle("-fx-alignment: center-left;");
            }

            // Create and style sender label
            Label senderLabel = new Label(senderName);
            senderLabel.getStyleClass().add("message-sender");


            // Create and style time label
            Label timeLabel = new Label(message.getTimestamp().format(TIME_FORMATTER));
            timeLabel.getStyleClass().add("message-time");

            header.getChildren().addAll(senderLabel, timeLabel);

            // Create and style message content
            Label contentLabel = new Label(message.getContent());
            contentLabel.setWrapText(true);
            contentLabel.getStyleClass().add("message-content");

            // Add all elements to the container
            container.getChildren().addAll(header, contentLabel);

            setGraphic(container);
//            // Message text
//            Label textLabel = new Label(message.getContent());
//            textLabel.setWrapText(true);
//            textLabel.getStyleClass().add("message-text");
//
//            // Message time
//            Label timeLabel = new Label(message.getTimestamp().format(TIME_FORMATTER));
//            timeLabel.getStyleClass().add("message-time");
//
//            // Add to container
//            container.getChildren().addAll(textLabel, timeLabel);
//
//            setGraphic(container);
        }
    }
}