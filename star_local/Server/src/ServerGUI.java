import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerGUI extends Application {

	private TextField ipField; //  IP
	private TextField portField; //  Port
	private Button startButton; 
	private Label statusLabel;

	private Thread serverThread;    

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
		serverThread.setDaemon(true); // תגרום לשרשור להסגר עם סגירת התוכנה
		serverThread.start();

		statusLabel.setText("Server started on " + ip + ":" + port);
	}


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

	public static void main(String[] args) {
		launch(args);
	}
}
