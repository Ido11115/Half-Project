package client;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Node;

public class LibrarianMenuController {

	private ServerCommunicator serverCommunicator;
	
	@FXML
	private Button logoutButton;


	public void setServerCommunicator(ServerCommunicator serverCommunicator) {
		this.serverCommunicator = serverCommunicator;
	}

	@FXML
	private void handleRegisterSubscriber() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterSubscriber.fxml"));
			Parent root = loader.load();

			RegisterSubscriberController controller = loader.getController();
			controller.setServerCommunicator(serverCommunicator);

			Stage registerStage = new Stage();
			registerStage.setTitle("Register Subscriber");
			registerStage.setScene(new Scene(root));
			registerStage.show();
		} catch (Exception e) {
			e.printStackTrace();
			showError("Error loading the Register Subscriber screen.");
		}
	}

	@FXML
	private void handleLoanBook() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("LoanBook.fxml"));
			Parent root = loader.load();

			LoanBookController controller = loader.getController();
			controller.setServerCommunicator(serverCommunicator);

			Stage loanBookStage = new Stage();
			loanBookStage.setTitle("Loan Book to Subscriber");
			loanBookStage.setScene(new Scene(root));
			loanBookStage.show();
		} catch (Exception e) {
			e.printStackTrace();
			showError("Error loading the Loan Book screen.");
		}
	}

	@FXML
	private void handleReturnBook() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ReturnBook.fxml"));
			Parent root = loader.load();

			ReturnBookController controller = loader.getController();
			controller.setServerCommunicator(serverCommunicator);

			Stage returnBookStage = new Stage();
			returnBookStage.setTitle("Return Book");
			returnBookStage.setScene(new Scene(root));
			returnBookStage.show();
		} catch (Exception e) {
			e.printStackTrace();
			showError("Error loading the Return Book screen.");
		}

	}

	@FXML
	private void handleGetSubscriberInfo() {
		try {
			// Load the FXML
			FXMLLoader loader = new FXMLLoader(getClass().getResource("SubscriberInformation.fxml"));
			Parent root = loader.load();

			// Set the serverCommunicator in the controller
			SubscriberInformationController controller = loader.getController();
			controller.setServerCommunicator(serverCommunicator); // Pass the server communicator

			// Show the Subscriber Information screen
			Stage stage = new Stage();
			stage.setTitle("Subscriber Information");
			stage.setScene(new Scene(root));
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
			showError("Error loading Subscriber Information screen.");
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





	@FXML
	private void loadRegisterSubscriberScreen() {
		try {
			Stage registerStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterSubscriber.fxml"));
			Parent root = loader.load();

			RegisterSubscriberController registerController = loader.getController();
			registerController.setServerCommunicator(serverCommunicator);

			registerStage.setTitle("Register a New Subscriber");
			registerStage.setScene(new Scene(root));
			registerStage.show();
		} catch (Exception e) {
			e.printStackTrace();
			showError("Error loading register subscriber screen.");
		}
	}

	@FXML
	private void handleChangeSubscriberDetails() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("SubscriberDetailsChange.fxml"));
			Parent root = loader.load();

			// Pass the server communicator to the new controller
			SubscriberDetailsChangeController controller = loader.getController();
			controller.setServerCommunicator(serverCommunicator);

			Stage stage = new Stage();
			stage.setTitle("Change Subscriber Details");
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			showError("Failed to load the Subscriber Details Change screen.");
		}
	}

	@FXML
	private void handleGenerateSubscribersStatus() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("SubscribersStatusGraph.fxml"));
			Parent root = loader.load();

			// Pass the necessary data to the controller if required
			SubscribersStatusGraphController controller = loader.getController();
			controller.setServerCommunicator(serverCommunicator); // If server data is needed

			Stage stage = new Stage();
			stage.setTitle("Subscribers Status Graph");
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			showError("Error loading Subscribers Status Graph screen.");
		}
	}

	@FXML
	private void handleGenerateLoansTimeGraph() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("LoansTimeGraph.fxml"));
			Parent root = loader.load();

			// Pass the server communicator to the new controller
			LoansTimeGraphController controller = loader.getController();
			controller.setServerCommunicator(serverCommunicator);

			Stage stage = new Stage();
			stage.setTitle("Loans-Time Graph");
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			showError("Error loading Loans-Time Graph screen.");
		}
	}
	
	@FXML
	private void handleLoans() {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("Loans.fxml"));
	        Parent root = loader.load();

	        LoansController controller = loader.getController();
	        controller.setServerCommunicator(serverCommunicator); // Pass the ServerCommunicator

	        Stage stage = new Stage();
	        stage.setTitle("Loans");
	        stage.setScene(new Scene(root));
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	        showError("Error loading Loans screen: " + e.getMessage());
	    }
	}

	private void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setContentText(message);
		alert.showAndWait();
	}
}
