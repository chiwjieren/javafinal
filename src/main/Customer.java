import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Customer extends User {

    private String customerID;

    public Customer(String username, String password, String customerID) {
        super(username, password);
        this.customerID = customerID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        if (!cleanInput(customerID)) throw new IllegalArgumentException("Invalid ID");
        this.customerID = customerID;
    }

    public static String getNextCustomerID(String filename) throws IOException {
        File file = new File(filename);

        if (!file.exists() || file.length() == 0) return "C0001";

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

        return String.format("C%04d", num + 1);
    }

    public static boolean login(String filename, ArrayList<String> data) throws IOException {
        String id = data.get(0);
        String password = data.get(1);

        File file = new File(filename);

        if (!file.exists()) return false;

        Customer customer = searchCustomer(filename, id);

        if (customer == null) return false;
        
        return password != null && password.equals(customer.getPassword());
    }

    public static Customer searchCustomer(String filename, String id) {
        File file = new File(filename);

        if (!file.exists()) return null;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", 3);

                String customerID = parts[0];
                String username = parts[1];
                String password = parts[2];

                if (customerID.equals(id)) {
                    return new Customer(username, password, customerID);
                }
            }
        } 
        
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
