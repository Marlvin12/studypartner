package studypartner.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import studypartner.model.Conversation;
import studypartner.model.Message;
import studypartner.util.MongoDBClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing conversations and messages.
 */
public class ChatService {
    private static ChatService instance;
    private final MongoCollection<Document> conversationCollection;
    
    // Private constructor for singleton
    private ChatService() {
        // Initialize with some sample data
        MongoDatabase db = MongoDBClient.getDatabase();
        conversationCollection = db.getCollection("conversations");
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
     * Gets all conversations for a student.
     * 
     * @param studentId The student ID
     * @return List of conversations
     */
    public List<Conversation> getConversationsForStudent(String studentId) {
        List<Conversation> conversations = new ArrayList<>();
        Document query = new Document("$or", List.of(
                new Document("student1Id", studentId),
                new Document("student2Id", studentId)
        ));

        conversationCollection.find(query).forEach(doc ->
                conversations.add(mapDocumentToConversation(doc))
        );
        return conversations;
    }


    /**
     * Gets a conversation by ID.
     * 
     * @param conversationId The conversation ID
     * @return The conversation or null if not found
     */
    public Conversation getConversationById(String conversationId) {
        Document query = new Document("_id", new ObjectId(conversationId));
        Document doc = conversationCollection.find(query).first();
        return doc != null ? mapDocumentToConversation(doc) : null;
    }
    /**
     * Gets or creates a conversation between two students.
     * 
     * @param student1Id The first student ID
     * @param student2Id The second student ID
     * @return The existing or new conversation
     */
    public Conversation getOrCreateConversation(String student1Id, String student2Id) {
        // Check if conversation exists
        Document query = new Document("$or", List.of(
                new Document("student1Id", student1Id).append("student2Id", student2Id),
                new Document("student1Id", student2Id).append("student2Id", student1Id)
        ));

        Document existingConversation = conversationCollection.find(query).first();
        if (existingConversation != null) {
            return mapDocumentToConversation(existingConversation);
        }

        // Create new conversation
        Document newConversation = new Document()
                .append("student1Id", student1Id)
                .append("student2Id", student2Id)
                .append("messages", new ArrayList<>())
                .append("createdAt", Instant.now());

        conversationCollection.insertOne(newConversation);
        return mapDocumentToConversation(newConversation);
    }
    
    /**
     * Adds a message to a conversation.
     * 
     * @param conversationId The conversation ID
     * @param message The message to add
     */
    public void addMessageToConversation(String conversationId, Message message) {
        Document messageDoc = new Document()
                .append("senderId", message.getSenderId())
                .append("content", message.getContent())
                .append("timestamp", message.getTimestamp())
                .append("read", message.isRead());

        conversationCollection.updateOne(
                new Document("_id", new ObjectId(conversationId)),
                Updates.push("messages", messageDoc)
        );
    }
    
    /**
     * Marks all messages in a conversation as read for a student.
     * 
     * @param conversationId The conversation ID
     * @param studentId The student ID
     */
    public void markConversationAsRead(String conversationId, String studentId) {
        Document query = new Document("_id", new ObjectId(conversationId));
        Document update = new Document("$set", new Document(
                "messages.$[elem].read", true
        ));

        Document arrayFilter = new Document("elem.senderId",
                new Document("$ne", studentId))
                .append("elem.read", false);

        conversationCollection.updateOne(query, update,
                new com.mongodb.client.model.UpdateOptions()
                        .arrayFilters(List.of(arrayFilter)));
    }
    
    /**
     * Gets the number of unread messages for a student.
     * 
     * @param studentId The student ID
     * @return Number of unread messages
     */
    public int getUnreadMessageCount(String studentId) {
        int count = 0;
        List<Conversation> conversations = getConversationsForStudent(studentId);

        for (Conversation conversation : conversations) {
            count += (int) conversation.getMessages().stream()
                    .filter(m -> !m.getSenderId().equals(studentId) && !m.isRead())
                    .count();
        }
        return count;
    }

    private Conversation mapDocumentToConversation(Document doc) {
        Conversation conversation = new Conversation(
                doc.getObjectId("_id").toString(),
                doc.getString("student1Id"),
                doc.getString("student2Id")
        );

        List<Document> messageDocs = doc.getList("messages", Document.class);
        if (messageDocs != null) {
            List<Message> messages = messageDocs.stream()
                    .map(messageDoc -> new Message(
                            messageDoc.getString("senderId"),
                            messageDoc.getString("content"),
                            messageDoc.getDate("timestamp"),
                            messageDoc.getBoolean("read", false)
                    ))
                    .collect(Collectors.toList());
            conversation.setMessages(messages);
        }

        return conversation;
    }
}