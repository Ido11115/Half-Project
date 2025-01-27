package client;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The LoansController class is responsible for managing and displaying loan records in the application.
 * It interacts with a server to fetch loan data and populates a TableView with the details of each loan.
 */
public class LoansController {

    @FXML
    private TableView<Loan> loansTable;

    @FXML
    private TableColumn<Loan, Integer> loanIdColumn;

    @FXML
    private TableColumn<Loan, String> subscriberNameColumn;

    @FXML
    private TableColumn<Loan, Integer> bookIdColumn;

    @FXML
    private TableColumn<Loan, String> loanDateColumn;

    @FXML
    private TableColumn<Loan, String> returnDateColumn;

    private ServerCommunicator serverCommunicator;

    private ObservableList<Loan> loansList = FXCollections.observableArrayList();

    /**
     * Sets the ServerCommunicator for this controller and loads the loan data from the server.
     *
     * @param serverCommunicator the ServerCommunicator used for server communication
     */
    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
        loadLoansData();

    }

    /**
     * Initializes the TableView by binding the table columns to the Loan object properties.
     */
    @FXML
    private void initialize() {
        loanIdColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getLoanId()));
        subscriberNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getSubscriberName()));
        bookIdColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getBookId()));
        loanDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getLoanDate()));
        returnDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getReturnDate()));

        loansTable.setItems(loansList); // Bind loanList to TableView
    }






    /**
     * Loads loan data from the server and populates the TableView.
     * If no loans are found or an error occurs, an appropriate error message is displayed.
     */
    @FXML
    private void loadLoansData() {
        try {
            String response = serverCommunicator.sendRequest("GET_LOANS");
            if (response.equals("No loans found.")) {
                showError("No loans found in the database.");
                return;
            }

            List<Loan> loans = parseLoans(response);
            loansList.setAll(loans);
            loansTable.setItems(loansList);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading loans data: " + e.getMessage());
        }
    }

    /**
     * Parses the raw data string received from the server into a list of Loan objects.
     *
     * @param data the raw data string containing loan details
     * @return a list of Loan objects parsed from the data
     */
    private List<Loan> parseLoans(String response) {
        List<Loan> loans = new ArrayList<>();
        try {
            String[] entries = response.split(";");
            for (String entry : entries) {
                System.out.println("Parsing entry: " + entry); // Debug log

                String[] details = entry.split(",");
                if (details.length == 6) {
                    loans.add(new Loan(
                        Integer.parseInt(details[0]), // Loan ID
                        Integer.parseInt(details[1]), // Subscriber ID
                        Integer.parseInt(details[2]), // Book ID
                        details[3], // Loan Date
                        details[4], // Return Date
                        details[5]  // Subscriber Name
                    ));
                } else {
                    System.err.println("Invalid loan entry: " + entry); // Debug invalid entries
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error parsing loans: " + e.getMessage());
        }
        return loans;
    }



    /**
     * Handles the prolongation of a loan for a selected book.
     * Prompts the user for a new return date and updates the loan if valid.
     */
    @FXML
    private void handleProlongLoan() {
        Loan selectedLoan = loansTable.getSelectionModel().getSelectedItem();
        if (selectedLoan == null) {
            showError("No loan selected. Please select a loan to prolong.");
            return;
        }

        try {
            // Check if the book is reserved
            if (serverCommunicator.isBookReserved(selectedLoan.getBookId())) {
                showError("This book is reserved by another subscriber. Prolongation is not allowed.");
                return;
            }

            // Prompt the librarian for a new return date
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Prolong Loan");
            dialog.setHeaderText("Prolong the loan for Book ID: " + selectedLoan.getBookId());
            dialog.setContentText("Enter new return date (YYYY-MM-DD):");

            dialog.showAndWait().ifPresent(newReturnDate -> {
                try {
                    // Prolong the loan
                    String response = serverCommunicator.prolongLoan(selectedLoan.getLoanId(), newReturnDate);
                    if ("Prolongation successful".equals(response)) {
                        showInfo("Loan successfully prolonged.");
                        loadLoans(); // Refresh loans table
                    } else {
                        showError(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showError("Error communicating with the server: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error communicating with the server: " + e.getMessage());
        }
    }

    /**
     * Displays an informational message in an alert dialog.
     *
     * @param message The informational message to display.
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null); // Optional: Hide the header
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Loads the list of loans from the server and populates the loans table.
     * Displays an error message if the loans cannot be loaded.
     */
    private void loadLoans() {
        if (serverCommunicator == null) {
            showError("Server communicator is not initialized.");
            return;
        }

        try {
            String response = serverCommunicator.sendRequest("GET_LOANS");
            System.out.println("Server Response in Client: " + response); // Debug log

            if (response.isEmpty() || response.equalsIgnoreCase("No loans found.")) {
                showError("No loans found.");
                return;
            }

            List<Loan> loans = parseLoans(response);
            System.out.println("Parsed Loans: " + loans); // Debug log

            loansList.setAll(loans); // Populate TableView
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading loans: " + e.getMessage());
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
        alert.setHeaderText("An Error Occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
