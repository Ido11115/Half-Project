package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private TableColumn<Subscriber, String> detailedSubscriptionHistoryColumn;
	@FXML
	private TableColumn<Subscriber, String> allReturnDatesColumn;

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
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
		emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
		statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
		detailedSubscriptionHistoryColumn
				.setCellValueFactory(cellData -> cellData.getValue().detailedSubscriptionHistoryProperty());
		allReturnDatesColumn.setCellValueFactory(cellData -> cellData.getValue().allReturnDatesProperty());
	}

	private void loadSubscriberData() {
		if (serverCommunicator == null) {
			showError("Server communicator is not initialized.");
			return;
		}

		try {
			Object response = serverCommunicator.sendRequest("GET_SUBSCRIBERS");

			if (response instanceof String) {
				String jsonResponse = (String) response;

				// Debug: Print the JSON response received
				System.out.println("JSON Received: " + jsonResponse);

				// Parse JSON into List of Subscriber objects
				ObjectMapper objectMapper = new ObjectMapper();
				List<Subscriber> subscribers = objectMapper.readValue(jsonResponse,
						new TypeReference<List<Subscriber>>() {
						});

				// Debug: Print the parsed subscribers
				subscribers.forEach(subscriber -> System.out.println("Parsed Subscriber: " + subscriber.getName()));

				// Update the TableView
				subscriberList.setAll(subscribers);
				subscriberTable.setItems(subscriberList);
			} else {
				showError("Unexpected response from server. Please try again.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			showError("Error loading subscriber data: " + e.getMessage());
		}
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
