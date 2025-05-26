import java.io.*;
import java.util.*;

public class SalesmanData extends SalesmanPerson {
    private String email;
    private String password;

    // basic constructor
    public SalesmanData(String id, String name, String email, String phone, String password) {
        super(id, name, phone);
        this.email = email;
        this.password = password;
    }

    // getters
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getID() { return super.id; }
    public String getName() { return super.name; }
    public String getPhone() { return super.phone; }

    // setters
    public void setName(String name) { super.name = name; }
    public void setPhone(String phone) { super.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String getProfile() {
        return super.getProfile() + "\nEmail: " + email;
    }

    // load all salesmen from file
    public static List<SalesmanData> loadSalesmen(String filename) {
        List<SalesmanData> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    // handle empty fields
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String email = parts[2].trim().isEmpty() ? "Not set" : parts[2].trim();
                    String phone = parts[3].trim().isEmpty() ? "Not set" : parts[3].trim();
                    String password = parts[4].trim();
                    
                    // only add if we have the required fields
                    if (!id.isEmpty() && !name.isEmpty() && !password.isEmpty()) {
                        list.add(new SalesmanData(id, name, email, phone, password));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading salesman file: " + e.getMessage());
        }
        return list;
    }

    // save all salesmen to file
    public static void saveSalesmen(String filename, List<SalesmanData> salesmen) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (SalesmanData s : salesmen) {
                pw.println(s.id + "," + s.name + "," + s.email + "," + s.phone + "," + s.password);
            }
        } catch (IOException e) {
            System.err.println("Error saving salesman file: " + e.getMessage());
        }
    }

    // get next available salesman ID
    public static String getNextSalesmanID(String filename) throws IOException {
        File file = new File(filename);

        // if file is empty or doesn't exist, start with S0001
        if (!file.exists() || file.length() == 0) return "S0001";

        // read the last line to get the last ID
        String lastLine = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lastLine = line;
                }
            }
        }

        // extract number from last ID and increment
        String[] parts = lastLine.split(",");
        String lastID = parts[0];
        int num = Integer.parseInt(lastID.substring(1));
        return String.format("S%04d", num + 1);
    }

    // check if login is valid
    public static boolean login(String filename, String id, String password) {
        try {
            List<SalesmanData> salesmen = loadSalesmen(filename);
            for (SalesmanData s : salesmen) {
                if (s.getID().equals(id) && s.getPassword().equals(password)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return false;
    }

    // find a salesman by ID
    public static SalesmanData searchSalesman(String filename, String id) {
        try {
            List<SalesmanData> salesmen = loadSalesmen(filename);
            for (SalesmanData s : salesmen) {
                if (s.getID().equals(id)) {
                    return s;
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching for salesman: " + e.getMessage());
        }
        return null;
    }
} 