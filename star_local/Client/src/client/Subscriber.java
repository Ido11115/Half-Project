package client;

import javafx.beans.property.*;

public class Subscriber {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty lastName;
    private final StringProperty email;
    private final StringProperty status;
    private final StringProperty detailedSubscriptionHistory;
    private final StringProperty allReturnDates;

    // Default constructor for Jackson
    public Subscriber() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.lastName = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.status = new SimpleStringProperty();
        this.detailedSubscriptionHistory = new SimpleStringProperty();
        this.allReturnDates = new SimpleStringProperty();
    }

    // Constructor to initialize all fields
    public Subscriber(int id, String name, String lastName, String email, String status, 
                      String detailedSubscriptionHistory, String latestReturnDates) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.lastName = new SimpleStringProperty(lastName);
        this.email = new SimpleStringProperty(email);
        this.status = new SimpleStringProperty(status);
        this.detailedSubscriptionHistory = new SimpleStringProperty(detailedSubscriptionHistory);
        this.allReturnDates = new SimpleStringProperty(latestReturnDates);
    }

    // Getters for JavaFX properties (used for TableView binding)
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty detailedSubscriptionHistoryProperty() {
        return detailedSubscriptionHistory;
    }

    public StringProperty allReturnDatesProperty() {
        return allReturnDates;
    }

    // Getters for values
    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getLastName() {
        return lastName.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getStatus() {
        return status.get();
    }

    public String getDetailedSubscriptionHistory() {
        return detailedSubscriptionHistory.get();
    }

    public String getallReturnDates() {
        return allReturnDates.get();
    }

    // Setters for values
    public void setId(int id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public void setDetailedSubscriptionHistory(String detailedSubscriptionHistory) {
        this.detailedSubscriptionHistory.set(detailedSubscriptionHistory);
    }

    public void setallReturnDates(String allReturnDates) {
        this.allReturnDates.set(allReturnDates);
    }
}
