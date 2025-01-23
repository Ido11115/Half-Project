package client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SubscriberMenuController {
	
	@FXML
	private Button logoutButton;

	private ServerCommunicator serverCommunicator;
	private int currentSubscriberId;

	public void setServerCommunicator(ServerCommunicator serverCommunicator, int subscriberId) {
	    this.serverCommunicator = serverCommunicator; // Assign the server communicator
	    this.currentSubscriberId = subscriberId; // Assign the subscriber ID
	}


	@FXML
	private void handleSearchBook() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("SearchBook.fxml"));
			Parent root = loader.load();

			SearchBookController controller = loader.getController();
			controller.setServerCommunicator(serverCommunicator);

			Stage stage = new Stage();
			stage.setTitle("Search Book");
			stage.setScene(new Scene(root));
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
			showError("Error loading Search Book screen: " + e.getMessage());
		}
	}



	@FXML
	private void handleMyProfile() {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("MyProfile.fxml"));
	        Parent root = loader.load();

	        MyProfileController controller = loader.getController();
	        controller.setServerCommunicator(serverCommunicator, currentSubscriberId); // Pass the correct subscriber ID

	        Stage stage = new Stage();
	        stage.setTitle("My Profile");
	        stage.setScene(new Scene(root));
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	        showError("Error loading My Profile screen: " + e.getMessage());
	    }
	}




	@FXML
    private void handleLogout(ActionEvent event) {
        try {
            System.out.println("Logging out...");

            // Get the current stage from the event source
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Close the current stage
            currentStage.close();

            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error during logout: " + e.getMessage());
        }
    }
	



	private void showError(String message) {
		System.err.println(message);
	}
}
