
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private DBHandler dbHandler;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.dbHandler = new DBHandler(); // Instantiate DBHandler
    }



    @Override
    public void run() {
        try (ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream())) {

            System.out.println("New client connected: " + clientSocket.getInetAddress());

            Object obj;
            while ((obj = input.readObject()) != null) {
                if (!(obj instanceof String)) {
                    output.writeObject("Invalid command format.");
                    output.flush();
                    continue;
                }

                String command = (String) obj;
                System.out.println("Received command: " + command);

                if (command.equals("TEST_CONNECTION")) {
                    output.writeObject("Connection successful");
                } else if (command.startsWith("LOGIN")) {
                    handleLogin(command, output);
                } else if (command.startsWith("REGISTER_SUBSCRIBER")) {
                    handleRegisterSubscriber(command, output);
                } else {
                    output.writeObject("Unknown command");
                }
                output.flush();
            }
        } catch (Exception e) {
            System.out.println("Client disconnected.");
        }
    }

    private void handleLogin(String command, ObjectOutputStream output) throws IOException {
        String[] parts = command.split(",");
        if (parts.length != 4) {
            output.writeObject("Invalid command format.");
            return;
        }

        String role = parts[1].trim();
        String username = parts[2].trim();
        String password = parts[3].trim();

        System.out.println("Processing login request: Role=" + role + ", Username=" + username);

        boolean isValid = false;

        try {
            if (role.equalsIgnoreCase("Subscriber")) {
                isValid = dbHandler.validateSubscriberLogin(username, password);
            } else if (role.equalsIgnoreCase("Librarian")) {
                isValid = dbHandler.validateLibrarianLogin(username, password);
            } else {
                output.writeObject("Invalid role specified.");
                return;
            }

            if (isValid) {
                System.out.println("Login successful for: " + username);
                output.writeObject("Login successful");
            } else {
                System.out.println("Login failed for: " + username);
                output.writeObject("Invalid username or password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            output.writeObject("Error processing login: " + e.getMessage());
        }
    }

    private void handleRegisterSubscriber(String command, ObjectOutputStream output) throws IOException {
        String[] parts = command.split(",");
        if (parts.length != 6) {
            output.writeObject("Invalid command format. Expected: REGISTER_SUBSCRIBER,id,name,phone,email,password");
            return;
        }

        int subscriberId = Integer.parseInt(parts[1]);
        String name = parts[2].trim();
        String phone = parts[3].trim();
        String email = parts[4].trim();
        String password = parts[5].trim();

        System.out.println("Processing registration request for: " + name);

        try {
            dbHandler.addSubscriberWithId(subscriberId, name, phone, email, password);
            output.writeObject("Registration successful");
        } catch (SQLException e) {
            e.printStackTrace();
            output.writeObject("Database error: " + e.getMessage());
        }
    }

}
