package studypartner.service;

import studypartner.model.Student;
import studypartner.model.Subject;
import studypartner.model.TimeSlot;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StudentService {
    private static StudentService instance;
    private List<Student> students = new ArrayList<>();
    
    private StudentService() {
        initializeSampleData();
    }
    
    public static synchronized StudentService getInstance() {
        if (instance == null) {
            instance = new StudentService();
        }
        return instance;
    }
    
    private void initializeSampleData() {
        Subject math101 = new Subject("MATH101", "Introduction to Calculus");
        Subject cs201 = new Subject("CS201", "Data Structures");
        Subject cs301 = new Subject("CS301", "Algorithms");
        Subject phys201 = new Subject("PHYS201", "Physics II");
        Subject chem101 = new Subject("CHEM101", "General Chemistry");
        Subject bio201 = new Subject("BIO201", "Cell Biology");
        
        Student alice = new Student("1", "Alice Johnson", "alice@example.com", "password", 
                "Computer Science", 2, "I'm a CS major looking for study partners for algorithm courses.");
        alice.addSubject(math101);
        alice.addSubject(cs201);
        alice.addSubject(cs301);
        alice.addTimeSlot(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)));
        alice.addTimeSlot(new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(16, 0)));
        alice.setProfileImageUrl("/images/profile-1.png");
        
        Student bob = new Student("2", "Bob Smith", "bob@example.com", "password",
                "Computer Science", 3, "Looking to ace my programming assignments. Happy to help others too!");
        bob.addSubject(cs201);
        bob.addSubject(cs301);
        bob.addTimeSlot(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(13, 0)));
        bob.addTimeSlot(new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(11, 0)));
        bob.setProfileImageUrl("/images/profile-2.png");
        
        students.addAll(Arrays.asList(alice, bob));
    }
    
    public Student authenticate(String email, String password) {
        return students.stream()
                .filter(s -> s.getEmail().equals(email) && s.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
    
    public Student getStudentById(String id) {
        return students.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public List<Student> getOtherStudents(String currentStudentId) {
        return students.stream()
                .filter(s -> !s.getId().equals(currentStudentId))
                .collect(Collectors.toList());
    }
    
    public void addStudent(Student student) {
        students.add(student);
    }
    
    public void updateStudent(Student student) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(student.getId())) {
                students.set(i, student);
                break;
            }
        }
    }
    
    public List<MatchResult> findMatches(String studentId) {
        Student currentStudent = getStudentById(studentId);
        if (currentStudent == null) return new ArrayList<>();
        
        List<MatchResult> matches = new ArrayList<>();
        for (Student other : getOtherStudents(studentId)) {
            int compatibility = currentStudent.calculateCompatibility(other);
            matches.add(new MatchResult(other, compatibility));
        }
        
        matches.sort((m1, m2) -> Integer.compare(m2.getCompatibility(), m1.getCompatibility()));
        return matches;
    }
    
    public List<Subject> getAllSubjects() {
        List<Subject> allSubjects = new ArrayList<>();
        for (Student student : students) {
            for (Subject subject : student.getSubjects()) {
                if (!allSubjects.contains(subject)) {
                    allSubjects.add(subject);
                }
            }
        }
        return allSubjects;
    }
    
    public static class MatchResult {
        private Student student;
        private int compatibility;
        
        public MatchResult(Student student, int compatibility) {
            this.student = student;
            this.compatibility = compatibility;
        }
        
        public Student getStudent() { return student; }
        public int getCompatibility() { return compatibility; }
    }
}