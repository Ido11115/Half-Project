package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The ClientGUI class is the main entry point for the JavaFX application.
 * It initializes and displays the GUI for connecting to a server.
 * <p>
 * This class relies on the default constructor provided by Java,
 * as no additional fields or setup are required upon instantiation.
 * </p>
 */
public class ClientGUI extends Application {

    /**
     * Default constructor for the ClientGUI class.
     * <p>
     * This constructor is implicitly provided by Java and requires no additional implementation,
     * as all necessary setup is performed in the {@link #start(Stage)} method.
     * </p>
     */
    public ClientGUI() {
        // Default constructor
    }

    /**
     * Starts the JavaFX application by loading the "Connect.fxml" file,
     * setting up the primary stage, and showing the GUI.
     *
     * @param primaryStage the main stage for the application
     * @throws Exception if an error occurs while loading the FXML file
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Connect.fxml"));
        Parent root = loader.load();

        ConnectController connectController = loader.getController();
        connectController.setStage(primaryStage);

        primaryStage.setTitle("Connect to Server");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
