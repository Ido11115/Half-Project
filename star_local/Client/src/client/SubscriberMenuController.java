/**
 * Controller class for managing the subscriber menu in the application.
 * Provides functionality for navigating to different screens such as Search Book, My Profile, and Logout.
 */
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

/**
 * Controller class for managing the subscriber menu in the application.
 * Provides functionality for navigating to different screens, such as Search Book,
 * My Profile, and Logout.
 * <p>
 * This controller interacts with the server using a {@code ServerCommunicator} instance
 * and maintains the context of the currently logged-in subscriber.
 * </p>
 */
public class SubscriberMenuController {
	
	/**
     * Default constructor for the SubscriberMenuController class.
     * <p>
     * Initializes the controller without any specific parameters.
     * All required fields and components are set by the FXML loader.
     * </p>
     */
    public SubscriberMenuController() {
        // No specific initialization required
    }
	
	/**
	 * Button for logging out the current subscriber.
	 */
	@FXML
	private Button logoutButton;

	/**
	 * Communicator for interacting with the server.
	 */
	private ServerCommunicator serverCommunicator;

	/**
	 * ID of the currently logged-in subscriber.
	 */
	private int currentSubscriberId;

	/**
	 * Sets the ServerCommunicator instance and the current subscriber's ID.
	 *
	 * @param serverCommunicator the ServerCommunicator instance
	 * @param subscriberId the ID of the current subscriber
	 */
	public void setServerCommunicator(ServerCommunicator serverCommunicator, int subscriberId) {
	    this.serverCommunicator = serverCommunicator; // Assign the server communicator
	    this.currentSubscriberId = subscriberId; // Assign the subscriber ID
	}

	/**
	 * Handles the navigation to the Search Book screen.
	 * Loads the SearchBook.fxml and passes the ServerCommunicator instance to the controller.
	 */
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

	/**
	 * Handles the navigation to the My Profile screen.
	 * Loads the MyProfile.fxml and passes the ServerCommunicator instance and subscriber ID to the controller.
	 */
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

	/**
	 * Handles the logout process by closing the current stage and navigating to the login screen.
	 *
	 * @param event the ActionEvent triggered by the logout button
	 */
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
	
	/**
	 * Displays an error message in the console.
	 *
	 * @param message the error message to display
	 */
	private void showError(String message) {
		System.err.println(message);
	}
}
