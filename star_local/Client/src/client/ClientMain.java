package client;
import java.io.*;
import java.net.Socket;

public class ClientMain {
	private static final String SERVER_ADDRESS = "localhost";
	private static final int SERVER_PORT = 5555;

	public static void main(String[] args) {
		try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

			// GET_SUBSCRIBERS request
			output.writeObject("GET_SUBSCRIBERS");
			String subscribers = (String) input.readObject();
			System.out.println("Subscribers:\n" + subscribers);

			//  UPDATE_SUBSCRIBER request
			output.writeObject("UPDATE_SUBSCRIBER,1,55555555,new_email@example.com");
			String updateResponse = (String) input.readObject();
			System.out.println("Update response: " + updateResponse);

			// end comm
			output.writeObject("QUIT");
			String goodbye = (String) input.readObject();
			System.out.println(goodbye);

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
