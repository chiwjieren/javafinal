import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class User {
    String username;
    String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    
    protected static boolean cleanInput(String input) {
        if (input.trim().isEmpty()) return false;
        return !(input.contains(",") || input.contains("\"") || input.contains("\n") || input.contains("\r"));
    }

    public static void register(String filename, ArrayList<String> data) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {
            
            String id = data.get(0);
            String username = data.get(1);
            String password = data.get(2);

            if (!cleanInput(id) || !cleanInput(username) || !cleanInput(password)) {
                throw new IllegalArgumentException("Invalid Input!");
            }   

            pw.println();
            pw.print(id + "," + username + "," + password);
        }         
    }

}
