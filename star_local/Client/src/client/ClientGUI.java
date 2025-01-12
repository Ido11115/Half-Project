package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientGUI extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientGUI.fxml"));
	    Parent root = loader.load();

	    primaryStage.setScene(new Scene(root));
	    primaryStage.setTitle("Client GUI");

	    // Show the stage
	    primaryStage.show();

	    // Optionally, trigger login logic here if not handled in the controller
	    ClientController controller = loader.getController();
	    controller.showLoginPopup(); // Ensure the login popup displays
	}
	public static void main(String[] args) {
		launch(args);
	}
}
