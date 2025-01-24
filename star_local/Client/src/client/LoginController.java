package client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * The LoginController class manages the login and registration functionalities for the application.
 * It allows users to log in as either a Subscriber or a Librarian and register new subscribers.
 */
public class LoginController {

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

    /**
     * Sets the ServerCommunicator instance for this controller.
     *
     * @param serverCommunicator the ServerCommunicator used for server communication
     */
    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
    }

    /**
     * Initializes the controller, setting up default configurations such as the toggle group
     * for the role selection radio buttons.
     */
    @FXML
    public void initialize() {
        ToggleGroup roleGroup = new ToggleGroup();
        subscriberRadioButton.setToggleGroup(roleGroup);
        librarianRadioButton.setToggleGroup(roleGroup);

        // Set default selection
        subscriberRadioButton.setSelected(true);
    }

    /**
     * Handles the login process. Validates user input, sends a login request to the server,
     * and navigates to the appropriate menu (Subscriber or Librarian) upon successful login.
     */
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
                    subscriberId = Integer.parseInt(parts[1]);
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

    /**
     * Loads the librarian menu upon successful login as a librarian.
     */
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

    /**
     * Handles the registration process for new subscribers. Validates user input,
     * sends a registration request to the server, and provides feedback based on the server's response.
     */
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

    /**
     * Loads the subscriber menu upon successful login as a subscriber. Also checks for any books due soon.
     *
     * @param subscriberId the ID of the logged-in subscriber
     */
    private void loadSubscriberMenu(int subscriberId) {
        try {
            String dueBooksResponse = serverCommunicator.sendRequest("GET_DUE_BOOKS," + subscriberId);

            if (!"No books due soon.".equals(dueBooksResponse)) {
                showDueBooksPopup(dueBooksResponse);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("SubscriberMenu.fxml"));
            Parent root = loader.load();

            SubscriberMenuController controller = loader.getController();
            controller.setServerCommunicator(serverCommunicator, subscriberId);

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

    /**
     * Displays a popup with information about books due soon.
     *
     * @param dueBooks the details of the books that are due soon
     */
    private void showDueBooksPopup(String dueBooks) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Books Due Soon");
        alert.setHeaderText("You have books due soon: check mail");
        alert.setContentText(dueBooks);
        alert.showAndWait();
    }

    /**
     * Closes the current stage of the application.
     */
    private void closeCurrentStage() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the logout process by closing the current stage.
     */
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
}
