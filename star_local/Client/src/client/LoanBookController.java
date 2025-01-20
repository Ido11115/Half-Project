package client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class LoanBookController {

    @FXML
    private TextField subscriberIdField;
    @FXML
    private TextField bookIdField;
    @FXML
    private DatePicker loanDatePicker;
    @FXML
    private DatePicker returnDatePicker;
    @FXML
    private Label errorLabel;

    private ServerCommunicator serverCommunicator;

    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
    }

    @FXML
    private void handleLoanBook() {
        String subscriberIdText = subscriberIdField.getText().trim();
        String bookIdText = bookIdField.getText().trim();
        LocalDate loanDate = loanDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();

        if (subscriberIdText.isEmpty() || bookIdText.isEmpty() || loanDate == null || returnDate == null) {
            errorLabel.setText("All fields are required.");
            return;
        }

        int subscriberId, bookId;
        try {
            subscriberId = Integer.parseInt(subscriberIdText);
            bookId = Integer.parseInt(bookIdText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Subscriber ID and Book ID must be valid numbers.");
            return;
        }

        try {
            String command = "LOAN_BOOK," + subscriberId + "," + bookId + "," + loanDate + "," + returnDate;
            String response = serverCommunicator.sendRequest(command);

            if ("Loan successful".equals(response)) {
                System.out.println("Book loaned successfully.");
                errorLabel.setText("Book loaned successfully.");
                closeCurrentStage();
            } else {
                errorLabel.setText(response);
            }
        } catch (Exception e) {
            errorLabel.setText("Error communicating with server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeCurrentStage() {
        Stage currentStage = (Stage) subscriberIdField.getScene().getWindow();
        currentStage.close();
    }
}
