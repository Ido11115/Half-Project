
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientHandler implements Runnable {
	private Socket clientSocket;
	private DBHandler dbHandler;

	public ClientHandler(Socket socket) {
		this.clientSocket = socket;
		this.dbHandler = new DBHandler(); // Instantiate DBHandler
	}

	public void run() {
		try (ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream())) {

			System.out.println("New client connected: " + clientSocket.getInetAddress());

			Object obj;
			while ((obj = input.readObject()) != null) {
				if (!(obj instanceof String)) {
					output.writeObject("Invalid command format.");
					output.flush();
					continue;
				}

				String command = (String) obj;
				System.out.println("Received command: " + command);

				if (command.equals("TEST_CONNECTION")) {
					output.writeObject("Connection successful");
				} else if (command.startsWith("LOGIN")) {
					handleLogin(command, output);
				} else if (command.startsWith("REGISTER_SUBSCRIBER")) {
					handleRegisterSubscriber(command, output);
				} else if (command.startsWith("LOAN_BOOK")) {
					handleLoanBook(command, output);
				} else if (command.startsWith("RETURN_BOOK")) {
					handleReturnBook(command, output);
				} else if (command.startsWith("GET_SUBSCRIBER_INFO")) {
					handleGetSubscribers(output);
				} else if (command.startsWith("LOAN_BOOK")) {
					handleLoanBook(command, output);
				} else if (command.startsWith("RETURN_BOOK")) {
					handleReturnBook(command, output);
				} else if (command.equals("GET_SUBSCRIBERS")) {
					handleGetSubscribers(output);
				}else {
					output.writeObject("Unknown command");
				}
				output.flush();
			}
		} catch (Exception e) {
			System.out.println("Client disconnected.");
		}
	}

	private void handleLogin(String command, ObjectOutputStream output) throws IOException {
		String[] parts = command.split(",");
		if (parts.length != 4) {
			output.writeObject("Invalid command format.");
			return;
		}

		String role = parts[1].trim();
		String username = parts[2].trim();
		String password = parts[3].trim();

		System.out.println("Processing login request: Role=" + role + ", Username=" + username);

		boolean isValid = false;

		try {
			if (role.equalsIgnoreCase("Subscriber")) {
				isValid = dbHandler.validateSubscriberLogin(username, password);
			} else if (role.equalsIgnoreCase("Librarian")) {
				isValid = dbHandler.validateLibrarianLogin(username, password);
			} else {
				output.writeObject("Invalid role specified.");
				return;
			}

			if (isValid) {
				System.out.println("Login successful for: " + username);
				output.writeObject("Login successful");
			} else {
				System.out.println("Login failed for: " + username);
				output.writeObject("Invalid username or password");
			}
		} catch (Exception e) {
			e.printStackTrace();
			output.writeObject("Error processing login: " + e.getMessage());
		}
	}

	private void handleRegisterSubscriber(String command, ObjectOutputStream output) throws IOException {
		String[] parts = command.split(",");
		if (parts.length != 9) {
			output.writeObject(
					"Invalid command format. Expected: REGISTER_SUBSCRIBER,id,name,lastName,userName,phone,email,password,status");
			return;
		}

		int subscriberId = Integer.parseInt(parts[1]);
		String name = parts[2].trim();
		String lastName = parts[3].trim();
		String userName = parts[4].trim();
		String phone = parts[5].trim();
		String email = parts[6].trim();
		String password = parts[7].trim();
		String status = parts[8].trim();

		System.out.println("Processing registration for subscriber: " + name + " " + lastName);

		try {
			dbHandler.addSubscriber(subscriberId, name, lastName, userName, phone, email, password, status);
			output.writeObject("Registration successful");
		} catch (SQLException e) {
			e.printStackTrace();
			output.writeObject("Database error: " + e.getMessage());
		}
	}

	private void handleLoanBook(String command, ObjectOutputStream output) throws IOException {
		String[] parts = command.split(",");
		if (parts.length != 5) {
			output.writeObject("Invalid command format. Expected: LOAN_BOOK,subscriberId,bookId,loanDate,returnDate");
			return;
		}

		int subscriberId = Integer.parseInt(parts[1]);
		int bookId = Integer.parseInt(parts[2]);
		String loanDate = parts[3];
		String returnDate = parts[4];

		System.out.println("Processing loan request: Subscriber ID=" + subscriberId + ", Book ID=" + bookId);

		try {
			if (!dbHandler.isSubscriberActive(subscriberId)) {
				output.writeObject("Subscriber is not active.");
				return;
			}

			dbHandler.loanBookToSubscriber(subscriberId, bookId, loanDate, returnDate);
			output.writeObject("Loan successful");
		} catch (Exception e) {
			e.printStackTrace();
			output.writeObject("Error processing loan: " + e.getMessage());
		}
	}

	private void handleReturnBook(String command, ObjectOutputStream output) throws IOException {
		String[] parts = command.split(",");
		if (parts.length != 3) {
			output.writeObject("Invalid command format. Expected: RETURN_BOOK,subscriberId,bookId");
			return;
		}

		int subscriberId = Integer.parseInt(parts[1]);
		int bookId = Integer.parseInt(parts[2]);

		System.out.println("Processing return request: Subscriber ID=" + subscriberId + ", Book ID=" + bookId);

		try {
			// Validate that the loan exists
			if (!dbHandler.isLoanExists(subscriberId, bookId)) {
				output.writeObject("No loan record found for Subscriber ID " + subscriberId + " and Book ID " + bookId);
				return;
			}

			// Process the return
			dbHandler.returnBookFromSubscriber(subscriberId, bookId);
			output.writeObject("Return successful");
		} catch (Exception e) {
			e.printStackTrace();
			output.writeObject("Error processing return: " + e.getMessage());
		}
	}

	private void handleGetSubscribers(ObjectOutputStream output) throws IOException {
	    try {
	        List<Subscriber> subscribers = dbHandler.getAllSubscribers();
	        ObjectMapper objectMapper = new ObjectMapper();
	        String jsonResponse = objectMapper.writeValueAsString(subscribers);

	        // Debug: Print the JSON being sent
	        System.out.println("JSON Sent to Client: " + jsonResponse);

	        output.writeObject(jsonResponse);
	    } catch (Exception e) {
	        e.printStackTrace();
	        output.writeObject("Error fetching subscriber data: " + e.getMessage());
	    }
	}




}
