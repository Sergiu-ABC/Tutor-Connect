import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Read from environment variables so real credentials never live in the repository
    private static final String URL = env("DB_URL", "jdbc:postgresql://localhost:5432/tutor_db");
    private static final String USER = env("DB_USER", "user");
    private static final String PASSWORD = env("DB_PASSWORD", "password");
    private static Connection connection = null;

    private DatabaseConnection() {
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database Connected Successfully!");
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }
}