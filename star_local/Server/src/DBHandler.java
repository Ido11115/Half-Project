import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHandler {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/world?useSSL=false&serverTimezone=UTC";
	private static final String USER = "root";
	private static final String PASSWORD = "Aa123456";

	public Connection connect() throws SQLException {
		return DriverManager.getConnection(DB_URL, USER, PASSWORD);
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

	public boolean verifyLogin(int id, String name) throws SQLException {
		String query = "SELECT * FROM subscribe WHERE subscriber_id = ? AND subscriber_name = ?";
		try (Connection connection = connect();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, name);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next(); // Returns true if a record is found
			}
		}
	}

}
