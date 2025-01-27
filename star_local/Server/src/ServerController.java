import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

/**
 * The ServerController class manages the server's UI and server functionality.
 * It allows the user to start a server, manage client connections, and fetch server-related data.
 */
public class ServerController {

	/**
     * Default constructor for the ServerController class.
     * <p>
     * Initializes the controller without any specific parameters.
     * All required fields and components are set by the FXML loader.
     * </p>
     */
    public ServerController() {
        // No specific initialization required
    }
	
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

    /**
     * Starts the server using the IP and port specified in the input fields.
     * Updates the status label to indicate success or errors.
     */
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

    /**
     * Runs the server on the specified IP and port. Listens for client connections
     * and handles each client in a separate thread.
     *
     * @param ip   the IP address on which the server should run
     * @param port the port on which the server should run
     */
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
