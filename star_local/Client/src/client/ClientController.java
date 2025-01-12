package client;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.ButtonBar;
import javafx.event.ActionEvent;

public class ClientController {
	@FXML
	private TextField ipField;

	@FXML
	private TextField portField;

	@FXML
	private TextField subscriberIdField;

	@FXML
	private TextField subscriberPhoneField;

	@FXML
	private TextField subscriberEmailField;

	@FXML
	private TextArea subscribersTextArea;

	private ServerCommunicator serverCommunicator;
	private boolean isLoggedIn = false;

	@FXML
	public void initialize() {
		serverCommunicator = new ServerCommunicator("localhost", 5555);
		showLoginPopup(); // Display the login dialog on startup
	}

	@FXML
	private void connectToServer() {
		try {
			String ip = ipField.getText().trim();
			int port = Integer.parseInt(portField.getText().trim());
			serverCommunicator = new ServerCommunicator(ip, port);
			subscribersTextArea.setText("Connected to server at " + ip + ":" + port);
		} catch (NumberFormatException e) {
			subscribersTextArea.setText("Invalid port number.");
		} catch (Exception e) {
			subscribersTextArea.setText("Error connecting to server: " + e.getMessage());
		}
	}

	@FXML
	private void fetchSubscribers() {
		try {
			String response = serverCommunicator.getSubscribers();
			subscribersTextArea.setText(response);
		} catch (Exception e) {
			subscribersTextArea.setText("Error fetching subscribers: " + e.getMessage());
		}
	}

	@FXML
	private void updateSubscriber() {
		try {
			int id = Integer.parseInt(subscriberIdField.getText().trim());
			String phone = subscriberPhoneField.getText().trim();
			String email = subscriberEmailField.getText().trim();

			if (phone.isEmpty() || email.isEmpty()) {
				subscribersTextArea.setText("Phone and email cannot be empty.");
				return;
			}

			String response = serverCommunicator.updateSubscriber(id, phone, email);
			subscribersTextArea.setText(response);
		} catch (NumberFormatException e) {
			subscribersTextArea.setText("Invalid subscriber ID.");
		} catch (Exception e) {
			subscribersTextArea.setText("Error updating subscriber: " + e.getMessage());
		}
	}


	@FXML
	public void showLoginPopup() {
	    if (isLoggedIn) {
	        return; // Prevent reopening the dialog if already logged in
	    }

	    // Create a custom dialog
	    Dialog<Void> dialog = new Dialog<>();
	    dialog.setTitle("Login");
	    dialog.setHeaderText("Enter your login details.");

	    TextField idField = new TextField();
	    idField.setPromptText("Client ID");

	    TextField nameField = new TextField();
	    nameField.setPromptText("Client Name");

	    Label errorLabel = new Label();
	    errorLabel.setStyle("-fx-text-fill: red;");

	    VBox content = new VBox(10, new Label("Client ID:"), idField, new Label("Client Name:"), nameField, errorLabel);
	    dialog.getDialogPane().setContent(content);

	    ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
	    dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

	    Button loginButton = (Button) dialog.getDialogPane().lookupButton(loginButtonType);
	    loginButton.addEventFilter(ActionEvent.ACTION, event -> {
	        String clientIdText = idField.getText().trim();
	        String clientName = nameField.getText().trim();

	        try {
	            int clientId = Integer.parseInt(clientIdText);
	            if (clientName.isEmpty()) {
	                errorLabel.setText("Name cannot be empty.");
	                event.consume(); // Prevent dialog from closing
	                return;
	            }

	            // Send login command to server
	            String response = serverCommunicator.sendRequest("LOGIN," + clientId + "," + clientName);
	            if ("Login successful".equals(response)) {
	                isLoggedIn = true; // Update login status
	                subscribersTextArea.setText("Logged in as: ID=" + clientId + ", Name=" + clientName);
	                dialog.close(); // Close the dialog explicitly
	            } else {
	                errorLabel.setText(response); // Display error from server
	                event.consume(); // Prevent dialog from closing
	            }
	        } catch (NumberFormatException e) {
	            errorLabel.setText("Invalid ID format.");
	            event.consume(); // Prevent dialog from closing
	        } catch (Exception e) {
	            errorLabel.setText("Error communicating with server: " + e.getMessage());
	            event.consume(); // Prevent dialog from closing
	        }
	    });

	    // Show the dialog and wait
	    dialog.showAndWait();
	}


}
