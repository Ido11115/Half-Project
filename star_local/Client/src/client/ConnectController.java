package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ConnectController {

    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private Label connectErrorLabel;

    private Stage currentStage;
    private ServerCommunicator serverCommunicator;

    public void setStage(Stage stage) {
        this.currentStage = stage;
    }

    public ServerCommunicator getServerCommunicator() {
        return serverCommunicator;
    }

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

    @FXML
    private void handleCancel() {
        currentStage.close(); // Close the popup if canceled
    }

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
