package client;

import javafx.beans.property.*;

public class Loan {
    private final IntegerProperty loanId;
    private final IntegerProperty subscriberId;
    private final IntegerProperty bookId;
    private final StringProperty loanDate;
    private final StringProperty returnDate;
    private final StringProperty subscriberName;

    public Loan(int loanId, int subscriberId, int bookId, String loanDate, String returnDate, String subscriberName) {
        this.loanId = new SimpleIntegerProperty(loanId);
        this.subscriberId = new SimpleIntegerProperty(subscriberId);
        this.bookId = new SimpleIntegerProperty(bookId);
        this.loanDate = new SimpleStringProperty(loanDate);
        this.returnDate = new SimpleStringProperty(returnDate);
        this.subscriberName = new SimpleStringProperty(subscriberName);
    }

    // Property methods for UI binding
    public IntegerProperty loanIdProperty() {
        return loanId;
    }

    public IntegerProperty subscriberIdProperty() {
        return subscriberId;
    }

    public IntegerProperty bookIdProperty() {
        return bookId;
    }

    public StringProperty loanDateProperty() {
        return loanDate;
    }

    public StringProperty returnDateProperty() {
        return returnDate;
    }

    public StringProperty subscriberNameProperty() {
        return subscriberName;
    }

    // Getters
    public int getLoanId() {
        return loanId.get();
    }

    public int getSubscriberId() {
        return subscriberId.get();
    }

    public int getBookId() {
        return bookId.get();
    }

    public String getLoanDate() {
        return loanDate.get();
    }

    public String getReturnDate() {
        return returnDate.get();
    }

    public String getSubscriberName() {
        return subscriberName.get();
    }

    // Setters
    public void setLoanId(int loanId) {
        this.loanId.set(loanId);
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId.set(subscriberId);
    }

    public void setBookId(int bookId) {
        this.bookId.set(bookId);
    }

    public void setLoanDate(String loanDate) {
        this.loanDate.set(loanDate);
    }

    public void setReturnDate(String returnDate) {
        this.returnDate.set(returnDate);
    }

    public void setSubscriberName(String subscriberName) {
        this.subscriberName.set(subscriberName);
    }
}
