
import java.util.ArrayList;

public class Tutors {
    private String name;
    private int age;
    private double price;
    private ArrayList<String> languages = new ArrayList();
    private ArrayList<String> reviews = new ArrayList();
    private double totalRatingPoints = (double)0.0F;
    private int numberOfRatings = 0;

    public Tutors(String name, int age, double price) {
        this.name = name;
        this.age = age;
        this.price = price;
        this.addRating((int)(Math.random() * (double)3.0F) + 3, "Initial feedback");
    }

    public String getInitials() {
        if (this.name != null && !this.name.isEmpty()) {
            String[] parts = this.name.split(" ");
            if (parts.length == 1) {
                return parts[0].substring(0, 1).toUpperCase();
            } else {
                String var10000 = parts[0].substring(0, 1);
                return (var10000 + parts[1].substring(0, 1)).toUpperCase();
            }
        } else {
            return "?";
        }
    }

    public void addLanguage(String lang, int level) {
        this.languages.add(lang + " (Lvl " + level + ")");
    }

    public String getLanguagesString() {
        if (this.languages.isEmpty()) {
            return "None";
        } else {
            String listStr = this.languages.toString();
            return listStr.substring(1, listStr.length() - 1);
        }
    }

    public void addRating(int stars, String feedback) {
        this.totalRatingPoints += (double)stars;
        ++this.numberOfRatings;
        this.reviews.add(feedback);
    }

    public int getProficiency(String targetLang) {
        for(String l : this.languages) {
            if (l.toLowerCase().contains(targetLang.toLowerCase())) {
                return 1;
            }
        }

        return 0;
    }

    public double getPrice() {
        return this.price;
    }

    public String getName() {
        return this.name;
    }

    public String getStarDisplay() {
        if (this.numberOfRatings == 0) {
            return "No ratings";
        } else {
            double avg = this.totalRatingPoints / (double)this.numberOfRatings;
            String stars = "";

            for(int i = 0; i < 5; ++i) {
                if (i < (int)avg) {
                    stars = stars + "★";
                } else {
                    stars = stars + "☆";
                }
            }

            return stars + " (" + String.format("%.1f", avg) + ")";
        }
    }

    public String toString() {
        String var10000 = this.name;
        return var10000 + " | " + this.age + " yrs | " + this.price + " RON/hr | " + this.getStarDisplay() + "\n      Languages: " + String.valueOf(this.languages);
    }
}
