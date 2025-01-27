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
     * Returns the property representing the name of the book.
     * This property can be used for JavaFX bindings and listeners.
     *
     * @return the name property of the book
     */
    public StringProperty nameProperty() {
        return name;
    }

    /**
     * Returns the property representing the author of the book.
     * This property can be used for JavaFX bindings and listeners.
     *
     * @return the author property of the book
     */
    public StringProperty authorProperty() {
        return author;
    }

    /**
     * Returns the property representing the closest return date for borrowed copies.
     * This property can be used for JavaFX bindings and listeners.
     *
     * @return the closest return date property of the book
     */
    public StringProperty closestReturnDateProperty() {
        return closestReturnDate;
    }

    /**
     * Returns the property representing the unique identifier of the book.
     * This property can be used for JavaFX bindings and listeners.
     *
     * @return the id property of the book
     */
    public IntegerProperty idProperty() {
        return id;
    }

    /**
     * Returns the property representing the number of available copies.
     * This property can be used for JavaFX bindings and listeners.
     *
     * @return the available copies property of the book
     */
    public IntegerProperty availableCopiesProperty() {
        return availableCopies;
    }

    /**
     * Returns the property representing the location of the book.
     * This property can be used for JavaFX bindings and listeners.
     *
     * @return the location property of the book
     */
    public StringProperty locationProperty() {
        return location;
    }

    /**
     * Returns the property representing the subject of the book.
     * This property can be used for JavaFX bindings and listeners.
     *
     * @return the subject property of the book
     */
    public StringProperty subjectProperty() {
        return subject;
    }

    /**
     * Returns the property representing the description of the book.
     * This property can be used for JavaFX bindings and listeners.
     *
     * @return the description property of the book
     */
    public StringProperty descriptionProperty() {
        return description;
    }

    // Getters

    /**
     * Retrieves the name of the book.
     *
     * @return the name of the book
     */
    public String getName() {
        return name.get();
    }

    /**
     * Retrieves the author of the book.
     *
     * @return the author of the book
     */
    public String getAuthor() {
        return author.get();
    }

    /**
     * Retrieves the closest return date for borrowed copies of the book.
     *
     * @return the closest return date for the book
     */
    public String getClosestReturnDate() {
        return closestReturnDate.get();
    }

    /**
     * Retrieves the unique identifier of the book.
     *
     * @return the id of the book
     */
    public int getId() {
        return id.get();
    }

    /**
     * Retrieves the number of available copies of the book.
     *
     * @return the number of available copies of the book
     */
    public int getAvailableCopies() {
        return availableCopies.get();
    }

    /**
     * Retrieves the location of the book in the library.
     *
     * @return the location of the book
     */
    public String getLocation() {
        return location.get();
    }

    /**
     * Retrieves the subject of the book.
     *
     * @return the subject of the book
     */
    public String getSubject() {
        return subject.get();
    }

    /**
     * Retrieves a brief description of the book.
     *
     * @return the description of the book
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
