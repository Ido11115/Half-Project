/**
 * Represents an action performed on a subscription, including the action description and the date it occurred.
 */
package client;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents an action performed on a subscription.
 * <p>
 * This class encapsulates details about a specific action performed on a subscription,
 * including a description of the action and the date it occurred.
 * It provides properties to support JavaFX binding for real-time updates in a GUI.
 * </p>
 */
public class SubscriptionAction {

    private final StringProperty action;
    private final StringProperty date;

    /**
     * Constructs a SubscriptionAction with the specified action and date.
     *
     * @param action the description of the subscription action
     * @param date   the date the action was performed
     */
    public SubscriptionAction(String action, String date) {
        this.action = new SimpleStringProperty(action);
        this.date = new SimpleStringProperty(date);
    }

    /**
     * Retrieves the property representing the action performed on the subscription.
     * This is primarily used for JavaFX bindings to observe or modify the action.
     *
     * @return the property encapsulating the action description
     */
    public StringProperty actionProperty() {
        return action;
    }

    /**
     * Retrieves the description of the action performed on the subscription.
     *
     * @return the action description
     */
    public String getAction() {
        return action.get();
    }

    /**
     * Updates the description of the action performed on the subscription.
     *
     * @param action the new action description
     */
    public void setAction(String action) {
        this.action.set(action);
    }

    /**
     * Retrieves the property representing the date the action was performed.
     * This is primarily used for JavaFX bindings to observe or modify the date.
     *
     * @return the property encapsulating the action date
     */
    public StringProperty dateProperty() {
        return date;
    }

    /**
     * Retrieves the date the action was performed.
     *
     * @return the date the action was performed
     */
    public String getDate() {
        return date.get();
    }

    /**
     * Updates the date the action was performed.
     *
     * @param date the new date the action was performed
     */
    public void setDate(String date) {
        this.date.set(date);
    }
}
