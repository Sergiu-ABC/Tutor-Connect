
public class Review {
    private String studentName;
    private int rating;
    private String text;

    public Review(String studentName, int rating, String text) {
        this.studentName = studentName;
        this.rating = rating;
        this.text = text;
    }

    public String toString() {
        return "   ★" + this.rating + " - " + this.text + " (" + this.studentName + ")";
    }
}
