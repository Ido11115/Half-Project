package client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MyProfileController {

    @FXML
    private Label idLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;

    private ServerCommunicator serverCommunicator;
    private int subscriberId;

    public void setServerCommunicator(ServerCommunicator serverCommunicator, int subscriberId) {
        this.serverCommunicator = serverCommunicator;
        this.subscriberId = subscriberId;
        loadProfileData();
    }

    private void loadProfileData() {
        try {
            String response = serverCommunicator.sendRequest("GET_SUBSCRIBER_DATA," + subscriberId);
            String[] details = response.split(",");
            if (details.length == 7) {
                idLabel.setText(details[0]);
                nameLabel.setText(details[1]);
                lastNameLabel.setText(details[2]);
                emailLabel.setText(details[3]);
                phoneLabel.setText(details[4]);
                usernameLabel.setText(details[5]);
                passwordLabel.setText(details[6]);
            } else {
                System.err.println("Unexpected subscriber data format: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditProfile.fxml"));
            Parent root = loader.load();

            EditProfileController controller = loader.getController();
            controller.setServerCommunicator(serverCommunicator, subscriberId); // Pass the data

            Stage stage = new Stage();
            stage.setTitle("Edit Profile");
            stage.setScene(new Scene(root));
            stage.show();

            handleClose(); // Close the "My Profile" screen
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    

    @FXML
    private void handleClose() {
        idLabel.getScene().getWindow().hide();
    }
}
