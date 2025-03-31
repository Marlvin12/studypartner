package studypartner.util;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Utility class for form validation.
 */
public class ValidationUtils {
    
    /**
     * Validates that a field is not empty.
     * 
     * @param field The field to validate
     * @param fieldName The field name for error message
     * @return True if valid, false otherwise
     */
    public static boolean validateNotEmpty(TextField field, String fieldName) {
        if (field.getText().trim().isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            DialogUtils.showError("Validation Error", "Empty Field", 
                    fieldName + " cannot be empty.");
            return false;
        }
        field.setStyle("");
        return true;
    }
    
    /**
     * Validates an email address.
     * 
     * @param field The field to validate
     * @return True if valid, false otherwise
     */
    public static boolean validateEmail(TextField field) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        
        if (!pattern.matcher(field.getText().trim()).matches()) {
            field.setStyle("-fx-border-color: red;");
            DialogUtils.showError("Validation Error", "Invalid Email", 
                    "Please enter a valid email address.");
            return false;
        }
        field.setStyle("");
        return true;
    }
    
    /**
     * Validates a password.
     * 
     * @param field The field to validate
     * @return True if valid, false otherwise
     */
    public static boolean validatePassword(TextField field) {
        String password = field.getText().trim();
        
        if (password.length() < 6) {
            field.setStyle("-fx-border-color: red;");
            DialogUtils.showError("Validation Error", "Invalid Password", 
                    "Password must be at least 6 characters long.");
            return false;
        }
        field.setStyle("");
        return true;
    }
    
    /**
     * Creates a text formatter that only allows numeric input.
     * 
     * @return The text formatter
     */
    public static TextFormatter<String> createNumericTextFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        return new TextFormatter<>(filter);
    }
    
    /**
     * Validates multiple fields.
     * 
     * @param validations Array of validation results
     * @return True if all validations pass, false otherwise
     */
    public static boolean validateAll(boolean... validations) {
        for (boolean valid : validations) {
            if (!valid) {
                return false;
            }
        }
        return true;
    }
}