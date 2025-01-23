
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
	private static final int PORT = 5555;
	private ScheduledExecutorService scheduler;

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

	public static void main(String[] args) {
		new Server().start();
	}
}
