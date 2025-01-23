package client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    // Login fields
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
    
	@FXML
	private Button loginButton;

    private ServerCommunicator serverCommunicator;

    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
    }

    @FXML
    public void initialize() {
        ToggleGroup roleGroup = new ToggleGroup();
        subscriberRadioButton.setToggleGroup(roleGroup);
        librarianRadioButton.setToggleGroup(roleGroup);

        // Set default selection
        subscriberRadioButton.setSelected(true);
    }




    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role;

        if (subscriberRadioButton.isSelected()) {
            role = "Subscriber";
        } else if (librarianRadioButton.isSelected()) {
            role = "Librarian";
        } else {
            showError("Please select a role.");
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            showError("All fields must be filled.");
            return;
        }

        try {
            String response = serverCommunicator.sendRequest("LOGIN," + role + "," + username + "," + password);

            if (response.startsWith("Login successful")) {
                String[] parts = response.split(",");
                int subscriberId = -1;

                if (parts.length > 1) {
                    subscriberId = Integer.parseInt(parts[1]); // Extract subscriber ID
                }

                if ("Subscriber".equalsIgnoreCase(role)) {
                    loadSubscriberMenu(subscriberId);
                } else {
                    loadLibrarianMenu();
                }
            } else {
                showError(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error communicating with the server: " + e.getMessage());
        }
    }


    private void loadLibrarianMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LibrarianMenu.fxml"));
            Parent root = loader.load();

            LibrarianMenuController librarianController = loader.getController();
            librarianController.setServerCommunicator(serverCommunicator);

            Stage librarianStage = new Stage();
            librarianStage.setTitle("Librarian Menu");
            librarianStage.setScene(new Scene(root));
            librarianStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load librarian menu.");
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
            String command = "REGISTER_SUBSCRIBER," + subscriberId + "," + name + "," + email + "," + phone + "," + password;
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
    
    private void loadSubscriberMenu(int subscriberId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SubscriberMenu.fxml"));
            Parent root = loader.load();

            SubscriberMenuController controller = loader.getController();
            controller.setServerCommunicator(serverCommunicator, subscriberId); // Pass both arguments

            Stage stage = new Stage();
            stage.setTitle("Subscriber Menu");
            stage.setScene(new Scene(root));
            stage.show();

            closeCurrentStage();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Subscriber Menu: " + e.getMessage());
        }
    }

    private void closeCurrentStage() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }




    @FXML
    private void handleLogout() {
        try {
            System.out.println("Logging out...");
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
