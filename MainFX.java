package lab1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    private static int maxAttempts = 5;
    private static int lockSeconds = 60;

    public static void main(String[] args) {
        if (args.length >= 2) {
            try {
                maxAttempts = Integer.parseInt(args[0]);
                lockSeconds = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Usage: MainFX <n> <t> (max attempts, lock seconds). Using defaults 3 30.");
            }
        } else {
            System.out.println("Usage: MainFX <n> <t> (max attempts, lock seconds). Using defaults 3 30.");
        }
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("login.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController controller = loader.getController();
        controller.initConfig(maxAttempts, lockSeconds);

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
