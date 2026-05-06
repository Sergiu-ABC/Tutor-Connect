public class Appointment {
    private int id;
    private int tutorId;
    private String tutorName;
    private String studentName;
    private String meetingDate;
    private int duration;
    private double totalCost;

    public Appointment(int id, int tutorId, String tutorName, String studentName, String meetingDate, int duration, double totalCost) {
        this.id = id;
        this.tutorId = tutorId;
        this.tutorName = tutorName;
        this.studentName = studentName;
        this.meetingDate = meetingDate;
        this.duration = duration;
        this.totalCost = totalCost;
    }

    public String toString() {
        return "   [ID: " + this.id + "] " + this.meetingDate + " with " + this.tutorName + " (" + this.duration + " hrs) - Cost: " + this.totalCost + " RON";
    }
}
