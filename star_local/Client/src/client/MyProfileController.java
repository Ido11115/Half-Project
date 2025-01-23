package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MyProfileController {

    @FXML
    private Label idLabel; // Non-editable ID field
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

    // Set the ServerCommunicator and load subscriber data
    public void setServerCommunicator(ServerCommunicator serverCommunicator, int subscriberId) {
        this.serverCommunicator = serverCommunicator;
        this.subscriberId = subscriberId;
        loadProfileData();
    }

    // Load subscriber data from the server
    private void loadProfileData() {
        try {
            String response = serverCommunicator.sendRequest("GET_SUBSCRIBER_DATA," + subscriberId);
            String[] details = response.split(",");
            if (details.length >= 7) {
                idLabel.setText(details[0]);          // Subscriber ID
                nameField.setText(details[1]);       // Name
                lastNameField.setText(details[2]);   // Last Name
                emailField.setText(details[3]);      // Email
                phoneField.setText(details[4]);      // Phone
                usernameField.setText(details[5]);   // Username
                passwordField.setText(details[6]);   // Password
            } else {
                showError("Error fetching subscriber data. Please try again.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error communicating with the server: " + e.getMessage());
        }
    }

    // Save the updated subscriber data
    @FXML
    private void handleSaveChanges() {
        String name = nameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showError("All fields must be filled.");
            return;
        }

        try {
            String command = String.format(
                "UPDATE_SUBSCRIBER_DATA,%d,%s,%s,%s,%s,%s,%s",
                subscriberId, name, lastName, email, phone, username, password
            );
            String response = serverCommunicator.sendRequest(command);
            if ("Update successful".equals(response)) {
                showInfo("Your profile has been updated successfully.");
            } else {
                showError("Error updating your profile: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error communicating with the server: " + e.getMessage());
        }
    }

    // Close the My Profile screen
    @FXML
    private void handleClose() {
        Stage stage = (Stage) idLabel.getScene().getWindow();
        stage.close();
    }

    // Show error alert
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Show information alert
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
