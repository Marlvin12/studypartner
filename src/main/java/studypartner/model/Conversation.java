package studypartner.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a conversation between two students.
 */
public class Conversation {
    private String id;
    private String student1Id;
    private String student2Id;
    private List<Message> messages = new ArrayList<>();
    private LocalDateTime lastMessageTime;
    
    public Conversation(String student1Id, String student2Id) {
        this.id = UUID.randomUUID().toString();
        this.student1Id = student1Id;
        this.student2Id = student2Id;
        this.lastMessageTime = LocalDateTime.now();
    }
    
    public void addMessage(Message message) {
        messages.add(message);
        lastMessageTime = message.getTimestamp();
    }
    
    // Getters and setters
    public String getId() { return id; }
    
    public String getStudent1Id() { return student1Id; }
    
    public String getStudent2Id() { return student2Id; }
    
    public List<Message> getMessages() { return messages; }
    
    public LocalDateTime getLastMessageTime() { return lastMessageTime; }
    
    /**
     * Gets the ID of the other student in the conversation.
     * 
     * @param currentStudentId ID of the current student
     * @return ID of the other student
     */
    public String getOtherStudentId(String currentStudentId) {
        return currentStudentId.equals(student1Id) ? student2Id : student1Id;
    }
}