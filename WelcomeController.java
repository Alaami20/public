package lab1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class WelcomeController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button closeButton;

    public void setUsername(String username) {
        welcomeLabel.setText("Welcome, " + username + "!");
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
