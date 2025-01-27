/**
 * The ServerMain class serves as the entry point for starting the server application.
 * It initializes the Server instance and starts the server.
 */
public class ServerMain {

	/**
     * Default constructor for the ServerMain class.
     * <p>
     * This constructor is not strictly necessary, as the ServerMain class is intended
     * to be used as a utility class with a main method. It is added here for documentation
     * purposes and to comply with standards.
     * </p>
     */
    public ServerMain() {
        // No specific initialization required
    }
	
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
