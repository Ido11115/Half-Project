package client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class LibrarianMenuController {

	private ServerCommunicator serverCommunicator;

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
	private void handleLogout() {
		System.out.println("Logging out...");
		Stage currentStage = (Stage) ((javafx.scene.Node) null).getScene().getWindow();
		currentStage.close();
		// Optionally, return to the login screen
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


	private void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setContentText(message);
		alert.showAndWait();
	}
}
