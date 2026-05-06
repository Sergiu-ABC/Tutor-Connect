
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TutorController implements HttpHandler {
    private String currentUser = null;
    private final TutorRepository repo = new TutorRepository();
    private final HtmlView view = new HtmlView();

    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        String message = "";
        String messageType = "";
        if (requestMethod.equalsIgnoreCase("POST")) {
            String formData = this.readRequestBody(exchange);
            String action = this.getField(formData, "action");
            String usernameInput = this.getField(formData, "username");
            String passwordInput = this.getField(formData, "password");
            switch (action) {
                case "login":
                    if (this.repo.checkLogin(usernameInput, passwordInput)) {
                        this.currentUser = usernameInput;
                    } else {
                        message = "❌ Login Failed: Wrong username or password.";
                        messageType = "error";
                    }
                    break;
                case "register":
                    if (this.repo.registerUser(usernameInput, passwordInput)) {
                        this.currentUser = usernameInput;
                        message = "✅ Account created! Welcome, " + this.currentUser;
                        messageType = "success";
                    } else {
                        message = "❌ Registration Failed: Username taken.";
                        messageType = "error";
                    }
                    break;
                case "logout":
                    this.currentUser = null;
                    break;
                case "addMoney":
                    double amt = this.parseDouble(this.getField(formData, "amount"));
                    if (amt > (double)0.0F) {
                        this.repo.addFunds(this.currentUser, amt);
                        message = "✅ Added " + amt + " RON.";
                        messageType = "success";
                    } else {
                        message = "❌ Invalid Amount";
                        messageType = "error";
                    }
                    break;
                case "book":
                    int tId = this.parseInt(this.getField(formData, "tutorId"));
                    String date = this.getField(formData, "meetingDate");
                    int hours = this.parseInt(this.getField(formData, "hours"));
                    message = this.handleBooking(tId, date, hours);
                    if (message.contains("✅")) {
                        messageType = "success";
                    } else {
                        messageType = "error";
                    }
                    break;
                case "cancel":
                    int appId = this.parseInt(this.getField(formData, "appointmentId"));
                    this.repo.cancelAppointment(appId, this.currentUser);
                    message = "✅ Refunded.";
                    messageType = "success";
                    break;
                case "review":
                    int rTid = this.parseInt(this.getField(formData, "tutorId"));
                    int rating = this.parseInt(this.getField(formData, "rating"));
                    String comment = this.getField(formData, "comment");
                    this.repo.saveReview(rTid, rating, comment, this.currentUser);
                    message = "✅ Review Submitted!";
                    messageType = "success";
            }
        }

        String response;
        if (this.currentUser == null) {
            response = this.view.renderLoginPage(message, messageType);
        } else if (query != null && query.contains("view=profile")) {
            double budget = this.repo.getUserBudget(this.currentUser);
            ArrayList<String> history = this.repo.getMyAppointments(this.currentUser);
            response = this.view.renderProfilePage(this.currentUser, budget, history, message, messageType);
        } else {
            String filter = query != null && query.contains("lang=") ? query.split("=")[1] : null;
            ArrayList<Tutors> tutors = this.repo.loadTutors();
            double budget = this.repo.getUserBudget(this.currentUser);
            response = this.view.renderMainPage(this.currentUser, budget, tutors, filter, message, messageType, this.repo);
        }

        this.sendResponse(exchange, response);
    }

    private String handleBooking(int tutorId, String date, int hours) {
        if (this.currentUser != null && tutorId > 0 && !date.isEmpty()) {
            if (this.repo.isTutorBooked(tutorId, date)) {
                return "⚠️ Tutor is busy on this date.";
            } else if (this.repo.isStudentBooked(this.currentUser, date)) {
                return "⚠️ You are busy on this date.";
            } else {
                double cost = this.repo.getTutorPrice(tutorId) * (double)hours;
                if (this.repo.getUserBudget(this.currentUser) < cost) {
                    return "❌ Insufficient Funds.";
                } else {
                    this.repo.processPayment(this.currentUser, cost);
                    this.repo.saveAppointment(tutorId, this.currentUser, date, hours, cost);
                    return "✅ Booked!";
                }
            }
        } else {
            return "❌ Invalid Data";
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        return br.readLine();
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, (long)bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private String getField(String formData, String targetKey) {
        if (formData == null) {
            return "";
        } else {
            for(String pair : formData.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2 && kv[0].equals(targetKey)) {
                    return URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                }
            }

            return "";
        }
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception var3) {
            return 0;
        }
    }

    private double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception var3) {
            return (double)0.0F;
        }
    }
}
