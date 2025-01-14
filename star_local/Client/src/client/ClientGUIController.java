package client;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class ClientGUIController {

    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
        Stage currentStage = (Stage) ((javafx.scene.Node) null).getScene().getWindow();
        currentStage.close();
    }
}
