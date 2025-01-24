/**
 * Controller class for managing the Subscribers Status Graph.
 * This class is responsible for displaying subscriber status data in a bar chart.
 */
package client;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SubscribersStatusGraphController {

    /**
     * BarChart for displaying subscriber statuses by month.
     */
    @FXML
    private BarChart<String, Number> statusBarChart;

    /**
     * Communicator for interacting with the server.
     */
    private ServerCommunicator serverCommunicator;

    /**
     * Sets the ServerCommunicator instance and loads the subscriber status data.
     *
     * @param serverCommunicator the ServerCommunicator instance to be used for fetching data
     */
    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
        loadSubscriberStatusData();
    }

    /**
     * Loads subscriber status data from the server and populates the bar chart.
     * The data is fetched in the format "status,month,count" and organized into chart series.
     */
    private void loadSubscriberStatusData() {
        try {
            // Fetch subscriber status counts by month
            String response = serverCommunicator.getSubscriberStatusCountsByMonth();
            System.out.println("Server Response: " + response); // Debug

            // Parse the response
            String[] entries = response.split(";");
            Map<String, XYChart.Series<String, Number>> seriesMap = new HashMap<>();

            for (String entry : entries) {
                String[] parts = entry.split(",");
                if (parts.length == 3) {
                    String status = parts[0];
                    String month = parts[1];
                    int count = Integer.parseInt(parts[2]);

                    // Create or update the series for the status
                    seriesMap.putIfAbsent(status, new XYChart.Series<>());
                    XYChart.Series<String, Number> series = seriesMap.get(status);
                    series.setName(status);
                    series.getData().add(new XYChart.Data<>(month, count));
                } else {
                    System.err.println("Invalid data entry: " + entry);
                }
            }

            // Add all series to the chart
            statusBarChart.getData().clear();
            statusBarChart.getData().addAll(seriesMap.values());
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading subscriber status data by month.");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showError("Error parsing subscriber status data by month.");
        }
    }

    /**
     * Displays an error message in the console.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        // Error display logic
        System.err.println(message);
    }
}
