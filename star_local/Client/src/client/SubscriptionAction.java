/**
 * Represents an action performed on a subscription, including the action description and the date it occurred.
 */
package client;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SubscriptionAction {

    /**
     * Property representing the action performed on the subscription.
     */
    private final StringProperty action;

    /**
     * Property representing the date the action was performed.
     */
    private final StringProperty date;

    /**
     * Constructs a SubscriptionAction with the specified action and date.
     *
     * @param action the description of the subscription action
     * @param date the date the action was performed
     */
    public SubscriptionAction(String action, String date) {
        this.action = new SimpleStringProperty(action);
        this.date = new SimpleStringProperty(date);
    }

    /**
     * Gets the action property.
     *
     * @return the action property
     */
    public StringProperty actionProperty() {
        return action;
    }

    /**
     * Gets the action description.
     *
     * @return the action description
     */
    public String getAction() {
        return action.get();
    }

    /**
     * Sets the action description.
     *
     * @param action the new action description
     */
    public void setAction(String action) {
        this.action.set(action);
    }

    /**
     * Gets the date property.
     *
     * @return the date property
     */
    public StringProperty dateProperty() {
        return date;
    }

    /**
     * Gets the date the action was performed.
     *
     * @return the date the action was performed
     */
    public String getDate() {
        return date.get();
    }

    /**
     * Sets the date the action was performed.
     *
     * @param date the new date the action was performed
     */
    public void setDate(String date) {
        this.date.set(date);
    }
}
