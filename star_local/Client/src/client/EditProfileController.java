package client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * The EditProfileController class manages the UI and logic for editing a subscriber's profile.
 * It allows users to update their personal information, including name, last name, email,
 * phone number, username, and password.
 */
public class EditProfileController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private ServerCommunicator serverCommunicator;
    private int subscriberId;

    /**
     * Sets the ServerCommunicator and subscriber ID for this controller, then loads
     * the subscriber's data into the form.
     *
     * @param serverCommunicator the ServerCommunicator used for server communication
     * @param subscriberId the ID of the subscriber whose data is being edited
     */
    public void setServerCommunicator(ServerCommunicator serverCommunicator, int subscriberId) {
        this.serverCommunicator = serverCommunicator;
        this.subscriberId = subscriberId;
        loadSubscriberData();
    }

    /**
     * Loads the subscriber's data from the server and populates the fields in the form.
     */
    private void loadSubscriberData() {
        try {
            String response = serverCommunicator.sendRequest("GET_SUBSCRIBER_DATA," + subscriberId);
            String[] details = response.split(",");
            if (details.length == 7) {
                nameField.setText(details[1]);
                lastNameField.setText(details[2]);
                emailField.setText(details[3]);
                phoneField.setText(details[4]);
                usernameField.setText(details[5]);
                passwordField.setText(details[6]);
            } else {
                System.err.println("Unexpected subscriber data format: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles saving changes to the subscriber's profile.
     * Collects the data from the form, sends an update request to the server,
     * and provides feedback to the user based on the server's response.
     */
    @FXML
    private void handleSaveChanges() {
        try {
            String name = nameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            String command = String.format("UPDATE_SUBSCRIBER_DATA,%d,%s,%s,%s,%s,%s,%s",
                    subscriberId, name, lastName, email, phone, username, password);

            String response = serverCommunicator.sendRequest(command);

            if ("Update successful".equals(response)) {
                showInfo("Profile updated successfully.");
                handleCancel(); // Close the screen after saving
            } else {
                showError(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error saving changes: " + e.getMessage());
        }
    }

    /**
     * Handles the cancel action by closing the current stage.
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
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

    /**
     * Displays an informational message in an alert dialog.
     *
     * @param message the informational message to display
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
