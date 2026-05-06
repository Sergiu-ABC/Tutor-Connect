
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class Demo {
    static ArrayList<String> mySessionAppointments = new ArrayList();
    static double currentBalance = (double)0.0F;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- SYSTEM STARTUP ---");
        ArrayList<Tutors> allTutors = loadTutorsFromDB();
        System.out.print("Enter your total budget for today (RON): ");
        currentBalance = scanner.nextDouble();

        while(true) {
            System.out.println("\n-----------------------------------------");
            System.out.println("CURRENT BALANCE: " + currentBalance + " RON");
            System.out.println("1. View My Appointments");
            System.out.println("2. Search and Book a Tutor");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    showAppointments();
                    break;
                case 2:
                    bookTutor(scanner, allTutors);
                    break;
                case 3:
                    System.out.println("Exiting system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    public static void showAppointments() {
        System.out.println("\n--- YOUR APPOINTMENTS ---");
        if (mySessionAppointments.isEmpty()) {
            System.out.println("No appointments booked yet.");
        } else {
            for(String app : mySessionAppointments) {
                System.out.println(app);
            }
        }

    }

    public static void bookTutor(Scanner scanner, ArrayList<Tutors> allTutors) {
        System.out.print("\nWhat language do you want to learn?: ");
        String targetLang = scanner.nextLine().trim();
        ArrayList<Tutors> searchResults = new ArrayList();
        System.out.println("\n--- RESULTS FOR: " + targetLang.toUpperCase() + " ---");
        int index = 1;

        for(Tutors t : allTutors) {
            if (t.getProficiency(targetLang) > 0) {
                searchResults.add(t);
                System.out.println("[" + index + "] " + t.toString());
                ++index;
            }
        }

        if (searchResults.isEmpty()) {
            System.out.println("No tutors found.");
        } else {
            System.out.println("-----------------------------------------");
            System.out.print("Enter the number of the tutor to book (or 0 to cancel): ");
            int selection = scanner.nextInt();
            if (selection > 0 && selection <= searchResults.size()) {
                Tutors selectedTutor = (Tutors)searchResults.get(selection - 1);
                if (currentBalance >= selectedTutor.getPrice()) {
                    currentBalance -= selectedTutor.getPrice();
                    String var10000 = selectedTutor.getName();
                    String confirmation = "Booked " + var10000 + " for " + targetLang;
                    mySessionAppointments.add(confirmation);
                    System.out.println("SUCCESS! You have booked the session.");
                    System.out.println("Remaining Balance: " + currentBalance);
                } else {
                    System.out.println("ERROR: Insufficient funds! You need " + selectedTutor.getPrice());
                }
            } else if (selection != 0) {
                System.out.println("Invalid selection.");
            }

        }
    }

    public static ArrayList<Tutors> loadTutorsFromDB() {
        ArrayList<Tutors> list = new ArrayList();

        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return list;
            }

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tutors");

            while(rs.next()) {
                Tutors t = new Tutors(rs.getString("name"), rs.getInt("age"), rs.getDouble("price"));
                Statement langStmt = conn.createStatement();
                ResultSet langRs = langStmt.executeQuery("SELECT * FROM tutor_languages WHERE tutor_id = " + rs.getInt("id"));

                while(langRs.next()) {
                    t.addLanguage(langRs.getString("language"), langRs.getInt("level"));
                }

                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
