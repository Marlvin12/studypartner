package studypartner.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Student {
    private String id;
    private String name;
    private String email;
    private String password;
    private String major;
    private int yearOfStudy;
    private String bio;
    private List<Subject> subjects = new ArrayList<>();
    private List<TimeSlot> availability = new ArrayList<>();
    private String profileImageUrl;
    
    public Student(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }
    
    public Student(String id, String name, String email, String password, 
                   String major, int yearOfStudy, String bio) {
        this(id, name, email, password);
        this.major = major;
        this.yearOfStudy = yearOfStudy;
        this.bio = bio;
    }
    
    public int calculateCompatibility(Student other) {
        int subjectScore = calculateSubjectOverlap(other);
        int timeScore = calculateTimeOverlap(other);
        return (int)(subjectScore * 0.6 + timeScore * 0.4);
    }
    
    private int calculateSubjectOverlap(Student other) {
        if (subjects.isEmpty() || other.subjects.isEmpty()) return 0;
        
        Set<String> mySubjectCodes = new HashSet<>();
        for (Subject subject : subjects) {
            mySubjectCodes.add(subject.getCode());
        }
        
        int matches = 0;
        for (Subject theirSubject : other.subjects) {
            if (mySubjectCodes.contains(theirSubject.getCode())) {
                matches++;
            }
        }
        
        int minSize = Math.min(subjects.size(), other.subjects.size());
        return (minSize > 0) ? (matches * 100) / minSize : 0;
    }
    
    private int calculateTimeOverlap(Student other) {
        if (availability.isEmpty() || other.availability.isEmpty()) return 0;
        
        int totalOverlapMinutes = 0;
        int totalAvailableMinutes = 0;
        
        for (TimeSlot mySlot : availability) {
            int slotMinutes = mySlot.getDurationMinutes();
            totalAvailableMinutes += slotMinutes;
            
            for (TimeSlot theirSlot : other.availability) {
                if (mySlot.overlaps(theirSlot)) {
                    totalOverlapMinutes += mySlot.getOverlapMinutes(theirSlot);
                }
            }
        }
        
        return (totalAvailableMinutes > 0) ? (totalOverlapMinutes * 100) / totalAvailableMinutes : 0;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { this.subjects = subjects; }
    public void addSubject(Subject subject) { this.subjects.add(subject); }
    public List<TimeSlot> getAvailability() { return availability; }
    public void setAvailability(List<TimeSlot> availability) { this.availability = availability; }
    public void addTimeSlot(TimeSlot timeSlot) { this.availability.add(timeSlot); }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}