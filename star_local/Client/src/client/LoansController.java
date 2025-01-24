package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
        loanIdColumn.setCellValueFactory(cellData -> cellData.getValue().loanIdProperty().asObject());
        subscriberNameColumn.setCellValueFactory(cellData -> cellData.getValue().subscriberNameProperty());
        bookIdColumn.setCellValueFactory(cellData -> cellData.getValue().bookIdProperty().asObject());
        loanDateColumn.setCellValueFactory(cellData -> cellData.getValue().loanDateProperty());
        returnDateColumn.setCellValueFactory(cellData -> cellData.getValue().returnDateProperty());
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
    private List<Loan> parseLoans(String data) {
        List<Loan> loans = new ArrayList<>();
        try {
            String[] entries = data.split(";");
            for (String entry : entries) {
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
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing loans data: " + e.getMessage());
            e.printStackTrace();
        }
        return loans;
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
