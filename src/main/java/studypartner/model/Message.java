package studypartner.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
    
    public Message(String senderId, String content, Date timestamp, Boolean read) {
        this.id = UUID.randomUUID().toString();
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();;
        this.read = read;
    }
    
    // Getters and setters
    public String getId() { return id; }
    
    public String getSenderId() { return senderId; }
    
    public String getContent() { return content; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    
    public boolean isRead() { return read; }
    
    public void setRead(boolean read) { this.read = read; }
}