import java.sql.SQLException;
import java.util.List;

public class BackgroundTask implements Runnable {
	private DBHandler dbHandler;

	public BackgroundTask() {
		dbHandler = new DBHandler();
	}

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
