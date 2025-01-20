package client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterSubscriberController {

    @FXML
    private TextField subscriberIdField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ChoiceBox<String> statusChoiceBox;
    @FXML
    private Label errorLabel;

    private ServerCommunicator serverCommunicator;

    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
    }

    @FXML
    private void initialize() {
        // Populate the ChoiceBox programmatically
        if (statusChoiceBox != null) {
            statusChoiceBox.getItems().addAll("Active", "Inactive");
            statusChoiceBox.setValue("Active"); // Default selection
        } else {
            System.err.println("Error: statusChoiceBox is not initialized.");
        }
    }

    @FXML
    private void handleRegister() {
        String idText = subscriberIdField.getText().trim();
        String name = nameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String userName = userNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String status = statusChoiceBox.getValue();

        if (idText.isEmpty() || name.isEmpty() || lastName.isEmpty() || userName.isEmpty() ||
            phone.isEmpty() || email.isEmpty() || password.isEmpty() || status == null) {
            errorLabel.setText("All fields are required.");
            return;
        }

        int subscriberId;
        try {
            subscriberId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Subscriber ID must be a valid number.");
            return;
        }

        try {
            String command = "REGISTER_SUBSCRIBER," + subscriberId + "," + name + "," + lastName + "," +
                             userName + "," + phone + "," + email + "," + password + "," + status;
            String response = serverCommunicator.sendRequest(command);

            if ("Registration successful".equals(response)) {
                System.out.println("Subscriber registered successfully: " + name + " " + lastName);
                errorLabel.setText("");

                // Close the current stage (Register Subscriber screen)
                Stage currentStage = (Stage) subscriberIdField.getScene().getWindow();
                currentStage.close();

                // Optionally, print a confirmation message
                System.out.println("Returning to the Librarian Menu.");
            } else {
                errorLabel.setText(response);
            }
        } catch (Exception e) {
            errorLabel.setText("Error communicating with server: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
