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

    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
        loadLoansData();
    }

    @FXML
    private void initialize() {
        loanIdColumn.setCellValueFactory(cellData -> cellData.getValue().loanIdProperty().asObject());
        subscriberNameColumn.setCellValueFactory(cellData -> cellData.getValue().subscriberNameProperty());
        bookIdColumn.setCellValueFactory(cellData -> cellData.getValue().bookIdProperty().asObject());
        loanDateColumn.setCellValueFactory(cellData -> cellData.getValue().loanDateProperty());
        returnDateColumn.setCellValueFactory(cellData -> cellData.getValue().returnDateProperty());
    }

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
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An Error Occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
