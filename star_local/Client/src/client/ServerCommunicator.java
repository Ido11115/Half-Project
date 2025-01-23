package client;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServerCommunicator {
	private String serverAddress;
	private int serverPort;
	private Socket socket;
	private PrintWriter outputWriter;
	private BufferedReader inputReader;

	public ServerCommunicator(String serverAddress, int serverPort) throws IOException {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		connect();
	}

	private void connect() throws IOException {
		System.out.println("Connecting to server at " + serverAddress + ":" + serverPort);
		try {
			socket = new Socket(serverAddress, serverPort);
			outputWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			System.out.println("Connection successful!");
		} catch (IOException e) {
			System.err.println("Failed to connect to server: " + e.getMessage());
			throw e;
		}
	}

	public String sendRequest(String command) throws IOException {
		if (socket == null || socket.isClosed()) {
			throw new IOException("Not connected to server.");
		}

		// Send command to the server
		System.out.println("Sending command to server: " + command);
		outputWriter.println(command);

		// Read server's response
		String response = inputReader.readLine();
		if (response == null) {
			throw new IOException("Server closed the connection unexpectedly.");
		}
		System.out.println("Response from server: " + response);
		return response;
	}

	public String getSubscriptionHistory(int subscriberId) throws IOException {
	    String response = sendRequest("GET_SUBSCRIPTION_HISTORY," + subscriberId);
	    System.out.println("Received Subscription History for Subscriber ID " + subscriberId + ": " + response); // Debug
	    return response;
	}


	public String getSubscriberStatus(int subscriberId) throws IOException {
	    String response = sendRequest("GET_SUBSCRIBER_STATUS_COUNTS," + subscriberId);
	    System.out.println("Received Status for Subscriber ID " + subscriberId + ": " + response); // Debug log
	    return response;
	}
	
	public String getSubscriberStatusCountsByMonth() throws IOException {
	    String command = "GET_SUBSCRIBER_STATUS_COUNTS_BY_MONTH";
	    outputWriter.println(command);
	    String response = inputReader.readLine(); // Read the response
	    System.out.println("Received Status Counts by Month: " + response); // Debug
	    return response;
	}

	




	public void saveSubscriptionHistory(String subscriberId, List<String> actions) throws IOException {
		StringBuilder command = new StringBuilder("SAVE_SUBSCRIPTION_HISTORY," + subscriberId);
		for (String action : actions) {
			command.append(",").append(action);
		}
		sendRequest(command.toString());
	}

	public void updateSubscriberStatus(String subscriberId, String newStatus) throws IOException {
		sendRequest("UPDATE_SUBSCRIBER_STATUS," + subscriberId + "," + newStatus);
	}

	public void close() throws IOException {
		if (socket != null && !socket.isClosed()) {
			socket.close();
			System.out.println("Connection closed.");
		}
	}
}
