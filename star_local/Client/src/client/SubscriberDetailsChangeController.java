package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SubscriberDetailsChangeController {

	@FXML
	private TextField searchField;
	@FXML
	private TableView<SubscriptionAction> historyTable;
	@FXML
	private TableColumn<SubscriptionAction, String> actionColumn;
	@FXML
	private TableColumn<SubscriptionAction, String> dateColumn;
	@FXML
	private ComboBox<String> statusComboBox;

	private ObservableList<SubscriptionAction> historyList = FXCollections.observableArrayList();
	private ServerCommunicator serverCommunicator;

	public void setServerCommunicator(ServerCommunicator serverCommunicator) {
		this.serverCommunicator = serverCommunicator;
	}

	@FXML
	private void initialize() {
	    // Enable editing on the table
	    historyTable.setEditable(true);

	    // Bind the columns to the SubscriptionAction properties
	    actionColumn.setCellValueFactory(cellData -> cellData.getValue().actionProperty());
	    dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

	    // Allow inline editing for actions
	    actionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
	    actionColumn.setOnEditCommit(event -> {
	        SubscriptionAction action = event.getRowValue();
	        action.setAction(event.getNewValue());
	        System.out.println("Updated Action: " + action.getAction()); // Debug log
	    });

	    // Allow inline editing for dates
	    dateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
	    dateColumn.setOnEditCommit(event -> {
	        SubscriptionAction action = event.getRowValue();
	        action.setDate(event.getNewValue());
	        System.out.println("Updated Date: " + action.getDate()); // Debug log
	    });

	    // Populate status options for the ComboBox
	    statusComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive"));
	}



	@FXML
	private void handleSearch() {
	    try {
	        int subscriberId = Integer.parseInt(searchField.getText().trim());

	        // Fetch subscription history
	        String historyResponse = serverCommunicator.getSubscriptionHistory(subscriberId);
	        List<SubscriptionAction> history = parseSubscriptionHistory(historyResponse);

	        // Debug: Log fetched history
	        System.out.println("Fetched Subscription History: " + historyResponse);

	        // Update the table with subscription history
	        historyList.setAll(history);
	        historyTable.setItems(historyList);

	        // Fetch subscriber status
	        String statusResponse = serverCommunicator.getSubscriberStatus(subscriberId);
	        System.out.println("Fetched Status: " + statusResponse); // Debug log
	        statusComboBox.setValue(statusResponse);

	    } catch (NumberFormatException e) {
	        showError("Subscriber ID must be a valid number.");
	    } catch (IOException e) {
	        e.printStackTrace();
	        showError("Error fetching subscriber data: " + e.getMessage());
	    }
	}



	@FXML
	private void handleAddAction() {
	    SubscriptionAction newAction = new SubscriptionAction("New Action", "Enter Date");
	    historyList.add(newAction);
	    historyTable.setItems(historyList);
	    historyTable.getSelectionModel().select(newAction); // Optional: Auto-select the new row
	}



	@FXML
	private void handleRemoveAction() {
		SubscriptionAction selected = historyTable.getSelectionModel().getSelectedItem();
		if (selected != null) {
			historyList.remove(selected);
		}
	}

	@FXML
	private void handleSaveChanges() {
		try {
			// Save subscription history
			List<String> actions = new ArrayList<>();
			for (SubscriptionAction action : historyList) {
				actions.add(action.getAction() + "," + action.getDate());
			}
			serverCommunicator.saveSubscriptionHistory(searchField.getText().trim(), actions);

			// Save subscriber status
			serverCommunicator.updateSubscriberStatus(searchField.getText().trim(), statusComboBox.getValue());

			showInfo("Subscriber details updated successfully.");
		} catch (IOException e) {
			e.printStackTrace();
			showError("Error saving subscriber details: " + e.getMessage());
		}
	}

	private List<SubscriptionAction> parseSubscriptionHistory(String data) {
	    List<SubscriptionAction> actions = new ArrayList<>();
	    for (String entry : data.split("\n")) {
	        String[] parts = entry.split(",");
	        if (parts.length == 2) {
	            actions.add(new SubscriptionAction(parts[0], parts[1])); // Action, Date
	        }
	    }
	    return actions;
	}


	private void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void showInfo(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setContentText(message);
		alert.showAndWait();
	}
}
