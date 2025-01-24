package client;

import javafx.beans.property.*;

/**
 * Represents a Book entity with properties for name, author, available copies, location, subject,
 * description, and the closest return date. This class is designed for use with JavaFX bindings.
 */
public class Book {

    private final StringProperty name;
    private final StringProperty author;
    private final StringProperty closestReturnDate;
    private final IntegerProperty id;
    private final IntegerProperty availableCopies;
    private final StringProperty location;
    private final StringProperty subject;
    private final StringProperty description;

    /**
     * Constructs a Book with all fields.
     *
     * @param name              the name of the book
     * @param author            the author of the book
     * @param availableCopies   the number of available copies
     * @param location          the location of the book
     * @param id                the unique identifier of the book
     * @param subject           the subject of the book
     * @param description       a brief description of the book
     * @param closestReturnDate the closest return date for borrowed copies
     */
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

    /**
     * Constructs a Book without the closest return date.
     *
     * @param name            the name of the book
     * @param author          the author of the book
     * @param availableCopies the number of available copies
     * @param location        the location of the book
     * @param id              the unique identifier of the book
     * @param subject         the subject of the book
     * @param description     a brief description of the book
     */
    public Book(String name, String author, int availableCopies, String location, int id, String subject, String description) {
        this(name, author, availableCopies, location, id, subject, description, "Unknown");
    }

    /**
     * Constructs a Book with minimal fields.
     *
     * @param name            the name of the book
     * @param author          the author of the book
     * @param availableCopies the number of available copies
     * @param location        the location of the book
     */
    public Book(String name, String author, int availableCopies, String location) {
        this(name, author, availableCopies, location, -1, "Unknown", "Unknown", "Unknown");
    }

    /**
     * Constructs a Book for use with the ReserveBookController.
     *
     * @param name              the name of the book
     * @param author            the author of the book
     * @param closestReturnDate the closest return date for borrowed copies
     * @param id                the unique identifier of the book
     */
    public Book(String name, String author, String closestReturnDate, int id) {
        this(name, author, 0, "Unknown", id, "Unknown", "Unknown", closestReturnDate);
    }

    // Properties for JavaFX bindings

    /**
     * @return the name property
     */
    public StringProperty nameProperty() {
        return name;
    }

    /**
     * @return the author property
     */
    public StringProperty authorProperty() {
        return author;
    }

    /**
     * @return the closest return date property
     */
    public StringProperty closestReturnDateProperty() {
        return closestReturnDate;
    }

    /**
     * @return the id property
     */
    public IntegerProperty idProperty() {
        return id;
    }

    /**
     * @return the available copies property
     */
    public IntegerProperty availableCopiesProperty() {
        return availableCopies;
    }

    /**
     * @return the location property
     */
    public StringProperty locationProperty() {
        return location;
    }

    /**
     * @return the subject property
     */
    public StringProperty subjectProperty() {
        return subject;
    }

    /**
     * @return the description property
     */
    public StringProperty descriptionProperty() {
        return description;
    }

    // Getters

    /**
     * @return the name of the book
     */
    public String getName() {
        return name.get();
    }

    /**
     * @return the author of the book
     */
    public String getAuthor() {
        return author.get();
    }

    /**
     * @return the closest return date for borrowed copies
     */
    public String getClosestReturnDate() {
        return closestReturnDate.get();
    }

    /**
     * @return the unique identifier of the book
     */
    public int getId() {
        return id.get();
    }

    /**
     * @return the number of available copies
     */
    public int getAvailableCopies() {
        return availableCopies.get();
    }

    /**
     * @return the location of the book
     */
    public String getLocation() {
        return location.get();
    }

    /**
     * @return the subject of the book
     */
    public String getSubject() {
        return subject.get();
    }

    /**
     * @return a brief description of the book
     */
    public String getDescription() {
        return description.get();
    }

    // Setters

    /**
     * Sets the name of the book.
     *
     * @param name the new name of the book
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Sets the author of the book.
     *
     * @param author the new author of the book
     */
    public void setAuthor(String author) {
        this.author.set(author);
    }

    /**
     * Sets the closest return date for borrowed copies.
     *
     * @param closestReturnDate the new closest return date
     */
    public void setClosestReturnDate(String closestReturnDate) {
        this.closestReturnDate.set(closestReturnDate);
    }

    /**
     * Sets the unique identifier of the book.
     *
     * @param id the new unique identifier
     */
    public void setId(int id) {
        this.id.set(id);
    }

    /**
     * Sets the number of available copies.
     *
     * @param availableCopies the new number of available copies
     */
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies.set(availableCopies);
    }

    /**
     * Sets the location of the book.
     *
     * @param location the new location of the book
     */
    public void setLocation(String location) {
        this.location.set(location);
    }

    /**
     * Sets the subject of the book.
     *
     * @param subject the new subject of the book
     */
    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    /**
     * Sets a brief description of the book.
     *
     * @param description the new description of the book
     */
    public void setDescription(String description) {
        this.description.set(description);
    }
}
