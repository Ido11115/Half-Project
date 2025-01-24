import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The ServerGUI class is a JavaFX application that provides a graphical user interface
 * for starting a server. It allows the user to specify the IP address and port, start the server,
 * and monitor its status.
 */
public class ServerGUI extends Application {

    private TextField ipField; // Input field for the server IP address
    private TextField portField; // Input field for the server port
    private Button startButton; // Button to start the server
    private Label statusLabel; // Label to display the server status

    private Thread serverThread; // Thread to run the server

    /**
     * The entry point for the JavaFX application.
     * Initializes the GUI components and sets up the primary stage.
     *
     * @param primaryStage the primary stage for the application
     */
    @Override
    public void start(Stage primaryStage) {
        ipField = new TextField("localhost");
        portField = new TextField("5555");
        startButton = new Button("Start Server");
        statusLabel = new Label("Server not started yet.");

        startButton.setOnAction(e -> startServer());

        VBox root = new VBox(10, new Label("IP Address:"), ipField, new Label("Port:"), portField, startButton,
                statusLabel);

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Server GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Starts the server using the IP address and port specified in the input fields.
     * Updates the status label to indicate success or errors.
     */
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
        serverThread.setDaemon(true); // Ensures the thread closes with the application
        serverThread.start();

        statusLabel.setText("Server started on " + ip + ":" + port);
    }

    /**
     * Runs the server on the specified IP address and port. Waits for client connections
     * and handles each client in a separate thread.
     *
     * @param ip   the IP address for the server
     * @param port the port for the server
     */
    private void runServer(String ip, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip))) {
            System.out.println("Server is running on " + ip + ":" + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
        }
    }

    /**
     * The main method to launch the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
