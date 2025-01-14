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

	public List<Subscriber> getAllSubscribersList() throws SQLException {
		List<Subscriber> subscribers = new ArrayList<>();
		String query = "SELECT * FROM subscribe";

		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				subscribers.add(new Subscriber(resultSet.getInt("subscriber_id"),
						resultSet.getString("subscriber_name"), resultSet.getString("subscriber_phone_number"),
						resultSet.getString("subscriber_email"), resultSet.getInt("detailed_subscription_history")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}

		return subscribers;
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

	        System.out.println("Executing query: " + query + " with Username=" + username + " and Password=" + password);

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

	public void addSubscriberWithId(int id, String name, String phone, String email, String password) throws SQLException {
	    String query = "INSERT INTO subscribe (subscriber_id, subscriber_name, subscriber_phone_number, subscriber_email, password) VALUES (?, ?, ?, ?, ?)";
	    try (Connection connection = connect();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setInt(1, id);
	        preparedStatement.setString(2, name);
	        preparedStatement.setString(3, phone);
	        preparedStatement.setString(4, email);
	        preparedStatement.setString(5, password);
	        preparedStatement.executeUpdate();
	        System.out.println("New subscriber added with ID: " + id);
	    }
	

	}


	


}
