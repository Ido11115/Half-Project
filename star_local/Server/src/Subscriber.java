/**
 * The Subscriber class represents a library subscriber.
 * It contains information about the subscriber's ID, name, last name, email,
 * status, and all return dates for borrowed books.
 */
public class Subscriber {
    private int id;
    private String name;
    private String lastName;
    private String email;
    private String status;
    private String allReturnDates;

    /**
     * Default constructor for creating an empty Subscriber object.
     */
    public Subscriber() {}

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
     * Gets a string representing all return dates associated with the subscriber.
     *
     * @return a string of all return dates
     */
    public String getAllReturnDates() {
        return allReturnDates;
    }

    /**
     * Sets a string representing all return dates associated with the subscriber.
     *
     * @param allReturnDates the string of all return dates
     */
    public void setAllReturnDates(String allReturnDates) {
        this.allReturnDates = allReturnDates;
    }
}