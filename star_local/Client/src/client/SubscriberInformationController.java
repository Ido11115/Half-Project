package client;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubscriberInformationController {

	@FXML
	private TextField searchField;
	@FXML
	private TableView<Subscriber> subscriberTable;
	@FXML
	private TableColumn<Subscriber, Integer> idColumn;
	@FXML
	private TableColumn<Subscriber, String> nameColumn;
	@FXML
	private TableColumn<Subscriber, String> lastNameColumn;
	@FXML
	private TableColumn<Subscriber, String> emailColumn;
	@FXML
	private TableColumn<Subscriber, String> statusColumn;
	@FXML
	private TableColumn<Subscriber, String> allReturnDatesColumn;
	@FXML
	private TableColumn<Subscriber, String> detailedSubscriptionHistoryColumn;

	private ObservableList<Subscriber> subscriberList = FXCollections.observableArrayList();
	private ServerCommunicator serverCommunicator;

	// Setter for ServerCommunicator
	public void setServerCommunicator(ServerCommunicator serverCommunicator) {
		this.serverCommunicator = serverCommunicator;
		if (serverCommunicator != null) {
			loadSubscriberData();
		}
	}

	@FXML
	private void initialize() {
		// Use direct getters for TableColumn cell value factories
		idColumn.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));
		nameColumn.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getName()));
		lastNameColumn.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getLastName()));
		emailColumn.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getEmail()));
		statusColumn.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStatus()));
		allReturnDatesColumn.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getAllReturnDates()));
		detailedSubscriptionHistoryColumn.setCellValueFactory(
				cellData -> new ReadOnlyStringWrapper(cellData.getValue().getSubscriptionHistory()));

	}

	private void loadSubscriberData() {
	    if (serverCommunicator == null) {
	        showError("Server communicator is not initialized.");
	        return;
	    }

	    try {
	        String response = serverCommunicator.sendRequest("GET_SUBSCRIBERS");

	        List<Subscriber> subscribers = parseSubscribers(response);
	        System.out.println("Parsed subscribers count: " + subscribers.size());

	        for (Subscriber subscriber : subscribers) {
	            String history = serverCommunicator.getSubscriptionHistory(subscriber.getId());
	            subscriber.setSubscriptionHistory(history);
	        }

	        subscriberList.setAll(subscribers);
	        subscriberTable.setItems(subscriberList);
	    } catch (Exception e) {
	        e.printStackTrace();
	        showError("Error loading subscriber data: " + e.getMessage());
	    }
	}


	private List<Subscriber> parseSubscribers(String data) {
	    List<Subscriber> subscribers = new ArrayList<>();
	    try {
	        Arrays.stream(data.split(";")).forEach(entry -> {
	            String[] details = entry.split(",");
	            try {
	                if (details.length >= 5) {
	                    subscribers.add(new Subscriber(
	                        Integer.parseInt(details[0]), // ID
	                        details[1],                  // Name
	                        details[2],                  // Last Name
	                        details[3],                  // Email
	                        details[4],                  // Status
	                        details.length > 5 ? details[5] : "" // All Return Dates
	                    ));
	                } else {
	                    System.err.println("Skipping incomplete subscriber data: " + entry);
	                }
	            } catch (Exception e) {
	                System.err.println("Error parsing subscriber entry: " + entry);
	                e.printStackTrace();
	            }
	        });
	    } catch (Exception e) {
	        System.err.println("Error parsing subscriber data: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return subscribers;
	}


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

	@FXML
	private void handleClearSearch() {
		searchField.clear();
		subscriberTable.setItems(subscriberList); // Reset the table to show all subscribers
	}

	private void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setContentText(message);
		alert.showAndWait();
	}
}
