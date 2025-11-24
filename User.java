package lab1;
import java.util.regex.Pattern;

public class User implements Comparable<User> {

    // Regex for validating email
    private static final Pattern EMAIL_RX = Pattern.compile(
            "^[A-Za-z0-9._%+-]*@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$"
    );

    // Regex for password: 8–12 chars, allowed symbols, must contain letters & digits
    private static final Pattern PASSWORD_RX = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z0-9+^_.%!@#]{8,12}$"
    );

    private final String username;
    private final String password;

    public User(String username, String password) throws Exception {
        try {
            validate(username, password);
        } catch (Exception e) {
            throw e;
        }

        this.username = username;
        this.password = password;
    }

    private static boolean isEmail(String s) {
        return EMAIL_RX.matcher(s).matches();
    }

    private static boolean isPasswordValid(String p) {
        return PASSWORD_RX.matcher(p).matches();
    }

    private static void validate(String username, String password) throws Exception{
        try {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("Please enter a valid Email as username");
            }

            // Email validation
            if (username.contains("@") && !isEmail(username)) {
                throw new IllegalArgumentException("Please enter a valid Email as username");
            }

            if (username.length() > 50) {
                throw new IllegalArgumentException("Username is too long, try something shorter");
            }

            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Please enter a valid password");
            }

            if (password.length() < 8) {
                throw new IllegalArgumentException("Your password is too short, add more characters");
            }

            if (password.length() > 12) {
                throw new IllegalArgumentException("Your password is too long, try a shorter one");
            }

            if (!isPasswordValid(password)) {
                throw new IllegalArgumentException("Please enter a valid password");
            }



        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("Unexpected validation error: " + e.getMessage());
        }
    }

    public String toString() {
        return this.getUsername() + ' ' + this.getPassword();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    @Override
    public int compareTo(User other) {
        // Case-insensitive alphabetical order
        return this.username.compareToIgnoreCase(other.username);
    }
    public static boolean isValidCredentials(String username, String password) throws Exception {
        validate(username, password);
        return true;
    }

}
