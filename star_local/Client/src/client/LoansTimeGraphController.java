package client;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.io.IOException;

/**
 * The LoansTimeGraphController class is responsible for displaying a bar chart
 * that visualizes loan durations for various books and subscribers.
 * It fetches loan data from the server and populates the graph with the parsed data.
 */
public class LoansTimeGraphController {

    @FXML
    private BarChart<String, Number> loansBarChart;

    private ServerCommunicator serverCommunicator;

    /**
     * Sets the ServerCommunicator for this controller and loads loan data to populate the graph.
     *
     * @param serverCommunicator the ServerCommunicator used for server communication
     */
    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
        loadLoanData();
    }

    /**
     * Loads loan data from the server, parses it, and populates the bar chart.
     * Each bar represents the loan duration for a specific book borrowed by a subscriber.
     */
    private void loadLoanData() {
        try {
            // Fetch loan data from the server
            String response = serverCommunicator.sendRequest("GET_LOANS_TIME");
            System.out.println("Server Response: " + response); // Debug

            // Parse the response and populate the graph
            String[] loans = response.split(";");
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Loan Durations");

            for (String loan : loans) {
                String[] parts = loan.split(",");
                if (parts.length == 3) {
                    String subscriberName = parts[0];
                    String bookName = parts[1];
                    int loanDays = Integer.parseInt(parts[2]);
                    series.getData().add(new XYChart.Data<>(subscriberName + " (" + bookName + ")", loanDays));
                } else {
                    System.err.println("Invalid loan data: " + loan);
                }
            }

            loansBarChart.getData().add(series);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading loan data.");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showError("Error parsing loan data.");
        }
    }

    /**
     * Displays an error message in the console or through other error handling mechanisms.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        // Handle error display (e.g., alert dialog or console log)
        System.err.println(message);
    }
}
