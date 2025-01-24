import java.sql.SQLException;
import java.util.List;

/**
 * A background task that fetches delinquent subscribers from the database and updates their status to 'inactive'.
 * This class implements the {@link Runnable} interface and is designed to be executed in a separate thread.
 */
public class BackgroundTask implements Runnable {
	/**
	 * A database handler used for fetching and updating subscriber information.
	 */
	private DBHandler dbHandler;

	/**
	 * Constructs a new {@code BackgroundTask} instance and initializes the database handler.
	 */
	public BackgroundTask() {
		dbHandler = new DBHandler();
	}

	/**
	 * Executes the background task to process delinquent subscribers.
	 *
	 * <p>
	 * This method fetches a list of delinquent subscribers from the database,
	 * logs their IDs, and updates their status to 'inactive'. If an SQL exception
	 * occurs during processing, it will be logged.
	 * </p>
	 */
	@Override
	public void run() {
		try {
			// Fetch delinquent subscribers
			List<Integer> delinquentSubscribers = dbHandler.getDelinquentSubscribers();
			System.out.println("Delinquent Subscribers: " + delinquentSubscribers);

			// Update their status to 'inactive'
			for (int subscriberId : delinquentSubscribers) {
				dbHandler.updateSubscriberStatusToInactive(subscriberId);
				System.out.println("Updated Subscriber ID " + subscriberId + " to inactive.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
