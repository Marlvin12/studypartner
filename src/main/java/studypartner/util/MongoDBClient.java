package studypartner.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBClient {
    private static final String CONNECTION_STRING = "";
    private static final String DATABASE_NAME = "studypartner";
    private static MongoClient client;

    public static MongoDatabase getDatabase() {
        if (client == null) {
            client = MongoClients.create(CONNECTION_STRING);
        }
        return client.getDatabase(DATABASE_NAME);
    }
}
