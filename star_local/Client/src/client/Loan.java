package client;

import javafx.beans.property.*;

/**
 * The Loan class represents a loan record in the library system.
 * It contains details about the loan, such as the loan ID, subscriber ID,
 * book ID, loan date, return date, and subscriber name.
 * This class is designed for use with JavaFX properties and bindings.
 */
public class Loan {

    private final IntegerProperty loanId;
    private final IntegerProperty subscriberId;
    private final IntegerProperty bookId;
    private final StringProperty loanDate;
    private final StringProperty returnDate;
    private final StringProperty subscriberName;

    /**
     * Constructs a new Loan object with the specified details.
     *
     * @param loanId         the unique identifier for the loan
     * @param subscriberId   the ID of the subscriber who borrowed the book
     * @param bookId         the ID of the book being loaned
     * @param loanDate       the date the loan was created
     * @param returnDate     the expected return date of the loan
     * @param subscriberName the name of the subscriber
     */
    public Loan(int loanId, int subscriberId, int bookId, String loanDate, String returnDate, String subscriberName) {
        this.loanId = new SimpleIntegerProperty(loanId);
        this.subscriberId = new SimpleIntegerProperty(subscriberId);
        this.bookId = new SimpleIntegerProperty(bookId);
        this.loanDate = new SimpleStringProperty(loanDate);
        this.returnDate = new SimpleStringProperty(returnDate);
        this.subscriberName = new SimpleStringProperty(subscriberName);
    }

    // Property methods for UI binding
    /**
     * Represents the loan ID property of a loan.
     * This property can be used for JavaFX bindings and listeners.
     *
     * @return the loan ID property, which uniquely identifies a loan
     */
    public IntegerProperty loanIdProperty() {
        return loanId;
    }

    /**
     * Represents the subscriber ID property of a loan.
     * This property can be used for JavaFX bindings and listeners.
     *
     * @return the subscriber ID property, which identifies the subscriber associated with the loan
     */
    public IntegerProperty subscriberIdProperty() {
        return subscriberId;
    }

    /**
     * Represents the book ID property of a loan.
     * This property can be used for JavaFX bindings and listeners.
     *
     * @return the book ID property, which identifies the book being loaned
     */
    public IntegerProperty bookIdProperty() {
        return bookId;
    }

    /**
     * Represents the loan date property of a loan.
     * This property can be used for JavaFX bindings and listeners.
     *
     * @return the loan date property, which stores the date when the loan was created
     */
    public StringProperty loanDateProperty() {
        return loanDate;
    }

    /**
     * Represents the return date property of a loan.
     * This property can be used for JavaFX bindings and listeners.
     *
     * @return the return date property, which stores the expected return date of the loan
     */
    public StringProperty returnDateProperty() {
        return returnDate;
    }

    /**
     * Represents the subscriber name property of a loan.
     * This property can be used for JavaFX bindings and listeners.
     *
     * @return the subscriber name property, which stores the name of the subscriber
     */
    public StringProperty subscriberNameProperty() {
        return subscriberName;
    }

    // Getters

    /**
     * Retrieves the loan ID.
     *
     * @return the loan ID, which uniquely identifies a loan
     */
    public int getLoanId() {
        return loanId.get();
    }

    /**
     * Retrieves the subscriber ID.
     *
     * @return the subscriber ID, which identifies the subscriber associated with the loan
     */
    public int getSubscriberId() {
        return subscriberId.get();
    }

    /**
     * Retrieves the book ID.
     *
     * @return the book ID, which identifies the book being loaned
     */
    public int getBookId() {
        return bookId.get();
    }

    /**
     * Retrieves the loan date.
     *
     * @return the loan date, which is the date when the loan was created
     */
    public String getLoanDate() {
        return loanDate.get();
    }

    /**
     * Retrieves the return date.
     *
     * @return the return date, which is the expected return date of the loan
     */
    public String getReturnDate() {
        return returnDate.get();
    }

    /**
     * Retrieves the subscriber name.
     *
     * @return the subscriber name, which is the name of the subscriber associated with the loan
     */
    public String getSubscriberName() {
        return subscriberName.get();
    }

    // Setters

    /**
     * Sets the loan ID.
     *
     * @param loanId the new loan ID
     */
    public void setLoanId(int loanId) {
        this.loanId.set(loanId);
    }

    /**
     * Sets the subscriber ID.
     *
     * @param subscriberId the new subscriber ID
     */
    public void setSubscriberId(int subscriberId) {
        this.subscriberId.set(subscriberId);
    }

    /**
     * Sets the book ID.
     *
     * @param bookId the new book ID
     */
    public void setBookId(int bookId) {
        this.bookId.set(bookId);
    }

    /**
     * Sets the loan date.
     *
     * @param loanDate the new loan date
     */
    public void setLoanDate(String loanDate) {
        this.loanDate.set(loanDate);
    }

    /**
     * Sets the return date.
     *
     * @param returnDate the new return date
     */
    public void setReturnDate(String returnDate) {
        this.returnDate.set(returnDate);
    }

    /**
     * Sets the subscriber name.
     *
     * @param subscriberName the new subscriber name
     */
    public void setSubscriberName(String subscriberName) {
        this.subscriberName.set(subscriberName);
    }
}
