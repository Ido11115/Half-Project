package client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * The RegisterSubscriberController class handles the registration of new subscribers.
 * It collects user input, validates the data, sends a registration request to the server,
 * and provides feedback based on the server's response.
 */
public class RegisterSubscriberController {

	/**
     * Default constructor for the RegisterSubscriberController class.
     * <p>
     * Initializes the controller without any specific parameters.
     * All required fields and components are set by the FXML loader.
     * </p>
     */
    public RegisterSubscriberController() {
        // No specific initialization required
    }
	
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

    /**
     * Sets the ServerCommunicator instance for this controller.
     *
     * @param serverCommunicator the ServerCommunicator used for server communication
     */
    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
    }

    /**
     * Initializes the controller, populating the status choice box with default values.
     */
    @FXML
    private void initialize() {
        if (statusChoiceBox != null) {
            statusChoiceBox.getItems().addAll("Active", "Inactive");
            statusChoiceBox.setValue("Active"); // Default selection
        } else {
            System.err.println("Error: statusChoiceBox is not initialized.");
        }
    }

    /**
     * Handles the registration process for a new subscriber. Validates input fields,
     * sends a registration request to the server, and displays feedback based on the response.
     */
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
