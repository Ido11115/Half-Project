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
    private int SubscriberId; // Field to store the subscriber's ID

	private ServerCommunicator serverCommunicator;

	public void setServerCommunicator(ServerCommunicator serverCommunicator) {
		this.serverCommunicator = serverCommunicator;
        this.SubscriberId = SubscriberId; // Set the subscriber's ID

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
	        controller.setServerCommunicator(serverCommunicator, SubscriberId); // Pass ServerCommunicator and subscriberId

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
