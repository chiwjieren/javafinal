import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Payment {
    private String paymentID;
    private String customerID;
    private int amount;
    private LocalDateTime timestamp;
    private String status;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Payment(String paymentID, String customerID, int amount, LocalDateTime timestamp, String status) {
        this.paymentID  = paymentID;
        this.customerID = customerID;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getPaymentID()        { return paymentID; }
    public String getCustomerID()       { return customerID; }
    public int getAmount()              { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus()           { return status; }

    public static Payment getPayment(String line) {
        String[] f = line.split(",", 5);
        return new Payment(f[0], f[1], Integer.parseInt(f[2].replaceAll("[^0-9.]", "")), LocalDateTime.parse(f[3], FMT), f[4]);
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
            out.println(String.join(",", paymentID, customerID, String.format("RM%.0f", amount), timestamp.format(FMT), status));
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
