import java.util.ArrayList;

public class HtmlView {
    public String renderLoginPage(String message, String msgType) {
        String msgHtml = message.isEmpty() ? "" : "<div class='alert " + msgType + "'>" + message + "</div>";
        return "<html><head><title>Login</title><link href='https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap' rel='stylesheet'><style>body { font-family: 'Poppins', sans-serif; background: linear-gradient(135deg, #667eea, #764ba2); height: 100vh; margin: 0; display: flex; align-items: center; justify-content: center; } .card { background: white; padding: 40px; border-radius: 15px; width: 350px; text-align: center; box-shadow: 0 10px 25px rgba(0,0,0,0.2); } h2 { color: #333; margin-bottom: 20px; } input { width: 100%; padding: 12px; margin: 10px 0; border: 1px solid #ddd; border-radius: 8px; box-sizing: border-box; } button { width: 100%; padding: 12px; border: none; border-radius: 8px; font-weight: bold; cursor: pointer; margin-top: 10px; color: white; transition: 0.3s; } .btn-login { background: #667eea; } .btn-login:hover { background: #5a6fd6; } .btn-reg { background: #27ae60; } .btn-reg:hover { background: #219150; } .alert { padding: 10px; border-radius: 5px; margin-bottom: 15px; font-size: 0.9em; } .alert.success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; } .alert.error { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; } </style></head><body><div class='card'><h2>\ud83c\udf93 Tutor Connect</h2>" + msgHtml + "<form method='POST'>  <input type='hidden' name='action' value='login'>  <input name='username' placeholder='Username' required>  <input type='password' name='password' placeholder='Password' required>  <button class='btn-login'>Sign In</button></form><hr style='margin:25px 0; border:0; border-top:1px solid #eee;'><p style='color:#666; font-size:0.9em'>New here?</p><form method='POST'>  <input type='hidden' name='action' value='register'>  <input name='username' placeholder='Choose Username' required>  <input type='password' name='password' placeholder='Choose Password' required>  <button class='btn-reg'>Create Account</button></form></div></body></html>";
    }

    public String renderMainPage(String currentUser, double budget, ArrayList<Tutors> tutors, String filter, String msg, String type, TutorRepository repo) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Tutor Connect</title>").append(this.getCommonStyles()).append("</head><body>");
        html.append("<div class='navbar'><div class='brand'>\ud83c\udf93 Tutor Connect</div><div class='user-panel'><a href='/?view=profile' style='color:#333;text-decoration:none;font-weight:600;'>Hello, " + currentUser + "</a><span class='badge badge-money'>" + String.format("%.2f", budget) + " RON</span><form method='POST' style='margin:0;display:flex;gap:5px;'><input type='hidden' name='action' value='addMoney'><input type='number' name='amount' class='input-sm' placeholder='Amount' min='1' required><button class='btn btn-success'>+</button></form><form method='POST' style='margin:0;'><input type='hidden' name='action' value='logout'><button class='btn btn-danger'>Logout</button></form></div></div>");
        html.append("<div class='container'>");
        if (!msg.isEmpty()) {
            html.append("<div class='alert " + type + "'>" + msg + "</div>");
        }

        html.append("<h2>\ud83d\udd0d Find a Tutor</h2><form method='GET' class='search-box'><input name='lang' placeholder='Search Language...' style='flex:1;'><button class='btn btn-primary'>Search</button></form>");
        html.append("<div class='grid-layout'>");

        for(Tutors t : tutors) {
            if (filter == null || filter.isEmpty() || t.getProficiency(filter) != 0) {
                String var10001 = t.getName();
                html.append("<div class='card'><div class='card-header'><div><div style='font-size:1.1rem;font-weight:bold;'>" + var10001 + "</div><div style='color:#f39c12;'>" + t.getStarDisplay() + "</div></div><div style='font-weight:bold;'>" + t.getPrice() + " RON/h</div></div>");
                html.append("<div class='card-body'><p><b>Skills:</b> " + t.getLanguagesString() + "</p></div>");
                int var12 = repo.getTutorIdByName(t.getName());
                html.append("<div class='card-footer'><form method='POST' style='display:flex;gap:10px;'><input type='hidden' name='action' value='book'><input type='hidden' name='tutorId' value='" + var12 + "'><input type='date' name='meetingDate' required style='flex:1;'><select name='hours'><option value='1'>1h</option><option value='2'>2h</option><option value='3'>3h</option></select><button class='btn btn-primary'>Book</button></form></div></div>");
            }
        }

        html.append("</div></div></body></html>");
        return html.toString();
    }

    public String renderProfilePage(String currentUser, double budget, ArrayList<String> history, String msg, String type) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Profile</title>").append(this.getCommonStyles()).append("</head><body>");
        html.append("<div class='navbar'><div class='brand'>\ud83c\udf93 Tutor Connect</div><div class='user-panel'><a href='/' class='btn btn-primary' style='text-decoration:none;'>⬅ Dashboard</a><form method='POST' style='margin:0;'><input type='hidden' name='action' value='logout'><button class='btn btn-danger'>Logout</button></form></div></div>");
        html.append("<div class='container' style='max-width:800px;'>");
        if (!msg.isEmpty()) {
            html.append("<div class='alert " + type + "'>" + msg + "</div>");
        }

        html.append("<div class='card' style='text-align:center;padding:30px;margin-bottom:30px;'><h1 style='margin:0;'>" + currentUser + "</h1><p style='color:#666;'>Student Account</p><h2 style='color:#27ae60;'>" + String.format("%.2f", budget) + " RON</h2></div>");
        html.append("<h3>\ud83d\udcdc Lesson History & Reviews</h3>");
        if (history.isEmpty()) {
            html.append("<p style='color:#888;'>No lessons yet.</p>");
        } else {
            for(String item : history) {
                html.append(item);
            }
        }

        html.append("</div></body></html>");
        return html.toString();
    }

    private String getCommonStyles() {
        return "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap' rel='stylesheet'><style>body{font-family:'Poppins',sans-serif;background:#f5f7fa;margin:0;color:#333;} .navbar{background:#fff;padding:15px 30px;display:flex;justify-content:space-between;align-items:center;box-shadow:0 2px 10px rgba(0,0,0,0.05);position:sticky;top:0;z-index:100;} .brand{font-size:1.5rem;font-weight:600;color:#667eea;} .user-panel{display:flex;align-items:center;gap:15px;} .btn{padding:8px 15px;border-radius:6px;border:none;cursor:pointer;color:white;font-weight:500;} .btn-primary{background:#667eea;} .btn-danger{background:#ff6b6b;} .btn-success{background:#26de81;} .badge-money{background:#e0f2f1;color:#00897b;padding:5px 10px;border-radius:20px;font-weight:600;} .container{max-width:1100px;margin:40px auto;padding:0 20px;} .grid-layout{display:grid;grid-template-columns:repeat(auto-fill,minmax(300px,1fr));gap:25px;} .card{background:white;border-radius:12px;overflow:hidden;box-shadow:0 4px 15px rgba(0,0,0,0.05);} .card-header{padding:20px;border-bottom:1px solid #f0f0f0;display:flex;justify-content:space-between;} .card-body{padding:20px;} .card-footer{background:#fafafa;padding:15px 20px;} .alert{padding:15px;border-radius:8px;margin-bottom:20px;text-align:center;} .alert.success{background:#d4edda;color:#155724;} .alert.error{background:#f8d7da;color:#721c24;} .booking-item{background:white;padding:15px;margin-bottom:15px;border-radius:8px;box-shadow:0 2px 5px rgba(0,0,0,0.05);display:flex;justify-content:space-between;align-items:center;} .input-sm{width:70px;padding:5px;border:1px solid #ddd;border-radius:4px;} input,select{padding:10px;border:1px solid #ddd;border-radius:6px;} .search-box{display:flex;gap:10px;margin-bottom:20px;background:white;padding:20px;border-radius:12px;}</style>";
    }
}
