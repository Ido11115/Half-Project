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

public class SubscriberInformationController {

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
     * Sets the ServerCommunicator instance and loads subscriber data if the communicator is initialized.
     *
     * @param serverCommunicator the ServerCommunicator instance
     */
    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
        if (serverCommunicator != null) {
            loadSubscriberData();
        }
    }

    /**
     * Initializes the controller by setting up table columns.
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
     * Loads subscriber data from the server and populates the table.
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
     *
     * @param data the raw subscriber data as a string
     * @return a list of Subscriber objects
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
     * Handles the search functionality by filtering subscribers by ID.
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
     * Clears the search field and resets the table to show all subscribers.
     */
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        subscriberTable.setItems(subscriberList); // Reset the table to show all subscribers
    }

    /**
     * Displays an error message in an alert dialog.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
