package studypartner.service;

import studypartner.model.Conversation;
import studypartner.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing conversations and messages.
 */
public class ChatService {
    private static ChatService instance;
    private List<Conversation> conversations = new ArrayList<>();
    
    // Private constructor for singleton
    private ChatService() {
        // Initialize with some sample data
        initializeSampleData();
    }
    
    /**
     * Gets the singleton instance.
     * 
     * @return The ChatService instance
     */
    public static synchronized ChatService getInstance() {
        if (instance == null) {
            instance = new ChatService();
        }
        return instance;
    }
    
    /**
     * Initializes sample data for testing.
     */
    private void initializeSampleData() {
        // Create a conversation between students 1 and 2
        Conversation conversation1 = new Conversation("1", "2");
        conversation1.addMessage(new Message("1", "Hi Bob, are you free to study CS201 together?"));
        conversation1.addMessage(new Message("2", "Sure Alice! How about Monday at noon?"));
        conversation1.addMessage(new Message("1", "That works for me. Let's meet at the library."));
        
        // Create a conversation between students 1 and 3
        Conversation conversation2 = new Conversation("1", "3");
        conversation2.addMessage(new Message("1", "Hi Carol, would you like to form a study group for Math101?"));
        conversation2.addMessage(new Message("3", "That sounds great! When were you thinking?"));
        
        // Add conversations to list
        conversations.add(conversation1);
        conversations.add(conversation2);
    }
    
    /**
     * Gets all conversations for a student.
     * 
     * @param studentId The student ID
     * @return List of conversations
     */
    public List<Conversation> getConversationsForStudent(String studentId) {
        return conversations.stream()
                .filter(c -> c.getStudent1Id().equals(studentId) || c.getStudent2Id().equals(studentId))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets a conversation by ID.
     * 
     * @param conversationId The conversation ID
     * @return The conversation or null if not found
     */
    public Conversation getConversationById(String conversationId) {
        return conversations.stream()
                .filter(c -> c.getId().equals(conversationId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Gets or creates a conversation between two students.
     * 
     * @param student1Id The first student ID
     * @param student2Id The second student ID
     * @return The existing or new conversation
     */
    public Conversation getOrCreateConversation(String student1Id, String student2Id) {
        // Check if conversation already exists
        for (Conversation conversation : conversations) {
            if ((conversation.getStudent1Id().equals(student1Id) && conversation.getStudent2Id().equals(student2Id)) ||
                (conversation.getStudent1Id().equals(student2Id) && conversation.getStudent2Id().equals(student1Id))) {
                return conversation;
            }
        }
        
        // Create new conversation
        Conversation newConversation = new Conversation(student1Id, student2Id);
        conversations.add(newConversation);
        return newConversation;
    }
    
    /**
     * Adds a message to a conversation.
     * 
     * @param conversationId The conversation ID
     * @param message The message to add
     */
    public void addMessageToConversation(String conversationId, Message message) {
        Conversation conversation = getConversationById(conversationId);
        if (conversation != null) {
            conversation.addMessage(message);
        }
    }
    
    /**
     * Marks all messages in a conversation as read for a student.
     * 
     * @param conversationId The conversation ID
     * @param studentId The student ID
     */
    public void markConversationAsRead(String conversationId, String studentId) {
        Conversation conversation = getConversationById(conversationId);
        if (conversation != null) {
            for (Message message : conversation.getMessages()) {
                if (!message.getSenderId().equals(studentId) && !message.isRead()) {
                    message.setRead(true);
                }
            }
        }
    }
    
    /**
     * Gets the number of unread messages for a student.
     * 
     * @param studentId The student ID
     * @return Number of unread messages
     */
    public int getUnreadMessageCount(String studentId) {
        int count = 0;
        for (Conversation conversation : getConversationsForStudent(studentId)) {
            for (Message message : conversation.getMessages()) {
                if (!message.getSenderId().equals(studentId) && !message.isRead()) {
                    count++;
                }
            }
        }
        return count;
    }
}