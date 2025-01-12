import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {
	private static final int PORT = 5555; // Port to listen on

	public void start() {
		System.out.println("Server is starting...");
		try (ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName("0.0.0.0"));) {
			System.out.println("Server is listening on port " + PORT);

			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

				// Handle client in a separate thread
				new Thread(new ClientHandler(socket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
