
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class TutorRepository {
    public static void initDatabase() {
        try {
            Connection c = DatabaseConnection.getConnection();
            c.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS reviews (id SERIAL PRIMARY KEY, tutor_id INT, rating INT, comment TEXT, student_name VARCHAR(100))");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean registerUser(String u, String p) {
        try {
            Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("INSERT INTO users (username, password, budget) VALUES (?,?, 100)");
            ps.setString(1, u);
            ps.setString(2, p);
            ps.executeUpdate();
            return true;
        } catch (Exception var5) {
            return false;
        }
    }

    public boolean checkLogin(String u, String p) {
        try {
            Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, u);
            ps.setString(2, p);
            return ps.executeQuery().next();
        } catch (Exception var5) {
            return false;
        }
    }

    public void addFunds(String u, double amt) {
        try {
            Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("UPDATE users SET budget=budget+? WHERE username=?");
            ps.setDouble(1, amt);
            ps.setString(2, u);
            ps.executeUpdate();
        } catch (Exception var6) {
        }

    }

    public double getUserBudget(String u) {
        try {
            Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT budget FROM users WHERE username=?");
            ps.setString(1, u);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("budget");
            }
        } catch (Exception var5) {
        }

        return (double)0.0F;
    }

    public ArrayList<Tutors> loadTutors() {
        ArrayList<Tutors> list = new ArrayList();

        try {
            Connection c = DatabaseConnection.getConnection();
            ResultSet rs = c.createStatement().executeQuery("SELECT t.*, COALESCE(AVG(r.rating),0) as avg_rate, COUNT(r.id) as count FROM tutors t LEFT JOIN reviews r ON t.id = r.tutor_id GROUP BY t.id");

            while(rs.next()) {
                Tutors t = new Tutors(rs.getString("name"), rs.getInt("age"), rs.getDouble("price"));
                Statement ls = c.createStatement();
                ResultSet lrs = ls.executeQuery("SELECT * FROM tutor_languages WHERE tutor_id=" + rs.getInt("id"));

                while(lrs.next()) {
                    t.addLanguage(lrs.getString("language"), lrs.getInt("level"));
                }

                list.add(t);
            }
        } catch (Exception var7) {
        }

        return list;
    }

    public int getTutorIdByName(String n) {
        try {
            Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT id FROM tutors WHERE name=?");
            ps.setString(1, n);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception var5) {
        }

        return 0;
    }

    public boolean isTutorBooked(int tid, String d) {
        try {
            Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT id FROM appointments WHERE tutor_id=? AND meeting_date=?");
            ps.setInt(1, tid);
            ps.setString(2, d);
            return ps.executeQuery().next();
        } catch (Exception var5) {
            return false;
        }
    }

    public boolean isStudentBooked(String s, String d) {
        try {
            Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT id FROM appointments WHERE student_name=? AND meeting_date=?");
            ps.setString(1, s);
            ps.setString(2, d);
            return ps.executeQuery().next();
        } catch (Exception var5) {
            return false;
        }
    }

    public double getTutorPrice(int id) {
        try {
            Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT price FROM tutors WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("price");
            }
        } catch (Exception var5) {
        }

        return (double)0.0F;
    }

    public void processPayment(String u, double amt) {
        try {
            Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("UPDATE users SET budget=budget-? WHERE username=?");
            ps.setDouble(1, amt);
            ps.setString(2, u);
            ps.executeUpdate();
        } catch (Exception var6) {
        }

    }

    public void saveAppointment(int tid, String s, String d, int h, double c) {
        try {
            Connection cn = DatabaseConnection.getConnection();
            PreparedStatement ps = cn.prepareStatement("INSERT INTO appointments (tutor_id,student_name,meeting_date,duration,total_cost) VALUES (?,?,?,?,?)");
            ps.setInt(1, tid);
            ps.setString(2, s);
            ps.setString(3, d);
            ps.setInt(4, h);
            ps.setDouble(5, c);
            ps.executeUpdate();
        } catch (Exception var9) {
        }

    }

    public void cancelAppointment(int aid, String u) {
        try {
            Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT total_cost FROM appointments WHERE id=?");
            ps.setInt(1, aid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                this.addFunds(u, rs.getDouble(1));
                c.prepareStatement("DELETE FROM appointments WHERE id=" + aid).executeUpdate();
            }
        } catch (Exception var6) {
        }

    }

    public void saveReview(int tid, int stars, String txt, String sname) {
        try {
            Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("INSERT INTO reviews (tutor_id, rating, comment, student_name) VALUES (?,?,?,?)");
            ps.setInt(1, tid);
            ps.setInt(2, stars);
            ps.setString(3, txt);
            ps.setString(4, sname);
            ps.executeUpdate();
        } catch (Exception var7) {
        }

    }

    public ArrayList<String> getMyAppointments(String u) {
        ArrayList<String> list = new ArrayList();

        try {
            Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT a.*, t.name, t.id as tid FROM appointments a JOIN tutors t ON a.tutor_id = t.id WHERE a.student_name = ? ORDER BY meeting_date DESC");
            ps.setString(1, u);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int appId = rs.getInt("id");
                int tid = rs.getInt("tid");
                String tname = rs.getString("name");
                String date = rs.getString("meeting_date");
                double cost = rs.getDouble("total_cost");
                String reviewForm = "<form method='POST' style='margin-top:10px;display:flex;gap:5px;'><input type='hidden' name='action' value='review'><input type='hidden' name='tutorId' value='" + tid + "'><select name='rating' style='padding:5px;'><option value='5'>★★★★★</option><option value='4'>★★★★</option><option value='3'>★★★</option></select><input name='comment' placeholder='Write review...' style='flex:1;padding:5px;'><button class='btn btn-success' style='font-size:0.8em;'>Rate</button></form>";
                list.add("<div class='booking-item'><div style='flex:1;'><div><b>" + tname + "</b> (" + date + ")</div><div style='color:#666;font-size:0.9em;'>Cost: " + cost + " RON</div>" + reviewForm + "</div><div><form method='POST'><input type='hidden' name='action' value='cancel'><input type='hidden' name='appointmentId' value='" + appId + "'><button class='btn btn-danger'>Cancel</button></form></div></div>");
            }
        } catch (Exception var13) {
        }

        return list;
    }
}
