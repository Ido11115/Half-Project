package client;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientController {

    // Login fields
    @FXML
    private ToggleGroup roleGroup;
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

    // Registration fields
    @FXML
    private TextField subscriberIdField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private Label registerErrorLabel;

    // Server connection fields
    @FXML
    private VBox serverConnectPopup;
    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private Label connectErrorLabel;

    private ServerCommunicator serverCommunicator;
    private boolean isLoggedIn = false;

    @FXML
    public void initialize() {
        showServerConnectPopup(); // Show the server connection dialog at startup
    }

    @FXML
    private void handleLogin() {
        if (isLoggedIn) return;

        String role = subscriberRadioButton.isSelected() ? "Subscriber" : "Librarian";
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Both fields are required.");
            return;
        }

        Task<Void> loginTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    String command = "LOGIN," + role + "," + username + "," + password;
                    String response = serverCommunicator.sendRequest(command);

                    javafx.application.Platform.runLater(() -> {
                        if ("Login successful".equals(response)) {
                            isLoggedIn = true;
                            errorLabel.setText("");
                            System.out.println("Logged in successfully as " + username);

                            // Close the login screen
                            closeCurrentStage();
                        } else {
                            errorLabel.setText(response);
                        }
                    });
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        errorLabel.setText("Error communicating with server: " + e.getMessage());
                    });
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(loginTask).start();
    }



    @FXML
    private void handleRegister() {
        String idText = subscriberIdField.getText().trim();
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String password = registerPasswordField.getText().trim();

        if (idText.isEmpty() || name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
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

        Task<Void> registerTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    String command = "REGISTER_SUBSCRIBER," + subscriberId + "," + name + "," + phone + "," + email + "," + password;
                    String response = serverCommunicator.sendRequest(command);

                    javafx.application.Platform.runLater(() -> {
                        if ("Registration successful".equals(response)) {
                            registerErrorLabel.setText("");
                            System.out.println("Registered successfully with ID " + subscriberId);
                        } else {
                            registerErrorLabel.setText(response);
                        }
                    });
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        registerErrorLabel.setText("Error communicating with server: " + e.getMessage());
                    });
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(registerTask).start();
    }

    @FXML
    public void showServerConnectPopup() {
        serverConnectPopup.setVisible(true);
    }

    @FXML
    public void hideServerConnectPopup() {
        serverConnectPopup.setVisible(false);
    }

    @FXML
    public void handleServerConnect() {
        String ip = ipField.getText().trim();
        String portText = portField.getText().trim();

        try {
            int port = Integer.parseInt(portText);
            serverCommunicator = new ServerCommunicator(ip, port);

            String response = serverCommunicator.sendRequest("TEST_CONNECTION");
            if ("Connection successful".equals(response)) {
                connectErrorLabel.setText("");
                hideServerConnectPopup();
            } else {
                connectErrorLabel.setText("Failed to connect: " + response);
            }
        } catch (NumberFormatException e) {
            connectErrorLabel.setText("Invalid port number.");
        } catch (Exception e) {
            connectErrorLabel.setText("Error connecting to server: " + e.getMessage());
        }
    }
    private void closeCurrentStage() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

}
