package client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    // Login fields
    private ToggleGroup roleGroup; // Define the ToggleGroup
    @FXML
    private RadioButton subscriberRadioButton;
    @FXML
    private RadioButton librarianRadioButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    // Register fields
    @FXML
    private TextField subscriberIdField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private Label registerErrorLabel;

    private ServerCommunicator serverCommunicator;

    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
    }

    @FXML
    public void initialize() {
        // Initialize the ToggleGroup and assign it to the RadioButtons
        roleGroup = new ToggleGroup();
        subscriberRadioButton.setToggleGroup(roleGroup);
        librarianRadioButton.setToggleGroup(roleGroup);

        // Set default selection
        subscriberRadioButton.setSelected(true);
    }

    @FXML
    private void handleLogin() {
        // Get the selected role
        String role = subscriberRadioButton.isSelected() ? "Subscriber" : "Librarian";
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Both fields are required.");
            return;
        }

        try {
            String command = "LOGIN," + role + "," + username + "," + password;
            String response = serverCommunicator.sendRequest(command);

            if ("Login successful".equals(response)) {
                System.out.println("Logged in successfully as " + username);
                errorLabel.setText("");

                // Close the login screen
                Stage currentStage = (Stage) usernameField.getScene().getWindow();
                currentStage.close();
            } else {
                errorLabel.setText(response);
            }
        } catch (Exception e) {
            errorLabel.setText("Error communicating with server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        String idText = subscriberIdField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = registerPasswordField.getText().trim();

        if (idText.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            registerErrorLabel.setText("All fields are required.");
            return;
        }

        int subscriberId;
        try {
            subscriberId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            registerErrorLabel.setText("Subscriber ID must be a valid number.");
            return;
        }

        try {
            String command = "REGISTER_SUBSCRIBER," + subscriberId + "," + name + "," + phone + "," + email + "," + password;
            String response = serverCommunicator.sendRequest(command);

            if ("Registration successful".equals(response)) {
                System.out.println("Subscriber registered successfully with ID " + subscriberId);
                registerErrorLabel.setText("");
            } else {
                registerErrorLabel.setText(response);
            }
        } catch (Exception e) {
            registerErrorLabel.setText("Error communicating with server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
        Stage currentStage = (Stage) ((javafx.scene.Node) null).getScene().getWindow();
        currentStage.close();
    }
}
