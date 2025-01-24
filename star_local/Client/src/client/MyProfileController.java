package client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * The MyProfileController class handles the display and management of a subscriber's profile information.
 * It allows the user to view their profile details and navigate to the Edit Profile screen to make changes.
 */
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

    /**
     * Sets the ServerCommunicator and subscriber ID for this controller, then loads the subscriber's profile data.
     *
     * @param serverCommunicator the ServerCommunicator used for server communication
     * @param subscriberId the ID of the subscriber whose profile is being displayed
     */
    public void setServerCommunicator(ServerCommunicator serverCommunicator, int subscriberId) {
        this.serverCommunicator = serverCommunicator;
        this.subscriberId = subscriberId;
        loadProfileData();
    }

    /**
     * Loads the subscriber's profile data from the server and populates the labels with the retrieved details.
     */
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

    /**
     * Handles the action of navigating to the Edit Profile screen.
     * Passes the current subscriber data to the EditProfileController and closes the My Profile screen.
     */
    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditProfile.fxml"));
            Parent root = loader.load();

            EditProfileController controller = loader.getController();
            controller.setServerCommunicator(serverCommunicator, subscriberId);

            Stage stage = new Stage();
            stage.setTitle("Edit Profile");
            stage.setScene(new Scene(root));
            stage.show();

            handleClose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the My Profile screen.
     */
    @FXML
    private void handleClose() {
        idLabel.getScene().getWindow().hide();
    }
}
