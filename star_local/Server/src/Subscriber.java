public class Subscriber {
    private int id;
    private String name;
    private String lastName;
    private String email;
    private String status;
    private String allReturnDates;

    // Default constructor
    public Subscriber() {}

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
}
