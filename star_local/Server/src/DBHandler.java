import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations including connecting to the database, managing
 * subscribers, loans, and subscription history, as well as performing various
 * queries and updates on the database.
 */
public class DBHandler {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/world?useSSL=false&serverTimezone=UTC";
	private static final String USER = "root";
	private static final String PASSWORD = "Aa123456";
	private int currentSubscriberId = -1;

	public static void main(String[] args) {
		DBHandler dbHandler = new DBHandler();
		try (Connection connection = dbHandler.connect()) {
			if (connection != null) {
				System.out.println("Database connection successful!");
			} else {
				System.out.println("Failed to connect to the database.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Establishes a connection to the database.
	 *
	 * @return a Connection object if successful
	 * @throws SQLException if a database access error occurs
	 */
	public Connection connect() throws SQLException {
		try {
			Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
			System.out.println("Connected to database: " + DB_URL);
			return connection;
		} catch (SQLException e) {
			System.err.println("Database connection error: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Retrieves all subscribers as a formatted string.
	 *
	 * @return a string containing all subscriber details
	 * @throws SQLException if a database access error occurs
	 */
	public String getAllSubscribersAsString() throws SQLException {
		String query = """
					            SELECT subscriber_id, subscriber_name, last_name, subscriber_email, status
				FROM subscribe;
					            """;

		StringBuilder result = new StringBuilder();

		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				result.append(resultSet.getInt("subscriber_id")).append(",")
						.append(resultSet.getString("subscriber_name")).append(",")
						.append(resultSet.getString("last_name") != null ? resultSet.getString("last_name") : "")
						.append(",").append(resultSet.getString("subscriber_email")).append(",")
						.append(resultSet.getString("status")).append(";"); // No loan-related data
			}
		}
		return result.toString();
	}

	/**
	 * Adds a record to the subscription history for a specific subscriber.
	 *
	 * @param subscriberId the ID of the subscriber
	 * @param action       the action performed
	 * @throws SQLException if a database access error occurs
	 */
	public void addSubscriptionHistory(int subscriberId, String action) throws SQLException {
		String query = "INSERT INTO detailed_subscription_history (subscriber_id, action, action_date) VALUES (?, ?, NOW())";

		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, subscriberId);
			preparedStatement.setString(2, action);
			preparedStatement.executeUpdate();
			System.out.println("Subscription history updated for subscriber: " + subscriberId);
		}
	}

	/**
	 * Retrieves the subscription history for a specific subscriber.
	 *
	 * @param subscriberId the ID of the subscriber
	 * @return a string containing the subscription history
	 * @throws SQLException if a database access error occurs
	 */
	public String getSubscriptionHistory(int subscriberId) throws SQLException {
		String query = """
				    SELECT action, action_date FROM detailed_subscription_history
				    WHERE subscriber_id = ? ORDER BY action_date DESC
				""";
		StringBuilder history = new StringBuilder();

		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, subscriberId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					history.append(resultSet.getString("action")).append(",")
							.append(resultSet.getTimestamp("action_date")).append(";");
				}
			}
		}
		return history.toString().trim();
	}

	/**
	 * Gets the status of a specific subscriber.
	 *
	 * @param subscriberId the ID of the subscriber
	 * @return the subscriber's status
	 * @throws SQLException if a database access error occurs
	 */
	public String getSubscriberStatus(int subscriberId) throws SQLException {
		String query = "SELECT status FROM subscribe WHERE subscriber_id = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, subscriberId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getString("status"); // "Active" or "Inactive"
				}
			}
		}
		return "Unknown";
	}

	/**
	 * Saves the subscription history for a subscriber by removing existing records
	 * and inserting new actions with their respective timestamps.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @param actions      A list of actions performed by the subscriber.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public void saveSubscriptionHistory(int subscriberId, List<String> actions) throws SQLException {
		String deleteQuery = "DELETE FROM detailed_subscription_history WHERE subscriber_id = ?";
		String insertQuery = "INSERT INTO detailed_subscription_history (subscriber_id, action, action_date) VALUES (?, ?, NOW())";

		try (Connection connection = connect();
				PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
				PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

			// Delete existing history
			deleteStmt.setInt(1, subscriberId);
			deleteStmt.executeUpdate();

			// Insert new history
			for (String action : actions) {
				insertStmt.setInt(1, subscriberId);
				insertStmt.setString(2, action);
				insertStmt.executeUpdate();
			}
		}
	}

	/**
	 * Updates the status of a subscriber in the subscription database.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @param newStatus    The new status to be assigned to the subscriber.
	 * @throws SQLException If an SQL error occurs during the update.
	 */
	public void updateSubscriberStatus(int subscriberId, String newStatus) throws SQLException {
		String query = "UPDATE subscribe SET status = ? WHERE subscriber_id = ?";

		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, newStatus);
			preparedStatement.setInt(2, subscriberId);
			preparedStatement.executeUpdate();
		}
	}

	/**
	 * Updates the status of a subscriber in the subscription database.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @param newStatus    The new status to be assigned to the subscriber.
	 * @throws SQLException If an SQL error occurs during the update.
	 */
	public void updateSubscriber(int id, String phone, String email) throws SQLException {
		String query = "UPDATE subscribe SET subscriber_phone_number = ?, subscriber_email = ? WHERE subscriber_id = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, phone);
			preparedStatement.setString(2, email);
			preparedStatement.setInt(3, id);
			preparedStatement.executeUpdate();
		}
	}

	/**
	 * Verifies the login credentials of a subscriber and retrieves their unique ID.
	 *
	 * @param name     The username of the subscriber.
	 * @param password The password of the subscriber.
	 * @return The unique ID of the subscriber if login is successful, or -1 if it
	 *         fails.
	 * @throws SQLException If an SQL error occurs during verification.
	 */
	public int verifySubscriberLogin(String name, String password) throws SQLException {
		String query = "SELECT subscriber_id FROM subscribe WHERE user_name = ? AND password = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, password);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt("subscriber_id"); // Return the subscriber ID
				}
			}
		}
		return -1; // Return -1 if login fails
	}

	/**
	 * Verifies the login credentials of a librarian.
	 *
	 * @param username The username of the librarian.
	 * @param password The password of the librarian.
	 * @return True if the credentials are valid, false otherwise.
	 * @throws SQLException If an SQL error occurs during verification.
	 */
	public boolean verifyLibrarianLogin(String username, String password) throws SQLException {
		String query = "SELECT * FROM Librarian WHERE Username = ? AND Password = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password); // Assume password is already hashed
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next(); // Returns true if a record is found
			}
		}
	}

	/**
	 * Validates the login credentials of a subscriber and retrieves their unique
	 * ID.
	 *
	 * @param username The username of the subscriber.
	 * @param password The password of the subscriber.
	 * @return The unique ID of the subscriber if login is successful, or -1 if it
	 *         fails.
	 * @throws SQLException If an SQL error occurs during validation.
	 */
	public int validateSubscriberLogin(String username, String password) throws SQLException {
		String query = "SELECT subscriber_id FROM subscribe WHERE LOWER(user_name) = LOWER(?) AND password = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt("subscriber_id"); // Return subscriber ID if login is successful
				}
			}
		}
		return -1; // Return -1 if login fails
	}

	/**
	 * Validates the login credentials of a librarian.
	 *
	 * @param username The username of the librarian.
	 * @param password The password of the librarian.
	 * @return True if the credentials are valid, false otherwise.
	 * @throws SQLException If an SQL error occurs during validation.
	 */
	public boolean validateLibrarianLogin(String username, String password) throws SQLException {
		String query = "SELECT * FROM Librarian WHERE Username = ? AND Password = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password); // Use hashed password in a real application
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next(); // Returns true if a matching record is found
			}
		}
	}

	/**
	 * Loans a book to a subscriber by inserting a record into the loans table.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @param bookId       The unique ID of the book to be loaned.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public void loanBookToSubscriber(int subscriberId, int bookId) throws SQLException {
		String query = "INSERT INTO loans (subscriber_id, book_id, loan_date) VALUES (?, ?, NOW())";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, subscriberId);
			preparedStatement.setInt(2, bookId);
			preparedStatement.executeUpdate();
			System.out.println("Book loaned to subscriber: " + subscriberId);
		}
	}

	/**
	 * Checks if a book is available for loan by querying the available copies in
	 * the database.
	 *
	 * @param bookId The unique ID of the book.
	 * @return True if the book is available, false otherwise.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public boolean isBookAvailable(int bookId) throws SQLException {
		String query = "SELECT available_copies FROM books WHERE id = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, bookId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt("available_copies") > 0;
				}
			}
		}
		return false; // Default to unavailable
	}

	/**
	 * Processes the return of a book from a subscriber by removing the loan record,
	 * incrementing the available copies of the book, and updating the subscription
	 * history.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @param bookId       The unique ID of the book being returned.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public void returnBookFromSubscriber(int subscriberId, int bookId) throws SQLException {
		String deleteLoanQuery = "DELETE FROM loans WHERE subscriber_id = ? AND book_id = ?";
		String incrementCopiesQuery = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";

		try (Connection connection = connect()) {
			connection.setAutoCommit(false);
			try (PreparedStatement deleteStmt = connection.prepareStatement(deleteLoanQuery);
					PreparedStatement incrementStmt = connection.prepareStatement(incrementCopiesQuery)) {
				deleteStmt.setInt(1, subscriberId);
				deleteStmt.setInt(2, bookId);
				deleteStmt.executeUpdate();

				incrementStmt.setInt(1, bookId);
				incrementStmt.executeUpdate();

				addSubscriptionHistory(subscriberId, "Returned book ID: " + bookId);
				connection.commit();
				System.out.println("Book returned and available copies updated.");
			} catch (SQLException e) {
				connection.rollback();
				throw e;
			}
		}
	}

	/**
	 * Retrieves detailed information about a subscriber based on their unique ID.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @return A formatted string containing subscriber details, or a message if not
	 *         found.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public String getSubscriberInfo(int subscriberId) throws SQLException {
		String query = "SELECT * FROM subscribers WHERE subscriber_id = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, subscriberId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return "Subscriber ID: " + resultSet.getInt("subscriber_id") + ", Name: "
							+ resultSet.getString("subscriber_name") + ", Email: "
							+ resultSet.getString("subscriber_email") + ", Phone: "
							+ resultSet.getString("subscriber_phone");
				} else {
					return "Subscriber not found.";
				}
			}
		}
	}

	/**
	 * Adds a new subscriber to the system with the provided details.
	 *
	 * @param id       The unique ID of the subscriber.
	 * @param name     The first name of the subscriber.
	 * @param lastName The last name of the subscriber.
	 * @param userName The username of the subscriber.
	 * @param phone    The phone number of the subscriber.
	 * @param email    The email address of the subscriber.
	 * @param password The password for the subscriber's account.
	 * @param status   The status of the subscriber (e.g., active, inactive).
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public void addSubscriber(int id, String name, String lastName, String userName, String phone, String email,
			String password, String status) throws SQLException {
		String query = "INSERT INTO subscribe (subscriber_id, subscriber_name,last_name, user_name, "
				+ "subscriber_phone_number, subscriber_email, password, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, name);
			preparedStatement.setString(3, lastName);
			preparedStatement.setString(4, userName);
			preparedStatement.setString(5, phone);
			preparedStatement.setString(6, email);
			preparedStatement.setString(7, password);
			preparedStatement.setString(8, status);
			preparedStatement.executeUpdate();
			System.out.println("New subscriber added: " + name + " " + lastName);
		}
	}

	/**
	 * Checks if a subscriber is active by querying their status in the database.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @return True if the subscriber is active, false otherwise.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public boolean isSubscriberActive(int subscriberId) throws SQLException {
		String query = "SELECT status FROM subscribe WHERE subscriber_id = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, subscriberId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return "active".equalsIgnoreCase(resultSet.getString("status"));
				}
			}
		}
		return false; // Default to inactive if not found
	}

	/**
	 * Checks if a loan record exists for a specific subscriber and book
	 * combination.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @param bookId       The unique ID of the book.
	 * @return True if a loan record exists, false otherwise.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public boolean isLoanExists(int subscriberId, int bookId) throws SQLException {
		String query = "SELECT COUNT(*) FROM loans WHERE subscriber_id = ? AND book_id = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, subscriberId);
			preparedStatement.setInt(2, bookId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt(1) > 0; // Return true if count > 0
				}
			}
		}
		return false;
	}

	/**
	 * Loans a book to a subscriber, creating a loan record and decrementing the
	 * book's available copies.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @param bookId       The unique ID of the book being loaned.
	 * @param loanDate     The date the loan starts.
	 * @param returnDate   The expected return date of the loan.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public void loanBookToSubscriber(int subscriberId, int bookId, String loanDate, String returnDate)
			throws SQLException {
		String insertLoanQuery = "INSERT INTO loans (subscriber_id, book_id, loan_date, return_date) VALUES (?, ?, ?, ?)";
		String decrementCopiesQuery = "UPDATE books SET available_copies = available_copies - 1 WHERE id = ?";

		try (Connection connection = connect();
				PreparedStatement loanStmt = connection.prepareStatement(insertLoanQuery);
				PreparedStatement decrementStmt = connection.prepareStatement(decrementCopiesQuery)) {

			connection.setAutoCommit(false);

			// Insert loan record
			loanStmt.setInt(1, subscriberId);
			loanStmt.setInt(2, bookId);
			loanStmt.setString(3, loanDate);
			loanStmt.setString(4, returnDate);
			loanStmt.executeUpdate();

			// Decrement available copies
			decrementStmt.setInt(1, bookId);
			decrementStmt.executeUpdate();

			connection.commit();
			System.out.println("Book loaned to subscriber: " + subscriberId);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Retrieves a list of subscriber IDs who have overdue loans and are still
	 * active.
	 *
	 * @return A list of delinquent subscriber IDs.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public List<Integer> getDelinquentSubscribers() throws SQLException {
		String query = """
				    SELECT DISTINCT l.subscriber_id
				    FROM loans l
				    JOIN subscribe s ON l.subscriber_id = s.subscriber_id
				    WHERE l.return_date < CURDATE() AND s.status = 'active'
				""";

		List<Integer> delinquentSubscribers = new ArrayList<>();

		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				delinquentSubscribers.add(resultSet.getInt("subscriber_id"));
			}
		}

		return delinquentSubscribers;
	}

	/**
	 * Updates the status of a subscriber to inactive in the database.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public void updateSubscriberStatusToInactive(int subscriberId) throws SQLException {
		String query = "UPDATE subscribe SET status = 'inactive' WHERE subscriber_id = ?";

		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, subscriberId);
			preparedStatement.executeUpdate();
		}
	}

	/**
	 * Retrieves the counts of subscribers by their status for the current month,
	 * grouped by status and month.
	 *
	 * @return A formatted string containing the status, month, and count for each
	 *         group.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public String getSubscriberStatusCountsByMonth() throws SQLException {
		String query = """
				    SELECT status, DATE_FORMAT(CURDATE(), '%Y-%m') AS month, COUNT(*) AS count
				    FROM subscribe
				    GROUP BY status, month
				    ORDER BY month, status
				""";

		StringBuilder result = new StringBuilder();

		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				result.append(resultSet.getString("status")).append(",").append(resultSet.getString("month"))
						.append(",").append(resultSet.getInt("count")).append(";");
			}
		}
		return result.toString().trim();
	}

	/**
	 * Retrieves the loan duration for all active loans, including subscriber names,
	 * book names, and loan days.
	 *
	 * @return A formatted string containing subscriber name, book name, and loan
	 *         days.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public String getLoansTime() throws SQLException {
		String query = """
				    SELECT s.subscriber_name, b.name AS book_name,
				           DATEDIFF(COALESCE(l.return_date, CURDATE()), l.loan_date) AS loan_days
				    FROM loans l
				    JOIN subscribe s ON l.subscriber_id = s.subscriber_id
				    JOIN books b ON l.book_id = b.id
				""";

		StringBuilder result = new StringBuilder();

		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				result.append(resultSet.getString("subscriber_name")).append(",")
						.append(resultSet.getString("book_name")).append(",").append(resultSet.getInt("loan_days"))
						.append(";");
			}
		}
		return result.toString().trim(); // Example: "John,The Great Gatsby,15;Jane,1984,30"
	}

	/**
	 * Searches for books in the database based on various criteria such as ID,
	 * name, author, subject, or description.
	 *
	 * @param query The search keyword or book ID.
	 * @return A formatted string containing matching books' details.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public String searchBooks(String query) throws SQLException {
		String sql = """
				    SELECT id, name, author, subject, description, available_copies, location
				    FROM books
				    WHERE id = ? OR
				          name LIKE ? OR
				          author LIKE ? OR
				          subject LIKE ? OR
				          description LIKE ?
				""";

		StringBuilder result = new StringBuilder();
		try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
			// Bind the query parameter for all fields
			try {
				statement.setInt(1, Integer.parseInt(query)); // For book ID
			} catch (NumberFormatException e) {
				statement.setNull(1, java.sql.Types.INTEGER); // If not a valid integer
			}
			statement.setString(2, "%" + query + "%"); // For name
			statement.setString(3, "%" + query + "%"); // For author
			statement.setString(4, "%" + query + "%"); // For subject
			statement.setString(5, "%" + query + "%"); // For description

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					result.append(resultSet.getInt("id")).append(",").append(resultSet.getString("name")).append(",")
							.append(resultSet.getString("author")).append(",").append(resultSet.getString("subject"))
							.append(",").append(resultSet.getString("description")).append(",")
							.append(resultSet.getInt("available_copies")).append(",")
							.append(resultSet.getString("location")).append(";");
				}
			}
		}
		return result.toString();
	}

	/**
	 * Reserves a book for the currently logged-in subscriber if it is not available
	 * for loan.
	 *
	 * @param bookId The unique ID of the book to be reserved.
	 * @throws SQLException If no subscriber is logged in, the book is available, or
	 *                      no valid return date is found.
	 */
	public void reserveBook(int bookId) throws SQLException {
		if (currentSubscriberId == -1) {
			throw new SQLException("No subscriber is currently logged in.");
		}

		if (isBookAvailable(bookId)) {
			throw new SQLException("This book is currently available. Reservation is not required.");
		}

		String closestReturnDate = getClosestReturnDate(bookId);
		if (closestReturnDate == null) {
			throw new SQLException(
					"No valid return date found for this book. Ensure loans exist for book ID: " + bookId);
		}

		String sql = "INSERT INTO reserve_books (subscriber_id, book_id, closest_return_date) VALUES (?, ?, ?)";
		try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, currentSubscriberId);
			statement.setInt(2, bookId);
			statement.setString(3, closestReturnDate); // Use setString for String dates
			statement.executeUpdate();
		}
	}

	/**
	 * Retrieves the closest return date for a specific book based on current loans.
	 *
	 * @param bookId The unique ID of the book.
	 * @return The closest return date as a string.
	 * @throws SQLException If no valid return date is found for the book.
	 */
	public String getClosestReturnDate(int bookId) throws SQLException {
		String query = "SELECT MIN(return_date) FROM loans WHERE book_id = ? AND return_date > NOW()";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, bookId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getString(1); // Fetch the closest return date
				} else {
					throw new SQLException("No valid return date found for this book. Cannot reserve.");
				}
			}
		}
	}

	/**
	 * Retrieves the ID of the currently logged-in subscriber.
	 *
	 * @return The ID of the currently logged-in subscriber.
	 */
	// Getter for currentSubscriberId
	public int getCurrentSubscriberId() {
		return this.currentSubscriberId;
	}

	/**
	 * Sets the ID of the currently logged-in subscriber.
	 *
	 * @param subscriberId The ID of the subscriber to set as currently logged in.
	 */
	// Setter for currentSubscriberId
	public void setCurrentSubscriberId(int subscriberId) {
		this.currentSubscriberId = subscriberId;
	}

	/**
	 * Retrieves the profile information of a specific subscriber.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @return A formatted string containing subscriber profile details.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public String getSubscriberProfile(int subscriberId) throws SQLException {
		String query = "SELECT subscriber_id, subscriber_name, last_name, subscriber_email, subscriber_phone_number, status "
				+ "FROM subscribe WHERE subscriber_id = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, subscriberId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt("subscriber_id") + "," + resultSet.getString("subscriber_name") + ","
							+ resultSet.getString("last_name") + "," + resultSet.getString("subscriber_email") + ","
							+ resultSet.getString("subscriber_phone_number") + "," + resultSet.getString("status");
				}
			}
		}
		return null;
	}

	/**
	 * Updates the profile information of a specific subscriber.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @param name         The updated first name of the subscriber.
	 * @param lastName     The updated last name of the subscriber.
	 * @param email        The updated email address of the subscriber.
	 * @param phone        The updated phone number of the subscriber.
	 * @param username     The updated username of the subscriber.
	 * @param password     The updated password of the subscriber.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public void updateSubscriberProfile(int subscriberId, String name, String lastName, String email, String phone,
			String username, String password) throws SQLException {
		String query = """
				    UPDATE subscribe
				    SET subscriber_name = ?, last_name = ?, subscriber_email = ?, subscriber_phone_number = ?, user_name = ?, password = ?
				    WHERE subscriber_id = ?
				""";

		try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, name);
			statement.setString(2, lastName);
			statement.setString(3, email);
			statement.setString(4, phone);
			statement.setString(5, username);
			statement.setString(6, password);
			statement.setInt(7, subscriberId);

			statement.executeUpdate();
		}
	}

	/**
	 * Retrieves detailed data for a specific subscriber.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @return A formatted string containing subscriber details.
	 * @throws SQLException If no subscriber is found with the given ID.
	 */
	public String getSubscriberData(int subscriberId) throws SQLException {
		String query = """
				    SELECT subscriber_id, subscriber_name, last_name, subscriber_email, subscriber_phone_number, user_name, password
				    FROM subscribe
				    WHERE subscriber_id = ?
				""";

		try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, subscriberId);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return String.join(",", String.valueOf(resultSet.getInt("subscriber_id")),
							resultSet.getString("subscriber_name"), resultSet.getString("last_name"),
							resultSet.getString("subscriber_email"), resultSet.getString("subscriber_phone_number"),
							resultSet.getString("user_name"), resultSet.getString("password"));
				}
			}
		}
		throw new SQLException("No subscriber found with ID: " + subscriberId);
	}

	/**
	 * Updates the detailed data for a specific subscriber.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @param name         The updated first name of the subscriber.
	 * @param lastName     The updated last name of the subscriber.
	 * @param email        The updated email address of the subscriber.
	 * @param phone        The updated phone number of the subscriber.
	 * @param username     The updated username of the subscriber.
	 * @param password     The updated password of the subscriber.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public void updateSubscriberData(int subscriberId, String name, String lastName, String email, String phone,
			String username, String password) throws SQLException {
		String query = """
				    UPDATE subscribe
				    SET subscriber_name = ?, last_name = ?, subscriber_email = ?, subscriber_phone_number = ?, user_name = ?, password = ?
				    WHERE subscriber_id = ?
				""";

		try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, name);
			statement.setString(2, lastName);
			statement.setString(3, email);
			statement.setString(4, phone);
			statement.setString(5, username);
			statement.setString(6, password);
			statement.setInt(7, subscriberId);
			statement.executeUpdate();
		}
	}

	/**
	 * Retrieves a list of books that are due for return by a specific subscriber.
	 *
	 * @param subscriberId The unique ID of the subscriber.
	 * @return A formatted string containing book names and their due dates.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public String getDueBooks(int subscriberId) throws SQLException {
		String query = """
				    SELECT b.name, l.return_date
				    FROM loans l
				    INNER JOIN books b ON l.book_id = b.id
				    WHERE l.subscriber_id = ? AND l.return_date <= DATE_ADD(NOW(), INTERVAL 1 DAY)
				""";

		StringBuilder dueBooks = new StringBuilder();

		try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, subscriberId);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					String bookName = resultSet.getString("name");
					String returnDate = resultSet.getString("return_date");
					dueBooks.append(bookName).append(" (Return by: ").append(returnDate).append("); ");
				}
			}
		}

		return dueBooks.toString().trim();
	}

	/**
	 * Retrieves a list of all loans in the system as formatted strings.
	 *
	 * @return A list of strings, each containing details of a loan.
	 * @throws SQLException If an SQL error occurs during the operation.
	 */
	public List<String> getAllLoansAsString() throws SQLException {
		String query = """
				    SELECT l.loan_id, l.subscriber_id, l.book_id, l.loan_date, l.return_date, s.subscriber_name
				    FROM loans l
				    JOIN subscribe s ON l.subscriber_id = s.subscriber_id
				""";

		List<String> loans = new ArrayList<>();
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String loan = resultSet.getInt("loan_id") + "," + resultSet.getInt("subscriber_id") + ","
						+ resultSet.getInt("book_id") + "," + resultSet.getString("loan_date") + ","
						+ resultSet.getString("return_date") + "," + resultSet.getString("subscriber_name");
				System.out.println("Fetched Loan: " + loan); // Debug log
				loans.add(loan);
			}
		}
		return loans;
	}

	public boolean isBookReserved(int bookId) throws SQLException {
		String query = "SELECT COUNT(*) FROM reserve_books WHERE book_id = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, bookId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt(1) > 0; // True if the book is reserved
				}
			}
		}
		return false; // Not reserved
	}

	public boolean prolongLoan(int loanId, String newReturnDate) throws SQLException {
		String query = "UPDATE loans SET return_date = ? WHERE loan_id = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, newReturnDate);
			preparedStatement.setInt(2, loanId);
			return preparedStatement.executeUpdate() > 0; // True if the update was successful
		}
	}

	public int getBookIdByLoanId(int loanId) throws SQLException {
		String query = "SELECT book_id FROM loans WHERE loan_id = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, loanId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt("book_id");
				} else {
					throw new SQLException("No loan found with the given loan ID: " + loanId);
				}
			}
		}
	}

	public void deleteSubscriber(int subscriberId) throws SQLException {
		String query = "DELETE FROM subscribe WHERE subscriber_id = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, subscriberId);
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected == 0) {
				throw new SQLException("No subscriber found with ID: " + subscriberId);
			}
		}
	}

	public List<String> getAllBooksAsString() throws SQLException {
	    String query = "SELECT * FROM books";
	    List<String> books = new ArrayList<>();

	    try (Connection connection = connect();
	         PreparedStatement preparedStatement = connection.prepareStatement(query);
	         ResultSet resultSet = preparedStatement.executeQuery()) {
	        while (resultSet.next()) {
	            books.add(resultSet.getInt("id") + "," +
	                      resultSet.getString("name") + "," +
	                      resultSet.getString("author") + "," +
	                      resultSet.getString("subject") + "," +
	                      resultSet.getInt("available_copies") + "," +
	                      resultSet.getString("location") + "," +
	                      resultSet.getString("description"));
	        }
	    }
	    return books;
	}

	public void addBook(String name, String author, String subject, int copies, String location, String description) throws SQLException {
	    String query = "INSERT INTO books (name, author, subject, available_copies, location, description) VALUES (?, ?, ?, ?, ?, ?)";

	    try (Connection connection = connect();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setString(1, name);
	        preparedStatement.setString(2, author);
	        preparedStatement.setString(3, subject);
	        preparedStatement.setInt(4, copies);
	        preparedStatement.setString(5, location);
	        preparedStatement.setString(6, description);
	        preparedStatement.executeUpdate();
	    }
	}

	public void deleteBook(String name) throws SQLException {
	    String query = "DELETE FROM books WHERE name = ?";

	    try (Connection connection = connect();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setString(1, name);
	        preparedStatement.executeUpdate();
	    }
	}


}
