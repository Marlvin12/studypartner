package studypartner.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import studypartner.model.Student;
import studypartner.model.Subject;
import studypartner.model.TimeSlot;

import org.bson.Document;
import studypartner.util.MongoDBClient;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class StudentService {
    private static StudentService instance;
    private List<Student> students = new ArrayList<>();
    private final MongoCollection<Document> studentCollection;

    private StudentService() {
        MongoDatabase db = MongoDBClient.getDatabase();
        studentCollection = db.getCollection("students");
    }
    
    public static synchronized StudentService getInstance() {
        if (instance == null) {
            instance = new StudentService();
        }
        return instance;
    }

    public CompletableFuture<Student> authenticateAsync(String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            Document query = new Document("email", email).append("password", password);
            Document result = studentCollection.find(query).first();
            if (result != null) {
                return mapDocumentToStudent(result);
            }
            return null;

        });
    }

    private Student mapDocumentToStudent(Document doc) {
        Student student = new Student(
                doc.getObjectId("_id").toString(),
                doc.getString("name"),
                doc.getString("email"),
                doc.getString("password"),
                doc.getString("major"),
                doc.getInteger("yearOfStudy", 1),
                doc.getString("bio")
        );
        student.setProfileImageUrl(doc.getString("profileImageUrl"));

        List<Document> subjectDocs = doc.getList("subjects", Document.class);
        if (subjectDocs != null) {
            List<Subject> subjects = subjectDocs.stream()
                    .map(subjectDoc -> new Subject(
                            subjectDoc.getString("name"),
                            subjectDoc.getString("code")
                    ))
                    .collect(Collectors.toList());
            student.setSubjects(subjects);
        }

        List<Document> availabilityDocs = doc.getList("availability", Document.class);
        if (availabilityDocs != null) {
            List<TimeSlot> availability = availabilityDocs.stream()
                    .map(timeSlotDoc -> new TimeSlot(
                            DayOfWeek.valueOf(timeSlotDoc.getString("dayOfWeek")),
                            LocalTime.parse(timeSlotDoc.getString("startTime")),
                            LocalTime.parse(timeSlotDoc.getString("endTime"))
                    ))
                    .collect(Collectors.toList());
            student.setAvailability(availability);
        }
        return student;
    }

    public Student authenticate(String email, String password) {
        return students.stream()
                .filter(s -> s.getEmail().equals(email) && s.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }


    public Student getStudentById(String id) {
        Document query = new Document("_id", new ObjectId(id));
        Document result = studentCollection.find(query).first();
        return result != null ? mapDocumentToStudent(result) : null;
    }

    public List<Student> getOtherStudents(String currentStudentId) {
        Document query = new Document("_id", new Document("$ne", new ObjectId(currentStudentId)));
        List<Student> otherStudents = new ArrayList<>();
        studentCollection.find(query).forEach(doc ->
                otherStudents.add(mapDocumentToStudent(doc))
        );
        return otherStudents;
    }

    public void addStudent(Student student) {
        Document doc = new Document()
                .append("name", student.getName())
                .append("email", student.getEmail())
                .append("password", student.getPassword())
                .append("major", student.getMajor())
                .append("yearOfStudy", student.getYearOfStudy())
                .append("bio", student.getBio())
                .append("profileImageUrl", student.getProfileImageUrl());

        // Convert subjects to documents
        List<Document> subjectDocs = student.getSubjects().stream()
                .map(subject -> new Document()
                        .append("name", subject.getName())
                        .append("code", subject.getCode()))
                .collect(Collectors.toList());
        doc.append("subjects", subjectDocs);

        studentCollection.insertOne(doc);
    }

    public void updateStudent(Student student) {
        Document query = new Document("_id", new ObjectId(student.getId()));

        // Convert subjects to documents
        List<Document> subjectDocs = student.getSubjects().stream()
                .map(subject -> new Document()
                        .append("name", subject.getName())
                        .append("code", subject.getCode()))
                .collect(Collectors.toList());

        // Convert TimeSlots to documents
        List<Document> availabilityDocs = student.getAvailability().stream()
                .map(timeSlot -> new Document()
                        .append("dayOfWeek", timeSlot.getDay().toString())
                        .append("startTime", timeSlot.getStartTime().toString())
                        .append("endTime", timeSlot.getEndTime().toString()))
                .collect(Collectors.toList());

        // All updates go under $set
        Document setData = new Document()
                .append("name", student.getName())
                .append("email", student.getEmail())
                .append("password", student.getPassword())
                .append("major", student.getMajor())
                .append("yearOfStudy", student.getYearOfStudy())
                .append("bio", student.getBio())
                .append("profileImageUrl", student.getProfileImageUrl())
                .append("subjects", subjectDocs)
                .append("availability", availabilityDocs);


        Document update = new Document("$set", setData);

        studentCollection.updateOne(query, update);
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
        Set<Subject> uniqueSubjects = new HashSet<>();
        studentCollection.find().forEach(doc -> {
            List<Document> subjectDocs = doc.getList("subjects", Document.class);
            if (subjectDocs != null) {
                subjectDocs.forEach(subjectDoc -> {
                    Subject subject = new Subject(
                            subjectDoc.getString("name"),
                            subjectDoc.getString("code")
                    );
                    uniqueSubjects.add(subject);
                });
            }
        });
        return new ArrayList<>(uniqueSubjects);
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