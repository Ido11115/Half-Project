package client;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SubscribersStatusGraphController {

    @FXML
    private BarChart<String, Number> statusBarChart;

    private ServerCommunicator serverCommunicator;

    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
        loadSubscriberStatusData();
    }

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



    private void showError(String message) {
        // Error display logic
        System.err.println(message);
    }
}
