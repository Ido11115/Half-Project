package client;

import java.io.*;
import java.net.Socket;

public class ServerCommunicator {
	private String serverAddress;
	private int serverPort;

	public ServerCommunicator(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}

	public String sendRequest(String command) throws IOException, ClassNotFoundException {
		try (Socket socket = new Socket(serverAddress, serverPort);
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

			output.writeObject(command);
			output.flush();

			Object response = input.readObject();
			if (response instanceof String) {
				return (String) response;
			} else {
				return "Unexpected response type from server.";
			}
		} catch (EOFException e) {
			return "Connection closed by server unexpectedly.";
		}
	}

	public String getSubscribers() {
		try {
			return sendRequest("GET_SUBSCRIBERS");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return "Error fetching subscribers: " + e.getMessage();
		}
	}

	public String updateSubscriber(int id, String phone, String email) throws IOException, ClassNotFoundException {
		try (Socket socket = new Socket(serverAddress, serverPort);
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

			// Send the update command
			String command = "UPDATE_SUBSCRIBER," + id + "," + phone + "," + email;
			output.writeObject(command);
			output.flush();

			// Get the response from the server
			Object response = input.readObject();
			if (response instanceof String) {
				return (String) response;
			} else {
				return "Unexpected response type from server.";
			}
		} catch (EOFException e) {
			return "Connection closed by server unexpectedly.";
		}
	}

}
