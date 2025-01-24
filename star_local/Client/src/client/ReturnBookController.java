package client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * The ReturnBookController class handles the functionality for returning borrowed books.
 * It collects input from the user, validates the data, sends a return request to the server,
 * and provides feedback based on the server's response.
 */
public class ReturnBookController {

    @FXML
    private TextField bookIdField;

    @FXML
    private TextField subscriberIdField;

    @FXML
    private Label errorLabel;

    private ServerCommunicator serverCommunicator;

    /**
     * Sets the ServerCommunicator for this controller.
     *
     * @param serverCommunicator the ServerCommunicator used for server communication
     */
    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
    }

    /**
     * Handles the process of returning a borrowed book. Validates input fields,
     * sends a return request to the server, and provides feedback to the user.
     */
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

    /**
     * Closes the current stage of the application.
     */
    private void closeCurrentStage() {
        Stage currentStage = (Stage) bookIdField.getScene().getWindow();
        currentStage.close();
    }
}
