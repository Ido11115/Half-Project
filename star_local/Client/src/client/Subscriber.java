package client;

public class Subscriber {
	private int id;
	private String name;
	private String lastName;
	private String email;
	private String status;
	private String allReturnDates;
	private String subscriptionHistory;

	// Default constructor
	public Subscriber() {
	}

	// Constructor to initialize all fields
	public Subscriber(int id, String name, String lastName, String email, String status, String allReturnDates) {
		this.id = id;
		this.name = name;
		this.lastName = lastName;
		this.email = email;
		this.status = status;
		this.allReturnDates = allReturnDates;
	}

	// Getters and setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAllReturnDates() {
		return allReturnDates;
	}

	public void setAllReturnDates(String allReturnDates) {
		this.allReturnDates = allReturnDates;
	}

	public String getSubscriptionHistory() {
		return subscriptionHistory;
	}

	public void setSubscriptionHistory(String subscriptionHistory) {
		this.subscriptionHistory = subscriptionHistory;
	}

	// Convert string to Subscriber
	public static Subscriber fromString(String data) {
		String[] parts = data.split(",");
		if (parts.length < 5) {
			throw new IllegalArgumentException("Invalid subscriber data format.");
		}
		int id = Integer.parseInt(parts[0]);
		String name = parts[1];
		String lastName = parts[2];
		String email = parts[3];
		String status = parts[4];
		String allReturnDates = parts.length > 5 ? parts[5] : "";
		return new Subscriber(id, name, lastName, email, status, allReturnDates);
	}

	// Convert Subscriber to string
	@Override
	public String toString() {
		return id + "," + name + "," + lastName + "," + email + "," + status + ","
				+ (allReturnDates != null ? allReturnDates : "");
	}
}
