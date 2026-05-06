
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

public class WebServer {
    public static void main(String[] args) throws IOException {
        TutorRepository.initDatabase();
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new TutorController());
        server.setExecutor((Executor)null);
        System.out.println("✅ Web Server started at: http://localhost:" + port);
        server.start();
    }
}
