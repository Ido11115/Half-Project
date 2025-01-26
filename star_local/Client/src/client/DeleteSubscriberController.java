
package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class DeleteSubscriberController {

    @FXML
    private TextField subscriberIdField;

    private ServerCommunicator serverCommunicator;

    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
    }

    @FXML
    private void handleDelete() {
        String subscriberIdText = subscriberIdField.getText().trim();

        if (subscriberIdText.isEmpty()) {
            showError("Subscriber ID cannot be empty.");
            return;
        }

        try {
            int subscriberId = Integer.parseInt(subscriberIdText);
            String response = serverCommunicator.sendRequest("DELETE_SUBSCRIBER," + subscriberId);

            if ("Delete successful".equals(response)) {
                showInfo("Subscriber deleted successfully.");
                closeCurrentStage();
            } else {
                showError(response);
            }
        } catch (NumberFormatException e) {
            showError("Subscriber ID must be a valid number.");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error communicating with server: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeCurrentStage();
    }

    private void closeCurrentStage() {
        Stage stage = (Stage) subscriberIdField.getScene().getWindow();
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
