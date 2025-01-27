/**
 * Controller class for managing subscriber information in a GUI.
 * Handles loading, searching, and displaying subscriber data.
 */
package client;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller class for managing subscriber information in a GUI.
 * Handles loading, searching, and displaying subscriber data in a table view.
 * <p>
 * This class communicates with the server to fetch and manage subscriber details.
 * It provides functionalities for:
 * <ul>
 *     <li>Loading subscriber data from the server</li>
 *     <li>Searching subscribers by their unique ID</li>
 *     <li>Displaying subscriber details such as name, email, and status</li>
 * </ul>
 * <p>
 * This class is designed to be used with JavaFX for creating a responsive user interface.
 */
public class SubscriberInformationController {

	/**
     * Default constructor for the SubscriberInformationController class.
     * <p>
     * Initializes the controller without any specific parameters.
     * All required fields and components are set by the FXML loader.
     * </p>
     */
    public SubscriberInformationController() {
        // No specific initialization required
    }

	
    /**
     * TextField for searching subscribers by ID.
     */
    @FXML
    private TextField searchField;

    /**
     * TableView for displaying subscriber data.
     */
    @FXML
    private TableView<Subscriber> subscriberTable;

    /**
     * TableColumn for displaying subscriber IDs.
     */
    @FXML
    private TableColumn<Subscriber, Integer> idColumn;

    /**
     * TableColumn for displaying subscriber first names.
     */
    @FXML
    private TableColumn<Subscriber, String> nameColumn;

    /**
     * TableColumn for displaying subscriber last names.
     */
    @FXML
    private TableColumn<Subscriber, String> lastNameColumn;

    /**
     * TableColumn for displaying subscriber email addresses.
     */
    @FXML
    private TableColumn<Subscriber, String> emailColumn;

    /**
     * TableColumn for displaying subscriber statuses.
     */
    @FXML
    private TableColumn<Subscriber, String> statusColumn;

    /**
     * List of subscribers to be displayed in the table.
     */
    private ObservableList<Subscriber> subscriberList = FXCollections.observableArrayList();

    /**
     * Communicator for interacting with the server.
     */
    private ServerCommunicator serverCommunicator;

    /**
     * Sets the ServerCommunicator instance for server interactions.
     * This method initializes the `serverCommunicator` field and triggers the loading of subscriber data.
     *
     * @param serverCommunicator the ServerCommunicator instance used to communicate with the server
     */
    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
        if (serverCommunicator != null) {
            loadSubscriberData();
        }
    }

    /**
     * Initializes the controller by setting up table column bindings.
     * Configures how each column in the table view retrieves data from the `Subscriber` objects.
     */
    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));
        lastNameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLastName()));
        emailColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEmail()));
        statusColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));
    }

    /**
     * Loads subscriber data from the server and populates the table view.
     * Sends a request to the server to fetch subscriber information,
     * parses the response, and updates the table with the retrieved data.
     */
    private void loadSubscriberData() {
        if (serverCommunicator == null) {
            showError("Server communicator is not initialized.");
            return;
        }

        try {
            String response = serverCommunicator.sendRequest("GET_SUBSCRIBERS");

            List<Subscriber> subscribers = parseSubscribers(response);

            subscriberList.setAll(subscribers);
            subscriberTable.setItems(subscriberList);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading subscriber data: " + e.getMessage());
        }
    }

    /**
     * Parses subscriber data received from the server.
     * Converts raw server response data into a list of `Subscriber` objects for display.
     *
     * @param data the raw subscriber data as a semicolon-separated string
     * @return a list of `Subscriber` objects representing the parsed data
     */
    private List<Subscriber> parseSubscribers(String data) {
        List<Subscriber> subscribers = new ArrayList<>();
        try {
            Arrays.stream(data.split(";")).forEach(entry -> {
                String[] details = entry.split(",", -1); // Use -1 to preserve empty fields
                if (details.length >= 5) {
                    subscribers.add(new Subscriber(
                            Integer.parseInt(details[0]), // ID
                            details[1],                  // Name
                            details[2],                  // Last Name
                            details[3],                  // Email
                            details[4],                  // Status
                            details.length > 5 && !details[5].isEmpty() ? details[5] : "No loans" // Handle missing return dates
                    ));
                }
            });
        } catch (Exception e) {
            System.err.println("Error parsing subscriber data: " + e.getMessage());
            e.printStackTrace();
        }
        return subscribers;
    }

    /**
     * Handles the search functionality for subscribers.
     * Filters the subscriber list by the ID entered in the search field
     * and updates the table view with the search results.
     */
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            subscriberTable.setItems(subscriberList); // Show all subscribers if search is empty
            return;
        }

        try {
            int subscriberId = Integer.parseInt(searchText);
            ObservableList<Subscriber> filteredList = subscriberList
                    .filtered(subscriber -> subscriber.getId() == subscriberId);
            subscriberTable.setItems(filteredList);
        } catch (NumberFormatException e) {
            showError("Subscriber ID must be a valid number.");
        }
    }

    /**
     * Clears the search field and resets the table view.
     * Restores the full list of subscribers in the table view after clearing the search input.
     */
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        subscriberTable.setItems(subscriberList); // Reset the table to show all subscribers
    }

    /**
     * Displays an error message in an alert dialog.
     * This method is used to notify the user of any issues, such as invalid input or server errors.
     *
     * @param message the error message to display in the alert dialog
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
