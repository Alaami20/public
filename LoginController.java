package lab1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private int maxAttempts;
    private int lockSeconds;

    private final Map<String, String> users   = new HashMap<>();
    private final Map<String, Integer> attempts  = new HashMap<>();
    private final Map<String, Long> lockUntil = new HashMap<>();

    public void initConfig(int maxAttempts, int lockSeconds) {
        this.maxAttempts = maxAttempts;
        this.lockSeconds = lockSeconds;
        statusLabel.setText("Max attempts: " + maxAttempts + ", lock: " + lockSeconds + " sec");
        loadUsersFromFile("Users.txt");
    }

    private void loadUsersFromFile(String filename) {
        File f = new File(filename);
        if (!f.exists()) {
            showError("File " + filename + " not found in project root.");
            return;
        }

        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                if (parts.length < 2) {
                    System.err.println("Bad line in " + filename + ": " + line);
                    continue;
                }
                String u = parts[0];
                String p = parts[1];

                try {
                    User user = new User(u, p);
                    users.put(user.getUsername(), user.getPassword());
                } catch (Exception e) {
                    System.err.println("Invalid user in " + filename + ": " + line + " | " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            showError("Cannot open " + filename + ": " + e.getMessage());
        }

        if (users.isEmpty()) {
            showError("No valid users loaded from " + filename);
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            showError("Please enter username (email).");
            return;
        }

        Thread checkThread = new Thread(() -> {
            long now = System.currentTimeMillis();
            Long until = lockUntil.get(username);

            // a) check if locked
            if (until != null && now < until) {
                long remaining = (until - now) / 1000L;
                if (remaining < 1L) remaining = 1L;
                long finalRemaining = remaining;
                Platform.runLater(() ->
                        showError("User is locked. Try again in " + finalRemaining + " seconds."));
                return;
            }

            boolean ok = false;
            String errorMessage = null;

            String expectedPassword = users.get(username);
            if (expectedPassword == null) {
                errorMessage = "Unknown username.";
            } else if (!expectedPassword.equals(password)) {
                errorMessage = "Incorrect password.";
            } else {
                try {
                    User.isValidCredentials(username, password);
                    ok = true;
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                }
            }

            if (ok) {
                Platform.runLater(() -> {
                    attempts.remove(username);
                    lockUntil.remove(username);
                    statusLabel.setText("Login successful.");
                    openWelcome(username);
                });
            } else {
                final String err = (errorMessage == null ? "Login failed." : errorMessage);

                Thread attemptsThread = new Thread(() -> {
                    int used;
                    synchronized (attempts) {
                        used = attempts.getOrDefault(username, 0) + 1;
                        attempts.put(username, used);
                    }

                    boolean lockedNow = false;
                    if (used >= maxAttempts) {
                        long untilTime = System.currentTimeMillis() + (long) lockSeconds * 1000L;
                        synchronized (lockUntil) {
                            lockUntil.put(username, untilTime);
                        }
                        lockedNow = true;
                    }

                    boolean finalLockedNow = lockedNow;
                    Platform.runLater(() -> {
                        if (finalLockedNow) {
                            statusLabel.setText("You used all " + maxAttempts +
                                    " attempts. User is locked for " + lockSeconds + " seconds.");
                            showError("Failed " + used + " times.\nUser is now locked for " + lockSeconds + " seconds.");
                        } else {
                            int left = maxAttempts - used;
                            statusLabel.setText("Login failed. Attempts left: " + left);
                            showError(err + "\nAttempts left: " + left);
                        }
                    });
                });

                attemptsThread.setDaemon(true);
                attemptsThread.start();
            }
        });

        checkThread.setDaemon(true);
        checkThread.start();
    }

    private void openWelcome(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/lab1/welcome.fxml"));
            Parent root = loader.load();
            WelcomeController wc = loader.getController();
            wc.setUsername(username);

            Stage st = new Stage();
            st.setTitle("Welcome");
            st.setScene(new Scene(root, 420, 220));
            st.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Failed to open welcome screen.");
        }
    }

    private void showError(String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(AlertType.ERROR);
            a.setHeaderText(null);
            a.setContentText(msg);
            a.showAndWait();
        });
    }
}
