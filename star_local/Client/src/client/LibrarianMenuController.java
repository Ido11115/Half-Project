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

/**
 * The LibrarianMenuController class manages the librarian's main menu in the application.
 * It provides functionality to navigate to various librarian-specific screens,
 * such as registering subscribers, loaning books, and generating reports.
 */
public class LibrarianMenuController {

    private ServerCommunicator serverCommunicator;

    @FXML
    private Button logoutButton;
    
    @FXML
    private Button loansButton;

    /**
     * Sets the ServerCommunicator instance for this controller.
     *
     * @param serverCommunicator the ServerCommunicator used for server communication
     */
    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
    }

    /**
     * Handles the action to navigate to the Register Subscriber screen.
     */
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

    /**
     * Handles the action to navigate to the Loan Book screen.
     */
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

    /**
     * Handles the action to navigate to the Return Book screen.
     */
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

    /**
     * Handles the action to navigate to the Subscriber Information screen.
     */
    @FXML
    private void handleGetSubscriberInfo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SubscriberInformation.fxml"));
            Parent root = loader.load();

            SubscriberInformationController controller = loader.getController();
            controller.setServerCommunicator(serverCommunicator);

            Stage stage = new Stage();
            stage.setTitle("Subscriber Information");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading Subscriber Information screen.");
        }
    }

    /**
     * Handles the logout action, closes the current stage, and returns to the login screen.
     *
     * @param event the ActionEvent triggered by the logout button
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            System.out.println("Logging out...");

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

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
     * Handles the action to navigate to the Change Subscriber Details screen.
     */
    @FXML
    private void handleChangeSubscriberDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SubscriberDetailsChange.fxml"));
            Parent root = loader.load();

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

    /**
     * Handles the action to navigate to the Subscribers Status Graph screen.
     */
    @FXML
    private void handleGenerateSubscribersStatus() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SubscribersStatusGraph.fxml"));
            Parent root = loader.load();

            SubscribersStatusGraphController controller = loader.getController();
            controller.setServerCommunicator(serverCommunicator);

            Stage stage = new Stage();
            stage.setTitle("Subscribers Status Graph");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Subscribers Status Graph screen.");
        }
    }

    /**
     * Handles the action to navigate to the Loans-Time Graph screen.
     */
    @FXML
    private void handleGenerateLoansTimeGraph() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoansTimeGraph.fxml"));
            Parent root = loader.load();

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

    /**
     * Handles the action to navigate to the Loans screen.
     */
    @FXML
    private void handleLoans() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Loans.fxml"));
            Parent root = loader.load();

            LoansController loansController = loader.getController();
            loansController.setServerCommunicator(serverCommunicator); // Pass the serverCommunicator

            Stage stage = new Stage();
            stage.setTitle("Loans");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Loans screen: " + e.getMessage());
        }
    }


    /**
     * Displays an error message in an alert dialog.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
