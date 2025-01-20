import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class ServerController {

    @FXML
    private TextField ipField;

    @FXML
    private TextField portField;

    @FXML
    private Button startButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Button fetchButton;

    @FXML
    private TextArea resultArea;

    private Thread serverThread;

    @FXML
    private void startServer() {
        String ip = ipField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid port number");
            return;
        }

        if (serverThread != null && serverThread.isAlive()) {
            statusLabel.setText("Server is already running.");
            return;
        }

        serverThread = new Thread(() -> runServer(ip, port));
        serverThread.setDaemon(true);
        serverThread.start();

        statusLabel.setText("Server started on " + ip + ":" + port);
    }

    private void runServer(String ip, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on " + ip + ":" + port);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Waits for a client connection
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Handle each client in a separate thread
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
        }
    }

   
}
