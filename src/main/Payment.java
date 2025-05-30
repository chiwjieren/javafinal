import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Payment {
    private String paymentID;
    private String customerID;
    private String carID;
    private int amount;
    private LocalDateTime timestamp;
    private String status;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getNextPaymentID(String filename) throws IOException {
        File file = new File(filename);

        if (!file.exists() || file.length() == 0) return "P0001";

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

        return String.format("P%04d", num + 1);
    }

    public Payment(String paymentID, String customerID, String carID, int amount, LocalDateTime timestamp, String status) {
        this.paymentID  = paymentID;
        this.customerID = customerID;
        this.carID = carID;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getPaymentID()        { return paymentID; }
    public String getCustomerID()       { return customerID; }
    public String getCarID()            { return carID; }
    public int getAmount()              { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus()           { return status; }

    public static void addPayment(String filename, String paymentID, String customerID, String carID, int amount, String status, LocalDateTime timestamp) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename, true))) {
            out.println(String.join(",", paymentID, customerID, carID,  String.format("RM%d", amount), timestamp.format(FMT), status));
        }
    }

    public static Payment getPayment(String line) {
        String[] f = line.split(",", 5);
        return new Payment(f[0], f[1], f[2], Integer.parseInt(f[3].replaceAll("[^0-9.]", "")), LocalDateTime.parse(f[4], FMT), f[5]);
    }

    public static List<Payment> loadAll(String filename) throws IOException {
        List<Payment> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (!line.isBlank()) { list.add(getPayment(line)); }

            }
        }
        return list;
    }

    public void save(String filename) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename, true))) {
            out.println(String.join(",", paymentID, customerID, String.format("RM%d", amount), timestamp.format(FMT), status));
        }
    }

    public static boolean delete(String filename, String paymentID) throws IOException {
        File in  = new File(filename);
        File tmp = new File(in.getParent(), "payment.tmp");
        boolean removed = false;

        try (BufferedReader r = new BufferedReader(new FileReader(in));
             PrintWriter   w = new PrintWriter(new FileWriter(tmp))) {

            String line;

            while ((line = r.readLine()) != null) {

                if (line.startsWith(paymentID + ",")) {
                    removed = true;
                } 
                
                else {
                    w.println(line);
                }
            }
        }

        if (removed) {

            if (!in.delete() || !tmp.renameTo(in)) { throw new IOException("Could not replace payment file"); }
        } 
        
        else {
            tmp.delete();
        }

        return removed;
    }
}
