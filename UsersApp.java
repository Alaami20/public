package lab1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class UsersApp {

    public static void main(String[] args) {
        String filename = "Users.txt";  // keep same name you use in the GUI

        ArrayList<User> users = new ArrayList<>();

        File f = new File(filename);
        if (!f.exists()) {
            System.err.println("File " + filename + " not found.");
            return;
        }

        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                if (parts.length < 2) {
                    System.out.println(line + "  -->  Bad line (not enough fields)");
                    continue;
                }

                String u = parts[0];
                String p = parts[1];

                try {
                    User user = new User(u, p);
                    users.add(user);
                } catch (IllegalArgumentException e) {
                    System.out.println(line + "  -->  " + e.getMessage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open " + filename + ": " + e.getMessage());
            return;
        }

        Collections.sort(users);
        System.out.println("Valid users (sorted):");
        for (User u : users) {
            System.out.println(u);
        }
    }
}
