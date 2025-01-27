import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The Server class represents a simple server application that listens for client connections
 * and handles background tasks. It uses a ScheduledExecutorService to schedule periodic tasks
 * and a ServerSocket to accept client connections.
 */
public class Server {

    private static final int PORT = 5555;
    private ScheduledExecutorService scheduler;

    
    /**
     * Default constructor for the Server class.
     * <p>
     * Initializes the server without any specific parameters. The server will be configured
     * to listen on the default port and handle background tasks.
     * </p>
     */
    public Server() {
        // No specific initialization required
    } 
   
    /**
     * Starts the server, initializes the background task scheduler, and listens for client connections.
     * The background task is scheduled to run every day.
     */
    public void start() {
        System.out.println("Server is starting...");
        scheduler = Executors.newScheduledThreadPool(1);

        // Schedule the background task to run every day
        scheduler.scheduleAtFixedRate(new BackgroundTask(), 0, 1, TimeUnit.DAYS);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on localhost:" + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method to launch the server application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new Server().start();
    }
}
