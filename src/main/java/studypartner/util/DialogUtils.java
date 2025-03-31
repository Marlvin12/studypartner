package src.main.java.studypartner.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Utility class for displaying alerts and dialogs.
 */
public class DialogUtils {
    
    /**
     * Shows an information alert.
     * 
     * @param title The alert title
     * @param header The alert header
     * @param content The alert content
     */
    public static void showInformation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Shows an error alert.
     * 
     * @param title The alert title
     * @param header The alert header
     * @param content The alert content
     */
    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Shows a warning alert.
     * 
     * @param title The alert title
     * @param header The alert header
     * @param content The alert content
     */
    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Shows a confirmation dialog.
     * 
     * @param title The dialog title
     * @param header The dialog header
     * @param content The dialog content
     * @return True if confirmed, false otherwise
     */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}