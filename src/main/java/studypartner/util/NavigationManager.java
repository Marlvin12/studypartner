package studypartner.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import studypartner.StudyPartnerApp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to handle navigation between screens.
 */
public class NavigationManager {
    private static Stage primaryStage;
    private static Map<String, Object> navigationData = new HashMap<>();
    
    /**
     * Initializes the NavigationManager with the primary stage.
     * 
     * @param stage The primary stage
     */
    public static void initialize(Stage stage) {
        primaryStage = stage;
    }
    
    /**
     * Navigates to a view.
     * 
     * @param fxmlPath The path to the FXML file
     */
    public static void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(StudyPartnerApp.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = primaryStage.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error navigating to: " + fxmlPath);
        }
    }
    
    /**
     * Sets data to be passed between screens.
     * 
     * @param key The data key
     * @param value The data value
     */
    public static void setData(String key, Object value) {
        navigationData.put(key, value);
    }
    
    /**
     * Gets data passed between screens.
     * 
     * @param key The data key
     * @return The data value
     */
    public static Object getData(String key) {
        return navigationData.get(key);
    }
    
    /**
     * Clears data for a key.
     * 
     * @param key The data key
     */
    public static void clearData(String key) {
        navigationData.remove(key);
    }
    
    /**
     * Clears all navigation data.
     */
    public static void clearAllData() {
        navigationData.clear();
    }
}

