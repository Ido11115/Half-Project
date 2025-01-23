import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientHandler implements Runnable {
	private Socket clientSocket;
	private DBHandler dbHandler;

	public ClientHandler(Socket socket) {
		this.clientSocket = socket;
		this.dbHandler = new DBHandler(); // Instantiate DBHandler
	}

	@Override
	public void run() {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
				PrintWriter writer = new PrintWriter(
						new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)) {
			System.out.println("New client connected: " + clientSocket.getInetAddress());

			String command;
			while ((command = reader.readLine()) != null) {
				System.out.println("Received command: " + command);

				if (command.equalsIgnoreCase("TEST_CONNECTION")) {
					writer.println("Connection successful");
				} else if (command.startsWith("LOGIN")) {
					handleLogin(command, writer);
				} else if (command.startsWith("REGISTER_SUBSCRIBER")) {
					handleRegisterSubscriber(command, writer);
				} else if (command.startsWith("LOAN_BOOK")) {
					handleLoanBook(command, writer);
				} else if (command.startsWith("RETURN_BOOK")) {
					handleReturnBook(command, writer);
				} else if (command.startsWith("GET_SUBSCRIBERS")) {
					handleGetSubscribersRequest(writer);
				} else if (command.startsWith("GET_SUBSCRIPTION_HISTORY")) {
					handleGetSubscriptionHistory(command, writer);
				} else if (command.equals("GET_SUBSCRIBER_STATUS_COUNTS_BY_MONTH")) {
					handleGetSubscriberStatusCountsByMonth(writer);
				} else if (command.startsWith("GET_SUBSCRIBER_STATUS")) {
					handleGetSubscriberStatus(command, writer);
				} else if (command.startsWith("SAVE_SUBSCRIPTION_HISTORY")) {
					handleSaveSubscriptionHistory(command, writer);
				} else if (command.startsWith("UPDATE_SUBSCRIBER_STATUS")) {
					handleUpdateSubscriberStatus(command, writer);
				} else if (command.equals("GET_LOANS_TIME")) {
					handleGetLoansTime(writer);
				} else if (command.startsWith("SEARCH_BOOK")) {
					handleSearchBook(command, writer);
				} else if (command.startsWith("RESERVE_BOOK")) {
					handleReserveBook(command, writer);
				} else if (command.equals("GET_PROFILE")) {
					handleGetProfile(writer);
				} else if (command.startsWith("UPDATE_PROFILE")) {
					handleUpdateProfile(command, writer);
				} else if (command.startsWith("GET_SUBSCRIBER_DATA")) {
					handleGetSubcriberData(command, writer); // Handle fetching subscriber data
				} else if (command.startsWith("UPDATE_SUBSCRIBER_DATA")) {
					handleUpdateSubscriberData(command, writer); // Handle updating subscriber data
				} else if (command.startsWith("GET_DUE_BOOKS")) { // Add this condition
					handleGetDueBooks(command, writer);
				} else {
					writer.println("Unknown command");
				}
			}
		} catch (Exception e) {
			System.out.println("Client disconnected.");
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("Error closing client socket: " + e.getMessage());
			}
		}
	}

	private void handleLogin(String command, PrintWriter writer) {
		String[] parts = command.split(",");
		if (parts.length != 4) {
			writer.println("Invalid command format. Expected: LOGIN,role,username,password");
			return;
		}

		String role = parts[1].trim();
		String username = parts[2].trim();
		String password = parts[3].trim();

		try {
			if (role.equalsIgnoreCase("Subscriber")) {
				int subscriberId = dbHandler.validateSubscriberLogin(username, password);
				if (subscriberId != -1) {
					writer.println("Login successful," + subscriberId); // Include subscriber ID in response
				} else {
					writer.println("Invalid username or password");
				}
			} else if (role.equalsIgnoreCase("Librarian")) {
				boolean isValid = dbHandler.validateLibrarianLogin(username, password);
				writer.println(isValid ? "Login successful" : "Invalid username or password");
			} else {
				writer.println("Invalid role specified");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error processing login: " + e.getMessage());
		}
	}

	private void handleRegisterSubscriber(String command, PrintWriter writer) {
		String[] parts = command.split(",");
		if (parts.length != 9) {
			writer.println(
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
			writer.println("Registration successful");
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Database error: " + e.getMessage());
		}
	}

	private void handleLoanBook(String command, PrintWriter writer) {
		String[] parts = command.split(",");
		if (parts.length != 5) {
			writer.println("Invalid command format. Expected: LOAN_BOOK,subscriberId,bookId,loanDate,returnDate");
			return;
		}

		int subscriberId = Integer.parseInt(parts[1]);
		int bookId = Integer.parseInt(parts[2]);
		String loanDate = parts[3];
		String returnDate = parts[4];

		System.out.println("Processing loan request: Subscriber ID=" + subscriberId + ", Book ID=" + bookId);

		try {
			if (!dbHandler.isSubscriberActive(subscriberId)) {
				writer.println("Subscriber is not active.");
				return;
			}

			if (!dbHandler.isBookAvailable(bookId)) {
				writer.println("No available copies for the requested book.");
				return;
			}

			dbHandler.loanBookToSubscriber(subscriberId, bookId, loanDate, returnDate);
			writer.println("Loan successful");
		} catch (Exception e) {
			e.printStackTrace();
			writer.println("Error processing loan: " + e.getMessage());
		}
	}

	private void handleReturnBook(String command, PrintWriter writer) {
		String[] parts = command.split(",");
		if (parts.length != 3) {
			writer.println("Invalid command format. Expected: RETURN_BOOK,subscriberId,bookId");
			return;
		}

		int subscriberId = Integer.parseInt(parts[1]);
		int bookId = Integer.parseInt(parts[2]);

		System.out.println("Processing return request: Subscriber ID=" + subscriberId + ", Book ID=" + bookId);

		try {
			if (!dbHandler.isLoanExists(subscriberId, bookId)) {
				writer.println("No loan record found for Subscriber ID " + subscriberId + " and Book ID " + bookId);
				return;
			}

			dbHandler.returnBookFromSubscriber(subscriberId, bookId);
			writer.println("Return successful");
		} catch (Exception e) {
			e.printStackTrace();
			writer.println("Error processing return: " + e.getMessage());
		}
	}

	private void handleGetSubscribersRequest(PrintWriter writer) {
		try {
			String subscriberData = dbHandler.getAllSubscribersAsString();
			writer.println(subscriberData);
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error fetching subscribers: " + e.getMessage());
		}
	}

	private void handleGetSubscriptionHistory(String command, PrintWriter writer) {
		String[] parts = command.split(",");
		if (parts.length != 2) {
			writer.println("Invalid command format. Expected: GET_SUBSCRIPTION_HISTORY,subscriberId");
			return;
		}

		int subscriberId = Integer.parseInt(parts[1]);

		try {
			String history = dbHandler.getSubscriptionHistory(subscriberId); // Returns history as a delimited string
			writer.println(history.isEmpty() ? "No history available" : history);
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error fetching subscription history: " + e.getMessage());
		}
	}

	private void handleGetSubscriberStatus(String command, PrintWriter writer) {
		String[] parts = command.split(",");
		if (parts.length != 2) {
			writer.println("Invalid command format. Expected: GET_SUBSCRIBER_STATUS,subscriberId");
			return;
		}

		int subscriberId = Integer.parseInt(parts[1]);

		try {
			String status = dbHandler.getSubscriberStatus(subscriberId); // Returns "Active" or "Inactive"
			writer.println(status);
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error fetching subscriber status: " + e.getMessage());
		}
	}

	private void handleSaveSubscriptionHistory(String command, PrintWriter writer) {
		String[] parts = command.split(",");
		if (parts.length < 3) {
			writer.println(
					"Invalid command format. Expected: SAVE_SUBSCRIPTION_HISTORY,subscriberId,action1,action2,...");
			return;
		}

		int subscriberId = Integer.parseInt(parts[1]);
		List<String> actions = new ArrayList<>(Arrays.asList(parts).subList(2, parts.length));

		System.out.println("Saving subscription history for Subscriber ID " + subscriberId + ": " + actions);

		try {
			dbHandler.saveSubscriptionHistory(subscriberId, actions);
			writer.println("Subscription history updated successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error updating subscription history: " + e.getMessage());
		}
	}

	private void handleUpdateSubscriberStatus(String command, PrintWriter writer) {
		String[] parts = command.split(",");
		if (parts.length != 3) {
			writer.println("Invalid command format. Expected: UPDATE_SUBSCRIBER_STATUS,subscriberId,newStatus");
			return;
		}

		int subscriberId = Integer.parseInt(parts[1]);
		String newStatus = parts[2];

		System.out.println("Updating status for Subscriber ID " + subscriberId + " to " + newStatus);

		try {
			dbHandler.updateSubscriberStatus(subscriberId, newStatus);
			writer.println("Subscriber status updated successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error updating subscriber status: " + e.getMessage());
		}
	}

	private void handleGetSubscriberStatusCountsByMonth(PrintWriter writer) {
		try {
			String statusCounts = dbHandler.getSubscriberStatusCountsByMonth();
			System.out.println("Subscriber Status Counts by Month: " + statusCounts); // Debug
			writer.println(statusCounts);
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error fetching subscriber status counts by month: " + e.getMessage());
		}
	}

	private void handleGetLoansTime(PrintWriter writer) {
		try {
			String loanData = dbHandler.getLoansTime();
			System.out.println("Loan Data: " + loanData); // Debug log
			writer.println(loanData);
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error fetching loan data: " + e.getMessage());
		}
	}

	private void handleSearchBook(String command, PrintWriter writer) {
		String[] parts = command.split(",", 2);
		if (parts.length != 2) {
			writer.println("Invalid command format. Expected: SEARCH_BOOK,query");
			return;
		}

		String query = parts[1].trim();
		try {
			String books = dbHandler.searchBooks(query);
			writer.println(books.isEmpty() ? "No books found" : books);
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error fetching book data: " + e.getMessage());
		}
	}

	private void handleReserveBook(String command, PrintWriter writer) {
		String[] parts = command.split(",", 2);
		if (parts.length != 2) {
			writer.println("Invalid command format. Expected: RESERVE_BOOK,bookId");
			return;
		}

		int bookId = Integer.parseInt(parts[1]);
		try {
			dbHandler.reserveBook(bookId);
			writer.println("Reservation successful");
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error reserving book: " + e.getMessage());
		}
	}

	private void handleGetProfile(PrintWriter writer) {
		try {
			int subscriberId = dbHandler.getCurrentSubscriberId();
			if (subscriberId == -1) {
				writer.println("No subscriber is logged in.");
				return;
			}

			String profileData = dbHandler.getSubscriberProfile(subscriberId);
			writer.println(profileData != null ? profileData : "Error fetching profile data.");
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error fetching profile data: " + e.getMessage());
		}
	}

	private void handleUpdateProfile(String command, PrintWriter writer) {
		String[] parts = command.split(",", 8);
		if (parts.length != 8) {
			writer.println(
					"Invalid command format. Expected: UPDATE_PROFILE,id,name,lastName,email,phone,username,password");
			return;
		}

		int subscriberId = Integer.parseInt(parts[1]);
		String name = parts[2];
		String lastName = parts[3];
		String email = parts[4];
		String phone = parts[5];
		String username = parts[6];
		String password = parts[7];

		try {
			dbHandler.updateSubscriberProfile(subscriberId, name, lastName, email, phone, username, password);
			writer.println("Profile updated successfully");
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error updating profile: " + e.getMessage());
		}
	}

	private void handleGetSubcriberData(String command, PrintWriter writer) {
		String[] parts = command.split(",");
		if (parts.length != 2) {
			writer.println("Invalid command format. Expected: GET_SUBSCRIBER_DATA,subscriberId");
			return;
		}

		int subscriberId = Integer.parseInt(parts[1]);

		try {
			String subscriberData = dbHandler.getSubscriberData(subscriberId);
			writer.println(subscriberData);
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error fetching subscriber data: " + e.getMessage());
		}
	}

	private void handleUpdateSubscriberData(String command, PrintWriter writer) {
		String[] parts = command.split(",", 8);
		if (parts.length != 8) {
			writer.println(
					"Invalid command format. Expected: UPDATE_SUBSCRIBER_DATA,id,name,lastName,email,phone,username,password");
			return;
		}

		int subscriberId = Integer.parseInt(parts[1]);
		String name = parts[2];
		String lastName = parts[3];
		String email = parts[4];
		String phone = parts[5];
		String username = parts[6];
		String password = parts[7];

		try {
			dbHandler.updateSubscriberData(subscriberId, name, lastName, email, phone, username, password);
			writer.println("Update successful");
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error updating subscriber data: " + e.getMessage());
		}
	}

	private void handleGetDueBooks(String command, PrintWriter writer) {
		String[] parts = command.split(",");
		if (parts.length != 2) {
			writer.println("Invalid command format. Expected: GET_DUE_BOOKS,subscriberId");
			return;
		}

		int subscriberId = Integer.parseInt(parts[1]);

		try {
			String dueBooks = dbHandler.getDueBooks(subscriberId);
			writer.println(dueBooks.isEmpty() ? "No books due soon." : dueBooks);
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error fetching due books: " + e.getMessage());
		}
	}

}
