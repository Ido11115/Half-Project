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

/**
 * The {@code ClientHandler} class handles communication with a client connected
 * via a socket.
 * <p>
 * This class is designed to process commands sent by the client, perform
 * database interactions, and send appropriate responses back to the client.
 * </p>
 */
public class ClientHandler implements Runnable {

	/**
	 * The socket used for communication with the client.
	 */
	private Socket clientSocket;

	/**
	 * The database handler for interacting with the database.
	 */
	private DBHandler dbHandler;

	/**
	 * Constructs a {@code ClientHandler} instance.
	 *
	 * @param socket the socket representing the client's connection
	 */
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
				} else if (command.startsWith("GET_LOANS")) {
					handleGetLoans(writer);
				} else if (command.startsWith("PROLONG_LOAN")) {
					handleProlongLoan(command, writer);
				} else if (command.startsWith("DELETE_SUBSCRIBER")) {
					handleDeleteSubscriber(command, writer);
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

	/**
	 * Handles the "LOGIN" command sent by the client.
	 *
	 * @param command the command for login
	 * @param writer  the writer used to send responses to the client
	 */
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
					dbHandler.setCurrentSubscriberId(subscriberId); // Set the current subscriber ID here
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

	/**
	 * Handles the "REGISTER_SUBSCRIBER" command sent by the client.
	 *
	 * @param command the command for registering a subscriber
	 * @param writer  the writer used to send responses to the client
	 */
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

	/**
	 * Handles the "LOAN_BOOK" command sent by the client.
	 *
	 * @param command the command for loaning a book
	 * @param writer  the writer used to send responses to the client
	 */
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

	/**
	 * Handles the "RETURN_BOOK" command sent by the client.
	 *
	 * @param command the command for returning a book
	 * @param writer  the writer used to send responses to the client
	 */
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

	/**
	 * Handles the "GET_SUBSCRIBERS" command sent by the client.
	 *
	 * @param writer the writer used to send responses to the client
	 */
	private void handleGetSubscribersRequest(PrintWriter writer) {
		try {
			String subscriberData = dbHandler.getAllSubscribersAsString();
			writer.println(subscriberData);
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error fetching subscribers: " + e.getMessage());
		}
	}

	/**
	 * Handles the "GET_SUBSCRIPTION_HISTORY" command by retrieving the subscription
	 * history of a specific subscriber and sending it to the client.
	 *
	 * @param command the command in the format
	 *                "GET_SUBSCRIPTION_HISTORY,subscriberId"
	 * @param writer  the writer used to send the response to the client
	 */
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

	/**
	 * Handles the "GET_SUBSCRIBER_STATUS" command by retrieving the status of a
	 * specific subscriber and sending it to the client.
	 *
	 * @param command the command in the format "GET_SUBSCRIBER_STATUS,subscriberId"
	 * @param writer  the writer used to send the response to the client
	 */
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

	/**
	 * Handles the "SAVE_SUBSCRIPTION_HISTORY" command by saving the subscription
	 * history actions for a specific subscriber and sending a confirmation to the
	 * client.
	 *
	 * @param command the command in the format
	 *                "SAVE_SUBSCRIPTION_HISTORY,subscriberId,action1,action2,..."
	 * @param writer  the writer used to send the response to the client
	 */
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

	/**
	 * Handles the "UPDATE_SUBSCRIBER_STATUS" command by updating the status of a
	 * subscriber and sending a confirmation to the client.
	 *
	 * @param command the command in the format
	 *                "UPDATE_SUBSCRIBER_STATUS,subscriberId,newStatus"
	 * @param writer  the writer used to send the response to the client
	 */
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

	/**
	 * Handles the "GET_SUBSCRIBER_STATUS_COUNTS_BY_MONTH" command by retrieving
	 * monthly status counts and sending the result to the client.
	 *
	 * @param writer the writer used to send the response to the client
	 */
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

	/**
	 * Handles the "GET_LOANS_TIME" command to retrieve the total loan time and
	 * sends it to the client.
	 *
	 * @param writer the writer used to send the response to the client
	 */
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

	/**
	 * Handles the "SEARCH_BOOK" command to search for books based on a query and
	 * sends the result to the client.
	 *
	 * @param command the command in the format "SEARCH_BOOK,query"
	 * @param writer  the writer used to send the response to the client
	 */
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

	/**
	 * Handles the "RESERVE_BOOK" command to reserve a book for a subscriber and
	 * sends the confirmation to the client.
	 *
	 * @param command the command in the format "RESERVE_BOOK,bookId"
	 * @param writer  the writer used to send the response to the client
	 */
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

	/**
	 * Handles the "GET_PROFILE" command to retrieve the current subscriber's
	 * profile data and sends it to the client.
	 *
	 * @param writer the writer used to send the response to the client
	 */
	private void handleGetProfile(PrintWriter writer) {
		try {
			int subscriberId = dbHandler.getCurrentSubscriberId();
			if (subscriberId == -1) {
				writer.println("No subscriber is logged in.");
				return;
			}
			System.out.println(subscriberId);
			String profileData = dbHandler.getSubscriberProfile(subscriberId);
			writer.println(profileData != null ? profileData : "Error fetching profile data.");
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error fetching profile data: " + e.getMessage());
		}
	}

	/**
	 * Handles the "UPDATE_PROFILE" command to update the subscriber's profile
	 * information and sends a confirmation to the client.
	 *
	 * @param command the command in the format
	 *                "UPDATE_PROFILE,id,name,lastName,email,phone,username,password"
	 * @param writer  the writer used to send the response to the client
	 */
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

	/**
	 * Handles the "GET_SUBSCRIBER_DATA" command to retrieve data for a specific
	 * subscriber and sends it to the client.
	 *
	 * @param command the command in the format "GET_SUBSCRIBER_DATA,subscriberId"
	 * @param writer  the writer used to send the response to the client
	 */
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

	/**
	 * Handles the "UPDATE_SUBSCRIBER_DATA" command to update a subscriber's data
	 * and sends a confirmation to the client.
	 *
	 * @param command the command in the format
	 *                "UPDATE_SUBSCRIBER_DATA,id,name,lastName,email,phone,username,password"
	 * @param writer  the writer used to send the response to the client
	 */
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

	/**
	 * Handles the "GET_DUE_BOOKS" command to retrieve books that are due for a
	 * specific subscriber and sends it to the client.
	 *
	 * @param command the command in the format "GET_DUE_BOOKS,subscriberId"
	 * @param writer  the writer used to send the response to the client
	 */
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

	/**
	 * Handles the "GET_LOANS" command to retrieve all active loans and sends them
	 * to the client.
	 *
	 * @param writer the writer used to send the response to the client
	 */
	private void handleGetLoans(PrintWriter writer) {
		try {
			List<String> loans = dbHandler.getAllLoansAsString();
			if (loans.isEmpty()) {
				writer.println("No loans found.");
				System.out.println("No loans found in the database."); // Debug log
			} else {
				String response = String.join(";", loans);
				writer.println(response);
				System.out.println("Server Response: " + response); // Debug log
			}
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error fetching loans: " + e.getMessage());
		}
	}

	private void handleProlongLoan(String command, PrintWriter writer) {
		String[] parts = command.split(",");
		if (parts.length != 3) {
			writer.println("Invalid command format. Expected: PROLONG_LOAN,loanId,newReturnDate");
			return;
		}

		int loanId = Integer.parseInt(parts[1]);
		String newReturnDate = parts[2];

		try {
			// Get the book ID associated with the loan
			int bookId = dbHandler.getBookIdByLoanId(loanId);

			// Check if the book is reserved
			if (dbHandler.isBookReserved(bookId)) {
				writer.println("This book is reserved by another subscriber. Prolongation is not allowed.");
				return;
			}

			// Prolong the loan
			if (dbHandler.prolongLoan(loanId, newReturnDate)) {
				writer.println("Prolongation successful");
			} else {
				writer.println("Error prolonging the loan.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error processing prolongation: " + e.getMessage());
		}
	}

	private void handleDeleteSubscriber(String command, PrintWriter writer) {
		String[] parts = command.split(",");
		if (parts.length != 2) {
			writer.println("Invalid command format. Expected: DELETE_SUBSCRIBER,subscriberId");
			return;
		}

		try {
			int subscriberId = Integer.parseInt(parts[1]);
			dbHandler.deleteSubscriber(subscriberId);
			writer.println("Delete successful");
		} catch (SQLException e) {
			e.printStackTrace();
			writer.println("Error deleting subscriber: " + e.getMessage());
		}
	}

}
