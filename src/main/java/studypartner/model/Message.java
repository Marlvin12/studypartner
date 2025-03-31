package studypartner.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a message in a conversation.
 */
public class Message {
    private String id;
    private String senderId;
    private String content;
    private LocalDateTime timestamp;
    private boolean read;
    
    public Message(String senderId, String content) {
        this.id = UUID.randomUUID().toString();
        this.senderId = senderId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }
    
    // Getters and setters
    public String getId() { return id; }
    
    public String getSenderId() { return senderId; }
    
    public String getContent() { return content; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    
    public boolean isRead() { return read; }
    
    public void setRead(boolean read) { this.read = read; }
}