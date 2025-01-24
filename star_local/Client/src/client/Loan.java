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
     * @return the loan ID property
     */
    public IntegerProperty loanIdProperty() {
        return loanId;
    }

    /**
     * @return the subscriber ID property
     */
    public IntegerProperty subscriberIdProperty() {
        return subscriberId;
    }

    /**
     * @return the book ID property
     */
    public IntegerProperty bookIdProperty() {
        return bookId;
    }

    /**
     * @return the loan date property
     */
    public StringProperty loanDateProperty() {
        return loanDate;
    }

    /**
     * @return the return date property
     */
    public StringProperty returnDateProperty() {
        return returnDate;
    }

    /**
     * @return the subscriber name property
     */
    public StringProperty subscriberNameProperty() {
        return subscriberName;
    }

    // Getters

    /**
     * @return the loan ID
     */
    public int getLoanId() {
        return loanId.get();
    }

    /**
     * @return the subscriber ID
     */
    public int getSubscriberId() {
        return subscriberId.get();
    }

    /**
     * @return the book ID
     */
    public int getBookId() {
        return bookId.get();
    }

    /**
     * @return the loan date
     */
    public String getLoanDate() {
        return loanDate.get();
    }

    /**
     * @return the return date
     */
    public String getReturnDate() {
        return returnDate.get();
    }

    /**
     * @return the subscriber name
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
