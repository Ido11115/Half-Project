package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * The ConnectController class manages the connection setup between the client and the server.
 * It handles user input for the server's IP address and port, establishes the connection,
 * and transitions to other application screens based on the connection status.
 */
public class ConnectController {

    @FXML
    private TextField ipField;

    @FXML
    private TextField portField;

    @FXML
    private Label connectErrorLabel;

    private Stage currentStage;
    private ServerCommunicator serverCommunicator;

    /**
     * Sets the stage for the controller.
     *
     * @param stage the current stage of the application
     */
    public void setStage(Stage stage) {
        this.currentStage = stage;
    }

    /**
     * Gets the ServerCommunicator instance.
     *
     * @return the ServerCommunicator used for communicating with the server
     */
    public ServerCommunicator getServerCommunicator() {
        return serverCommunicator;
    }

    /**
     * Handles the server connection process.
     * Validates the IP address and port, establishes a connection to the server,
     * and transitions to the login screen if successful.
     */
    @FXML
    private void handleServerConnect() {
        String ip = ipField.getText().trim();
        String portText = portField.getText().trim();

        try {
            int port = Integer.parseInt(portText);
            serverCommunicator = new ServerCommunicator(ip, port);

            // Test the connection
            String response = serverCommunicator.sendRequest("TEST_CONNECTION");
            if ("Connection successful".equals(response)) {
                connectErrorLabel.setText("");
                System.out.println("Server connection successful.");
                currentStage.close(); // Close the connect popup
                showLoginScreen(); // Show the login screen
            } else {
                connectErrorLabel.setText("Failed to connect: " + response);
            }
        } catch (NumberFormatException e) {
            connectErrorLabel.setText("Invalid port number.");
        } catch (Exception e) {
            connectErrorLabel.setText("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the cancel button action by closing the current stage.
     */
    @FXML
    private void handleCancel() {
        currentStage.close(); // Close the popup if canceled
    }

    /**
     * Displays the login screen after a successful connection to the server.
     */
    private void showLoginScreen() {
        try {
            Stage loginStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            loginController.setServerCommunicator(serverCommunicator);

            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();

            loginStage.setOnCloseRequest(event -> showMainScreen());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the main screen of the application after closing the login screen.
     */
    private void showMainScreen() {
        try {
            Stage mainStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientGUI.fxml"));
            Parent root = loader.load();

            mainStage.setTitle("Client Main Screen");
            mainStage.setScene(new Scene(root));
            mainStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
