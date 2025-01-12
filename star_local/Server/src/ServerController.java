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

    @FXML
    private void fetchSubscribers() {
        DBHandler dbHandler = new DBHandler();

        try {
            List<Subscriber> subscribers = dbHandler.getAllSubscribersList();

            StringBuilder sb = new StringBuilder();
            for (Subscriber sub : subscribers) {
                sb.append("ID: ").append(sub.getId()).append(", ")
                        .append("Name: ").append(sub.getName()).append(", ")
                        .append("Phone: ").append(sub.getPhone()).append(", ")
                        .append("Email: ").append(sub.getEmail()).append("\n");
            }

            resultArea.setText(sb.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            resultArea.setText("Error fetching subscribers: " + e.getMessage());
        }
    }
}
