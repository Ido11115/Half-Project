
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 5555;

    public void start() {
        System.out.println("Server is starting...");
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
