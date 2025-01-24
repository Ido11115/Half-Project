/**
 * The ServerMain class serves as the entry point for starting the server application.
 * It initializes the Server instance and starts the server.
 */
public class ServerMain {

    /**
     * The main method that launches the server application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
