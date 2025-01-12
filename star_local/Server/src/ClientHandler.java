import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class ClientHandler implements Runnable {
	private Socket clientSocket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private DBHandler dbHandler;

	public ClientHandler(Socket socket) {
		this.clientSocket = socket;
		this.dbHandler = new DBHandler();
	}

	@Override
	public void run() {
		try {
			output = new ObjectOutputStream(clientSocket.getOutputStream());
			input = new ObjectInputStream(clientSocket.getInputStream());

			System.out.println("Client connected: " + clientSocket.getInetAddress());

			Object obj;
			while ((obj = input.readObject()) != null) {
				if (!(obj instanceof String)) {
					output.writeObject("Invalid command format. Expected a string.");
					output.flush();
					continue;
				}

				String command = (String) obj;
				System.out.println("Received command: " + command);

				try {
					if (command.startsWith("LOGIN")) {
						handleLogin(command);
					} else if (command.startsWith("UPDATE_SUBSCRIBER")) {
						handleUpdateSubscriber(command);
					} else {
						switch (command) {
						case "GET_SUBSCRIBERS":
							handleGetSubscribers();
							break;
						case "QUIT":
							System.out.println("Client disconnected.");
							output.writeObject("Goodbye!");
							output.flush();
							return; // Exit the loop
						default:
							output.writeObject("Unknown command");
							output.flush();
						}
					}
				} catch (SQLException e) {
					output.writeObject("Database error: " + e.getMessage());
					output.flush();
				} catch (Exception e) {
					output.writeObject("Error processing command: " + e.getMessage());
					output.flush();
				}
			}
		} catch (EOFException e) {
			System.out.println("Client disconnected abruptly.");
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Error during communication: " + e.getMessage());
		} finally {
			try {
				if (clientSocket != null && !clientSocket.isClosed()) {
					clientSocket.close();
					System.out.println("Client connection closed.");
				}
			} catch (IOException e) {
				System.err.println("Error closing client socket: " + e.getMessage());
			}
		}
	}

	private void handleUpdateSubscriber(String command) throws SQLException, IOException {
		try {
			String[] parts = command.split(",");
			if (parts.length != 4) {
				output.writeObject("Invalid command format. Expected: UPDATE_SUBSCRIBER,id,phone,email");
				return;
			}

			int id = Integer.parseInt(parts[1]);
			String phone = parts[2];
			String email = parts[3];

			dbHandler.updateSubscriber(id, phone, email);
			output.writeObject("Subscriber updated successfully.");
		} catch (NumberFormatException e) {
			output.writeObject("Invalid subscriber ID.");
		} catch (SQLException e) {
			output.writeObject("Error updating subscriber: " + e.getMessage());
		} finally {
			output.flush();
		}
	}

	private void handleGetSubscribers() throws SQLException, IOException {
		List<Subscriber> subscribers = dbHandler.getAllSubscribersList();
		if (subscribers.isEmpty()) {
			output.writeObject("No subscribers available.");
		} else {
			StringBuilder response = new StringBuilder();
			for (Subscriber sub : subscribers) {
				response.append("ID: ").append(sub.getId()).append(", Name: ").append(sub.getName()).append(", Phone: ")
						.append(sub.getPhone()).append(", Email: ").append(sub.getEmail()).append(", History: ")
						.append(sub.getHistory()).append("\n");
			}
			output.writeObject(response.toString());
		}
		output.flush();
	}

	private void handleLogin(String command) throws SQLException, IOException {
		String[] parts = command.split(",");
		if (parts.length != 3) {
			output.writeObject("Invalid command format. Expected: LOGIN,id,name");
			return;
		}

		try {
			int id = Integer.parseInt(parts[1].trim());
			String name = parts[2].trim();

			boolean isValid = dbHandler.verifyLogin(id, name);
			if (isValid) {
				output.writeObject("Login successful");
			} else {
				output.writeObject("Invalid ID or name");
			}
		} catch (NumberFormatException e) {
			output.writeObject("Invalid ID format");
		} finally {
			output.flush();
		}
	}

}
