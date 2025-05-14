import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Admin extends User {
    String adminID;

    public Admin(String username, String password, String adminID) {
        super(username, password);
        this.adminID = adminID;
    }
    
    public static String getNextAdminID(String filename) throws IOException {
        File file = new File(filename);

        if (!file.exists() || file.length() == 0) return "A0001";

        String lastLine = "";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lastLine = line;
                }
            }
        }

        String[] line = lastLine.split(",", 2);
        String lastID = line[0];
        
        int num = Integer.parseInt(lastID.substring(1));

        return String.format("A%04d", num + 1);
    }

    public static boolean login(String filename, ArrayList<String> data) throws IOException {
        String id = data.get(0);
        String password = data.get(1);

        File file = new File(filename);

        if (!file.exists()) return false;

        Admin admin = searchAdmin(filename, id);

        if (admin == null) return false;
        
        return password != null && password.equals(admin.getPassword());
    }

    public static Admin searchAdmin(String filename, String id) {
        File file = new File(filename);

        if (!file.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue;

                String adminID = parts[0];
                String username = parts[1];
                String password = parts[2];

                if (adminID.equals(id)) return new Admin(username, password, adminID);
            }
        } 
        
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}