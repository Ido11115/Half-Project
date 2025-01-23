package client;

import javafx.beans.property.*;

public class Book {
    private final StringProperty name;
    private final StringProperty author;
    private final StringProperty closestReturnDate;
    private final IntegerProperty id;
    private final IntegerProperty availableCopies;
    private final StringProperty location;
    private final StringProperty subject;
    private final StringProperty description;

    // Constructor with all fields
    public Book(String name, String author, int availableCopies, String location, int id, String subject, String description, String closestReturnDate) {
        this.name = new SimpleStringProperty(name);
        this.author = new SimpleStringProperty(author);
        this.availableCopies = new SimpleIntegerProperty(availableCopies);
        this.location = new SimpleStringProperty(location);
        this.id = new SimpleIntegerProperty(id);
        this.subject = new SimpleStringProperty(subject);
        this.description = new SimpleStringProperty(description);
        this.closestReturnDate = new SimpleStringProperty(closestReturnDate);
    }

    // Constructor without closestReturnDate
    public Book(String name, String author, int availableCopies, String location, int id, String subject, String description) {
        this(name, author, availableCopies, location, id, subject, description, "Unknown");
    }

    // Constructor for minimal fields
    public Book(String name, String author, int availableCopies, String location) {
        this(name, author, availableCopies, location, -1, "Unknown", "Unknown", "Unknown");
    }

    // Constructor for ReserveBookController
    public Book(String name, String author, String closestReturnDate, int id) {
        this(name, author, 0, "Unknown", id, "Unknown", "Unknown", closestReturnDate);
    }

    // Properties for JavaFX bindings
    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty authorProperty() {
        return author;
    }

    public StringProperty closestReturnDateProperty() {
        return closestReturnDate;
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public IntegerProperty availableCopiesProperty() {
        return availableCopies;
    }

    public StringProperty locationProperty() {
        return location;
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    // Getters
    public String getName() {
        return name.get();
    }

    public String getAuthor() {
        return author.get();
    }

    public String getClosestReturnDate() {
        return closestReturnDate.get();
    }

    public int getId() {
        return id.get();
    }

    public int getAvailableCopies() {
        return availableCopies.get();
    }

    public String getLocation() {
        return location.get();
    }

    public String getSubject() {
        return subject.get();
    }

    public String getDescription() {
        return description.get();
    }

    // Setters
    public void setName(String name) {
        this.name.set(name);
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public void setClosestReturnDate(String closestReturnDate) {
        this.closestReturnDate.set(closestReturnDate);
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies.set(availableCopies);
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
