package src.main.java.studypartner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import studypartner.util.NavigationManager;
import studypartner.util.SessionManager;

import java.io.IOException;

public class StudyPartnerApp extends Application {
    
    private static final int DEFAULT_WIDTH = 900;
    private static final int DEFAULT_HEIGHT = 700;
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        
        // Initialize session manager
        SessionManager.getInstance();
        
        // Initialize navigation manager
        NavigationManager.initialize(primaryStage);
        
        // Load the login view
        FXMLLoader fxmlLoader = new FXMLLoader(StudyPartnerApp.class.getResource("/views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
        
        // Add stylesheet
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        // Configure stage
        primaryStage.setTitle("Study Partner Finder");
        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/app-icon.png")));
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}