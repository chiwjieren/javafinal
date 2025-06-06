import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Salesman extends User{
    private String salesmanID;

    public Salesman(String username, String password, String salesmanID) {
        super(username, password);
        this.salesmanID = salesmanID;
    }

    public String getSalesmanID() {
        return salesmanID;
    }

    public void setCarID(String salesmanID) {
        if (!cleanInput(salesmanID)) throw new IllegalArgumentException("Invalid ID");
        this.salesmanID = salesmanID;
    }

    public static String getNextSalesmanID(String filename) throws IOException {
        File file = new File(filename);

        if (!file.exists() || file.length() == 0) return "S0001";

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

        return String.format("S%04d", num + 1);
    }

    public static boolean login(String filename, ArrayList<String> data) throws IOException {
        String id = data.get(0);
        String password = data.get(1);

        File file = new File(filename);

        if (!file.exists()) return false;

        Salesman salesman = searchSalesman(filename, id);

        if (salesman == null) return false;
        
        return password != null && password.equals(salesman.getPassword());
    }

    public static Salesman searchSalesman(String filename, String id) {
        File file = new File(filename);

        if (!file.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue;

                String salesmanID = parts[0];
                String username = parts[1];
                String password = parts[2];

                if (salesmanID.equals(id)) return new Salesman(username, password, salesmanID);
            }
        } 
        
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
