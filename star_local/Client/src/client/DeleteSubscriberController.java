/**
 * Controller class for handling the deletion of a subscriber in the application.
 * Communicates with the server to process the deletion request.
 */
package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Default constructor for the DeleteSubscriberController class.
 * <p>
 * This constructor is implicitly provided by Java and does not require any specific initialization.
 * All required setup is handled by FXML loading and dependency injection.
 * </p>
 */
public class DeleteSubscriberController {

	/**
     * Default constructor for the DeleteSubscriberController class.
     * <p>
     * Initializes the controller without any specific parameters.
     * All FXML-related fields are initialized via the FXML loader.
     * </p>
     */
    public DeleteSubscriberController() {
        // No initialization required as fields are set via FXML loader
    }
	
    /**
     * Text field for entering the subscriber ID to be deleted.
     */
    @FXML
    private TextField subscriberIdField;

    /**
     * Object responsible for communicating with the server.
     */
    private ServerCommunicator serverCommunicator;

    /**
     * Sets the server communicator used for sending requests to the server.
     *
     * @param serverCommunicator The server communicator to be set.
     */
    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
    }

    /**
     * Handles the deletion of a subscriber when the delete button is clicked.
     *
     * Validates the subscriber ID, sends a delete request to the server, and displays feedback to the user.
     */
    @FXML
    private void handleDelete() {
        String subscriberIdText = subscriberIdField.getText().trim();

        if (subscriberIdText.isEmpty()) {
            showError("Subscriber ID cannot be empty.");
            return;
        }

        try {
            int subscriberId = Integer.parseInt(subscriberIdText);
            String response = serverCommunicator.sendRequest("DELETE_SUBSCRIBER," + subscriberId);

            if ("Delete successful".equals(response)) {
                showInfo("Subscriber deleted successfully.");
                closeCurrentStage();
            } else {
                showError(response);
            }
        } catch (NumberFormatException e) {
            showError("Subscriber ID must be a valid number.");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error communicating with server: " + e.getMessage());
        }
    }

    /**
     * Handles the cancellation of the operation when the cancel button is clicked.
     * Closes the current stage without making any changes.
     */
    @FXML
    private void handleCancel() {
        closeCurrentStage();
    }

    /**
     * Closes the current stage (window) of the application.
     */
    private void closeCurrentStage() {
        Stage stage = (Stage) subscriberIdField.getScene().getWindow();
        stage.close();
    }

    /**
     * Displays an error message in an alert dialog.
     *
     * @param message The error message to be displayed.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an informational message in an alert dialog.
     *
     * @param message The informational message to be displayed.
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
