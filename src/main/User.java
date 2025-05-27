import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        if (!cleanInput(username)) throw new IllegalArgumentException("Invalid username");
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        if (!cleanInput(password)) throw new IllegalArgumentException("Invalid password");
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    
    public static boolean cleanInput(String input) {
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

    public static boolean delete(String filename, String idToDelete) throws IOException {
        ArrayList<String> keptLines = new ArrayList<>();
        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            if (!cleanInput(idToDelete)) {
                throw new IllegalArgumentException("Invalid Input!");
            }   

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 2);
                String id = parts[0];

                if (id.equals(idToDelete)) {
                    deleted = true;    
                } 
                
                else {
                    keptLines.add(line);
                }
            }
        }

        if (deleted) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false))) {
                for (String kept : keptLines) {
                    writer.println(kept);
                }
            }
        }

        return deleted;
    }

    public static boolean update(String filename, String idToUpdate, String newUsername, String newPassword) throws IOException {
        File inputFile = new File(filename);
        if (!inputFile.exists()) return false;

        File tempFile  = new File(inputFile.getParent(), "salesman.tmp");

        boolean updated = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    writer.write(line);
                    writer.newLine();
                    continue;
                }

                String[] fields = line.split(",", 3);
                if (fields.length < 3) {
                    writer.write(line);
                }

                else if (fields[0].equals(idToUpdate)) {
                    if (!cleanInput(idToUpdate) || !cleanInput(newUsername) || !cleanInput(newPassword)) {
                        throw new IllegalArgumentException("Invalid Input!");
                    }

                    String newLine = String.join(",", idToUpdate, newUsername, newPassword);
                    writer.write(newLine);
                    updated = true;
                } 
                
                else {
                    writer.write(line);
                }

                writer.newLine();
            }
        }

        if (updated) {
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                throw new IOException("Could not replace original file.");
            }
        } 
        
        else {
            tempFile.delete();
        }

        return updated;
    }
}
