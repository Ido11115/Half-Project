package client;

import java.io.*;
import java.net.Socket;

public class ServerCommunicator {
    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ServerCommunicator(String serverAddress, int serverPort) throws IOException {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        connect();
    }

    private void connect() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    public String sendRequest(String command) throws IOException, ClassNotFoundException {
        if (socket == null || socket.isClosed()) {
            throw new IOException("Not connected to server.");
        }

        output.writeObject(command);
        output.flush();

        Object response = input.readObject();
        if (response instanceof String) {
            return (String) response;
        } else {
            return "Unexpected response type from server.";
        }
    }

    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
