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
	    actionColumn.setCellValueFactory(cellData -> {
	        if (cellData.getValue() != null) {
	            return cellData.getValue().actionProperty();
	        } else {
	            return null; // Debug: Handle null values gracefully
	        }
	    });

	    dateColumn.setCellValueFactory(cellData -> {
	        if (cellData.getValue() != null) {
	            return cellData.getValue().dateProperty();
	        } else {
	            return null; // Debug: Handle null values gracefully
	        }
	    });

	    // Allow inline editing for actions
	    actionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
	    actionColumn.setOnEditCommit(event -> {
	        SubscriptionAction action = event.getRowValue();
	        if (action != null) {
	            action.setAction(event.getNewValue());
	            System.out.println("Updated Action: " + action.getAction());
	        }
	    });

	    // Allow inline editing for dates
	    dateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
	    dateColumn.setOnEditCommit(event -> {
	        SubscriptionAction action = event.getRowValue();
	        if (action != null) {
	            action.setDate(event.getNewValue());
	            System.out.println("Updated Date: " + action.getDate());
	        }
	    });

	    // Populate status options for the ComboBox
	    statusComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive"));

	    // Debug: Log ComboBox value changes
	    statusComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
	        System.out.println("Status ComboBox changed: " + newVal);
	    });
	}


	@FXML
	private void handleSearch() {
	    try {
	        int subscriberId = Integer.parseInt(searchField.getText().trim());

	        // Fetch subscription history
	        String historyResponse = serverCommunicator.getSubscriptionHistory(subscriberId);
	        System.out.println("History Response: " + historyResponse); // Debug
	        List<SubscriptionAction> newHistory = parseSubscriptionHistory(historyResponse);

	        // Update the table
	        historyList.setAll(newHistory);
	        historyTable.setItems(historyList);

	        // Fetch subscriber status
	        String statusResponse = serverCommunicator.getSubscriberStatus(subscriberId);
	        System.out.println("Status Response: " + statusResponse); // Debug
	        statusComboBox.setValue(statusResponse);

	    } catch (NumberFormatException e) {
	        showError("Subscriber ID must be a valid number.");
	    } catch (IOException e) {
	        e.printStackTrace();
	        showError("Error fetching subscriber data: " + e.getMessage());
	    }
	}


	@FXML
	private void handleClear() {
		try {
			// Clear the search field
			searchField.clear();

			// Clear the history table
			historyList.clear();
			historyTable.setItems(historyList);

			// Reset the status combo box
			statusComboBox.setValue(null);

			System.out.println("Clear action executed.");
		} catch (Exception e) {
			e.printStackTrace();
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
	    List<SubscriptionAction> history = new ArrayList<>();
	    try {
	        String[] entries = data.split(";");
	        for (String entry : entries) {
	            String[] details = entry.split(",");
	            if (details.length == 2) {
	                history.add(new SubscriptionAction(details[0], details[1]));
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        showError("Error parsing subscription history data.");
	    }
	    return history;
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
