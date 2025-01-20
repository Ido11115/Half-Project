package client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ReturnBookController {

	@FXML
	private TextField bookIdField;
	@FXML
	private TextField subscriberIdField;
	@FXML
	private Label errorLabel;

	private ServerCommunicator serverCommunicator;

	public void setServerCommunicator(ServerCommunicator serverCommunicator) {
		this.serverCommunicator = serverCommunicator;
	}

	@FXML
	private void handleReturnBook() {
	    String bookIdText = bookIdField.getText().trim();
	    String subscriberIdText = subscriberIdField.getText().trim();

	    if (bookIdText.isEmpty() || subscriberIdText.isEmpty()) {
	        errorLabel.setText("Both fields are required.");
	        return;
	    }

	    int bookId, subscriberId;
	    try {
	        bookId = Integer.parseInt(bookIdText);
	        subscriberId = Integer.parseInt(subscriberIdText);
	    } catch (NumberFormatException e) {
	        errorLabel.setText("Book ID and Subscriber ID must be valid numbers.");
	        return;
	    }

	    try {
	        String command = "RETURN_BOOK," + subscriberId + "," + bookId;
	        String response = serverCommunicator.sendRequest(command);

	        if ("Return successful".equals(response)) {
	            System.out.println("Book returned successfully.");
	            errorLabel.setText("Book returned successfully.");
	            closeCurrentStage();
	        } else if (response.startsWith("No loan record found")) {
	            errorLabel.setText(response);
	        } else {
	            errorLabel.setText("Error: " + response);
	        }
	    } catch (Exception e) {
	        errorLabel.setText("Error communicating with server: " + e.getMessage());
	        e.printStackTrace();
	    }
	}


	private void closeCurrentStage() {
		Stage currentStage = (Stage) bookIdField.getScene().getWindow();
		currentStage.close();
	}
}
