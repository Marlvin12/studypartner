package studypartner.model;

/**
 * Represents an academic subject with a code and name.
 */
public class Subject {
    private String code;
    private String name;
    
    /**
     * Constructs a new Subject with the given code and name.
     * 
     * @param code The subject code (e.g., "CS101")
     * @param name The subject name (e.g., "Introduction to Programming")
     */
    public Subject(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    /**
     * Gets the subject code.
     * 
     * @return The subject code
     */
    public String getCode() { 
        return code; 
    }
    
    /**
     * Sets the subject code.
     * 
     * @param code The subject code to set
     */
    public void setCode(String code) { 
        this.code = code; 
    }
    
    /**
     * Gets the subject name.
     * 
     * @return The subject name
     */
    public String getName() { 
        return name; 
    }
    
    /**
     * Sets the subject name.
     * 
     * @param name The subject name to set
     */
    public void setName(String name) { 
        this.name = name; 
    }
    
    /**
     * Returns a string representation of the subject.
     * 
     * @return A string in the format "code - name"
     */
    @Override
    public String toString() {
        return code + " - " + name;
    }
    
    /**
     * Compares this subject with another object for equality.
     * Subjects are considered equal if they have the same code.
     * 
     * @param obj The object to compare with
     * @return True if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Subject subject = (Subject) obj;
        return code.equals(subject.code);
    }
    
    /**
     * Returns a hash code value for the subject.
     * 
     * @return A hash code based on the subject code
     */
    @Override
    public int hashCode() {
        return code.hashCode();
    }
}