package studypartner.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a time slot for availability.
 */
public class TimeSlot {
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
    
    public TimeSlot(DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    /**
     * Checks if this time slot overlaps with another.
     * 
     * @param other The other time slot
     * @return True if there is an overlap
     */
    public boolean overlaps(TimeSlot other) {
        if (day != other.day) return false;
        
        return !(endTime.isBefore(other.startTime) || startTime.isAfter(other.endTime));
    }
    
    /**
     * Calculates the duration of this time slot in minutes.
     * 
     * @return Duration in minutes
     */
    public int getDurationMinutes() {
        return (endTime.getHour() * 60 + endTime.getMinute()) -
               (startTime.getHour() * 60 + startTime.getMinute());
    }
    
    /**
     * Calculates the overlap duration with another time slot in minutes.
     * 
     * @param other The other time slot
     * @return Overlap duration in minutes
     */
    public int getOverlapMinutes(TimeSlot other) {
        if (!overlaps(other)) return 0;
        
        LocalTime overlapStart = startTime.isBefore(other.startTime) ? other.startTime : startTime;
        LocalTime overlapEnd = endTime.isAfter(other.endTime) ? other.endTime : endTime;
        
        return (overlapEnd.getHour() * 60 + overlapEnd.getMinute()) -
               (overlapStart.getHour() * 60 + overlapStart.getMinute());
    }
    
    // Getters and setters
    public DayOfWeek getDay() { return day; }
    public void setDay(DayOfWeek day) { this.day = day; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    @Override
    public String toString() {
        return day.toString() + " " + startTime.format(timeFormatter) + " - " + endTime.format(timeFormatter);
    }
}