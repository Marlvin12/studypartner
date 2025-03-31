package studypartner.util;

import studypartner.model.Student;

/**
 * Singleton class to manage the current user session.
 */
public class SessionManager {
    private static SessionManager instance;
    private Student currentStudent;
    
    /**
     * Private constructor for singleton pattern.
     * Prevents direct instantiation from outside the class.
     */
    private SessionManager() {
    }
    
    /**
     * Gets the singleton instance.
     * 
     * @return The SessionManager instance
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Sets the current student.
     * 
     * @param student The current student
     */
    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
    }
    
    /**
     * Gets the current student.
     * 
     * @return The current student
     */
    public Student getCurrentStudent() {
        return currentStudent;
    }
    
    /**
     * Clears the current session.
     */
    public void logout() {
        currentStudent = null;
    }
    
    /**
     * Checks if a user is logged in.
     * 
     * @return True if a user is logged in
     */
    public boolean isLoggedIn() {
        return currentStudent != null;
    }
}