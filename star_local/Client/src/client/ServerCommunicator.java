package client;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * The ServerCommunicator class is responsible for handling communication
 * between the client and the server. It provides methods to send requests to
 * the server and receive responses, as well as specialized operations for
 * managing subscriber and subscription data.
 */
public class ServerCommunicator {

	private String serverAddress;
	private int serverPort;
	private Socket socket;
	private PrintWriter outputWriter;
	private BufferedReader inputReader;

	/**
	 * Constructs a new ServerCommunicator and establishes a connection to the
	 * server.
	 *
	 * @param serverAddress the server's address
	 * @param serverPort    the server's port
	 * @throws IOException if an I/O error occurs while connecting to the server
	 */
	public ServerCommunicator(String serverAddress, int serverPort) throws IOException {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		connect();
	}

	/**
	 * Establishes a connection to the server and initializes the input and output
	 * streams.
	 *
	 * @throws IOException if an error occurs while connecting to the server
	 */
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

	/**
	 * Sends a command to the server and retrieves the response.
	 *
	 * @param command the command to send
	 * @return the server's response
	 * @throws IOException if an error occurs during communication with the server
	 */
	public String sendRequest(String command) throws IOException {
		if (socket == null || socket.isClosed()) {
			throw new IOException("Not connected to server.");
		}

		System.out.println("Sending command to server: " + command);
		outputWriter.println(command);

		String response = inputReader.readLine();
		if (response == null) {
			throw new IOException("Server closed the connection unexpectedly.");
		}
		System.out.println("Response from server: " + response);
		return response;
	}

	/**
	 * Retrieves the subscription history for a specific subscriber.
	 *
	 * @param subscriberId the subscriber's ID
	 * @return the subscription history as a string
	 * @throws IOException if an error occurs while retrieving the subscription
	 *                     history
	 */
	public String getSubscriptionHistory(int subscriberId) throws IOException {
		String response = sendRequest("GET_SUBSCRIPTION_HISTORY," + subscriberId);
		System.out.println("Received Subscription History for Subscriber ID " + subscriberId + ": " + response);
		return response;
	}

	/**
	 * Retrieves the status of a specific subscriber.
	 *
	 * @param subscriberId the subscriber's ID
	 * @return the subscriber's status as a string
	 * @throws IOException if an error occurs while retrieving the subscriber's
	 *                     status
	 */
	public String getSubscriberStatus(int subscriberId) throws IOException {
		String response = sendRequest("GET_SUBSCRIBER_STATUS_COUNTS," + subscriberId);
		System.out.println("Received Status for Subscriber ID " + subscriberId + ": " + response);
		return response;
	}

	/**
	 * Retrieves subscriber status counts grouped by month.
	 *
	 * @return the subscriber status counts as a string
	 * @throws IOException if an error occurs while retrieving the data
	 */
	public String getSubscriberStatusCountsByMonth() throws IOException {
		String command = "GET_SUBSCRIBER_STATUS_COUNTS_BY_MONTH";
		outputWriter.println(command);
		String response = inputReader.readLine();
		System.out.println("Received Status Counts by Month: " + response);
		return response;
	}

	/**
	 * Saves the subscription history for a specific subscriber.
	 *
	 * @param subscriberId the subscriber's ID
	 * @param actions      the list of subscription actions to save
	 * @throws IOException if an error occurs while saving the subscription history
	 */
	public void saveSubscriptionHistory(String subscriberId, List<String> actions) throws IOException {
		StringBuilder command = new StringBuilder("SAVE_SUBSCRIPTION_HISTORY," + subscriberId);
		for (String action : actions) {
			command.append(",").append(action);
		}
		sendRequest(command.toString());
	}

	/**
	 * Updates the status of a specific subscriber.
	 *
	 * @param subscriberId the subscriber's ID
	 * @param newStatus    the new status to set
	 * @throws IOException if an error occurs while updating the subscriber's status
	 */
	public void updateSubscriberStatus(String subscriberId, String newStatus) throws IOException {
		sendRequest("UPDATE_SUBSCRIBER_STATUS," + subscriberId + "," + newStatus);
	}

	public boolean isBookReserved(int bookId) throws IOException {
		String response = sendRequest("IS_BOOK_RESERVED," + bookId);
		return "true".equalsIgnoreCase(response);
	}

	public String prolongLoan(int loanId, String newReturnDate) throws IOException {
		return sendRequest("PROLONG_LOAN," + loanId + "," + newReturnDate);
	}

	/**
	 * Closes the connection to the server.
	 *
	 * @throws IOException if an error occurs while closing the connection
	 */
	public void close() throws IOException {
		if (socket != null && !socket.isClosed()) {
			socket.close();
			System.out.println("Connection closed.");
		}
	}
}
