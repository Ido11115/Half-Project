package client;

import java.io.*;
import java.net.Socket;

/**
 * The ClientMain class is responsible for establishing a connection to the server,
 * sending requests, and receiving responses. It serves as the main entry point for the client-side
 * application that communicates with the server.
 * <p>
 * This class relies on the default constructor provided by Java, as no additional fields
 * or setup are required upon instantiation.
 * </p>
 */
public class ClientMain {

    /**
     * Default constructor for the ClientMain class.
     * <p>
     * This constructor is implicitly provided by Java and requires no additional implementation,
     * as all setup occurs in the {@link #main(String[])} method.
     * </p>
     */
    public ClientMain() {
        // Default constructor
    }

    /**
     * The address of the server to connect to.
     */
    private static final String SERVER_ADDRESS = "localhost";

    /**
     * The port number of the server to connect to.
     */
    private static final int SERVER_PORT = 5555;

    /**
     * The main method establishes a connection to the server, sends requests, and processes responses.
     *
     * <p>Steps performed:</p>
     * <ul>
     *     <li>Connects to the server using a socket.</li>
     *     <li>Sends a "GET_SUBSCRIBERS" request and displays the server's response.</li>
     *     <li>Sends an "UPDATE_SUBSCRIBER" request to update subscriber information and displays the response.</li>
     *     <li>Sends a "QUIT" command to end communication and displays the server's farewell message.</li>
     * </ul>
     *
     * @param args command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            // GET_SUBSCRIBERS request
            output.writeObject("GET_SUBSCRIBERS");
            String subscribers = (String) input.readObject();
            System.out.println("Subscribers:\n" + subscribers);

            // UPDATE_SUBSCRIBER request
            output.writeObject("UPDATE_SUBSCRIBER,1,55555555,new_email@example.com");
            String updateResponse = (String) input.readObject();
            System.out.println("Update response: " + updateResponse);

            // End communication
            output.writeObject("QUIT");
            String goodbye = (String) input.readObject();
            System.out.println(goodbye);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
