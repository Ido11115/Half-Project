package client;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SubscriptionAction {
    private final StringProperty action;
    private final StringProperty date;

    public SubscriptionAction(String action, String date) {
        this.action = new SimpleStringProperty(action);
        this.date = new SimpleStringProperty(date);
    }

    // Getter and setter for action
    public StringProperty actionProperty() {
        return action;
    }

    public String getAction() {
        return action.get();
    }

    public void setAction(String action) {
        this.action.set(action);
    }

    // Getter and setter for date
    public StringProperty dateProperty() {
        return date;
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }
}
