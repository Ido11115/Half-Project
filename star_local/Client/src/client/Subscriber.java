package client;

/**
 * The Subscriber class represents a subscriber in the library system.
 * It contains details such as the subscriber's ID, name, last name, email,
 * status, all return dates, and subscription history.
 */
public class Subscriber {
    private int id;
    private String name;
    private String lastName;
    private String email;
    private String status;
    private String allReturnDates;
    private String subscriptionHistory;

    /**
     * Default constructor for creating an empty Subscriber object.
     */
    public Subscriber() {
    }

    /**
     * Constructs a Subscriber object with all fields initialized.
     *
     * @param id              the subscriber's ID
     * @param name            the subscriber's first name
     * @param lastName        the subscriber's last name
     * @param email           the subscriber's email address
     * @param status          the subscriber's status (e.g., active or inactive)
     * @param allReturnDates  a string representing all return dates associated with the subscriber
     */
    public Subscriber(int id, String name, String lastName, String email, String status, String allReturnDates) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
        this.allReturnDates = allReturnDates;
    }

    /**
     * Gets the subscriber's ID.
     *
     * @return the subscriber's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the subscriber's ID.
     *
     * @param id the subscriber's ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the subscriber's first name.
     *
     * @return the subscriber's first name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the subscriber's first name.
     *
     * @param name the subscriber's first name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the subscriber's last name.
     *
     * @return the subscriber's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the subscriber's last name.
     *
     * @param lastName the subscriber's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the subscriber's email address.
     *
     * @return the subscriber's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the subscriber's email address.
     *
     * @param email the subscriber's email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the subscriber's status.
     *
     * @return the subscriber's status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the subscriber's status.
     *
     * @param status the subscriber's status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the string representing all return dates associated with the subscriber.
     *
     * @return the subscriber's return dates
     */
    public String getAllReturnDates() {
        return allReturnDates;
    }

    /**
     * Sets the string representing all return dates associated with the subscriber.
     *
     * @param allReturnDates the subscriber's return dates
     */
    public void setAllReturnDates(String allReturnDates) {
        this.allReturnDates = allReturnDates;
    }

    /**
     * Gets the subscription history of the subscriber.
     *
     * @return the subscription history
     */
    public String getSubscriptionHistory() {
        return subscriptionHistory;
    }

    /**
     * Sets the subscription history of the subscriber.
     *
     * @param subscriptionHistory the subscription history
     */
    public void setSubscriptionHistory(String subscriptionHistory) {
        this.subscriptionHistory = subscriptionHistory;
    }

    /**
     * Converts a string representation of a subscriber to a Subscriber object.
     *
     * @param data the string representation of a subscriber
     * @return a Subscriber object
     * @throws IllegalArgumentException if the input data format is invalid
     */
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

    /**
     * Converts the Subscriber object to a string representation.
     *
     * @return the string representation of the subscriber
     */
    @Override
    public String toString() {
        return id + "," + name + "," + lastName + "," + email + "," + status + ","
                + (allReturnDates != null ? allReturnDates : "");
    }
}
