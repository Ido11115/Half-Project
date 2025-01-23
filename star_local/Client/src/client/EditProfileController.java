package client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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

    public void setServerCommunicator(ServerCommunicator serverCommunicator, int subscriberId) {
        this.serverCommunicator = serverCommunicator;
        this.subscriberId = subscriberId;
        loadSubscriberData();
    }

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

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
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
