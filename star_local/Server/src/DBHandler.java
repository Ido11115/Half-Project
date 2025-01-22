import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHandler {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/world?useSSL=false&serverTimezone=UTC";
	private static final String USER = "root";
	private static final String PASSWORD = "Aa123456";

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

	public String getAllSubscribersAsString() throws SQLException {
		String query = """
				    SELECT s.subscriber_id, s.subscriber_name, s.last_name, s.subscriber_email, s.status,
				           GROUP_CONCAT(l.return_date SEPARATOR '\n') AS all_return_dates
				    FROM subscribe s
				    LEFT JOIN loans l ON s.subscriber_id = l.subscriber_id
				    GROUP BY s.subscriber_id, s.subscriber_name, s.last_name, s.subscriber_email, s.status
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
						.append(resultSet.getString("status")).append(",")
						.append(resultSet.getString("all_return_dates") != null
								? resultSet.getString("all_return_dates")
								: "")
						.append(";");
			}
		}
		return result.toString();
	}

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
	                history.append(resultSet.getString("action"))
	                       .append(",")
	                       .append(resultSet.getTimestamp("action_date"))
	                       .append(";");
	            }
	        }
	    }
	    return history.toString().trim();
	}


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

	public void updateSubscriberStatus(int subscriberId, String newStatus) throws SQLException {
		String query = "UPDATE subscribe SET status = ? WHERE subscriber_id = ?";

		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, newStatus);
			preparedStatement.setInt(2, subscriberId);
			preparedStatement.executeUpdate();
		}
	}

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

	public boolean verifySubscriberLogin(String name, String password) throws SQLException {
		String query = "SELECT * FROM subscribe WHERE subscriber_name = ? AND password = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, password); // Assume password is already hashed
			System.out.println("Executing query: " + query + " with Username=" + name + ", Password=" + password);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next(); // Returns true if a record is found
			}
		}
	}

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

	public boolean validateSubscriberLogin(String username, String password) throws SQLException {
		String query = "SELECT * FROM subscribe WHERE LOWER(subscriber_name) = LOWER(?) AND password = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);

			System.out
					.println("Executing query: " + query + " with Username=" + username + " and Password=" + password);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				boolean found = resultSet.next();
				System.out.println("Login validation result for Username=" + username + ": " + found);
				return found;
			}
		}
	}

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
	    return false; // Default to false if the book does not exist or no copies are available
	}

	

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

	public boolean isSubscriberActive(int subscriberId) throws SQLException {
		String query = "SELECT status FROM subscribe WHERE subscriber_id = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, subscriberId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return "Active".equalsIgnoreCase(resultSet.getString("status"));
				}
			}
		}
		return false;
	}

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

	public void loanBookToSubscriber(int subscriberId, int bookId, String loanDate, String returnDate) throws SQLException {
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

	public void updateSubscriberStatusToInactive(int subscriberId) throws SQLException {
		String query = "UPDATE subscribe SET status = 'inactive' WHERE subscriber_id = ?";

		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, subscriberId);
			preparedStatement.executeUpdate();
		}
	}

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

}
