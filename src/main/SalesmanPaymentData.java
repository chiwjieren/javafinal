import java.io.*;
import java.util.ArrayList;
import java.util.List;

// class to handle payment records
public class SalesmanPaymentData {
    private String payID;
    private double amount;
    private String method;
    private String date;
    private String bookingID;

    // create a new payment record
    public SalesmanPaymentData(String payID, double amount, String method, String date, String bookingID) {
        this.payID = payID;
        this.amount = amount;
        this.method = method;
        this.date = date;
        this.bookingID = bookingID;
    }

    // getters
    public String getPayID() { return payID; }
    public double getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getDate() { return date; }
    public String getBookingID() { return bookingID; }

    // convert payment to string for file storage
    @Override
    public String toString() {
        return String.format("%s,%.2f,%s,%s,%s", payID, amount, method, date, bookingID);
    }

    // get next available payment ID
    public static String getNextPaymentID(String filename) throws IOException {
        File file = new File(filename);

        // if file is empty or doesn't exist, start with P0001
        if (!file.exists() || file.length() == 0) return "P0001";

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
        return String.format("P%04d", num + 1);
    }

    // save a new payment record to file
    public static void savePayment(String filename, SalesmanPaymentData payment) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.println(payment.toString());
        }
    }

    // load all payment records from file
    public static List<SalesmanPaymentData> loadPayments(String filename) {
        List<SalesmanPaymentData> payments = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length == 5) {
                        SalesmanPaymentData payment = new SalesmanPaymentData(
                            parts[0],
                            Double.parseDouble(parts[1]),
                            parts[2],
                            parts[3],
                            parts[4]
                        );
                        payments.add(payment);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading payment file: " + e.getMessage());
        }
        return payments;
    }
} 