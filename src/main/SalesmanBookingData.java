import java.io.*;
import java.util.*;

// class to handle car booking records
public class SalesmanBookingData {
    private String bookingID;
    private String date;
    private String status;
    private String salesmanID;
    private String customerID;
    private String carID;
    private boolean payment;
    private double price;
    private long lastStatusChangeTime;  // track when status was last changed

    // create a new booking
    public SalesmanBookingData(String bookingID, String date, String status, boolean payment,
                  String salesmanID, String customerID, String carID, double price) {
        this.bookingID = bookingID;
        this.date = date;
        this.status = status;
        this.payment = payment;
        this.salesmanID = salesmanID;
        this.customerID = customerID;
        this.carID = carID;
        this.price = price;
        this.lastStatusChangeTime = System.currentTimeMillis();
    }

    // getters
    public String getBookingID() { return bookingID; }
    public String getSalesmanID() { return salesmanID; }
    public String getCarID() { return carID; }
    public boolean getPayment() { return payment; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
    public String getCustomerID() { return customerID; }
    public double getPrice() { return price; }
    public String getBookingDate() { return date; }
    public long getLastStatusChangeTime() { return lastStatusChangeTime; }

    // setters
    public void setStatus(String status) {
        this.status = status;
        this.lastStatusChangeTime = System.currentTimeMillis();
    }
    public void setPayment(boolean payment) { this.payment = payment; }
    public void setPrice(double price) { this.price = price; }
    public void setSalesmanID(String salesmanID) { this.salesmanID = salesmanID; }
    public void setLastStatusChangeTime(long lastStatusChangeTime) {
        this.lastStatusChangeTime = lastStatusChangeTime;
    }

    // convert booking to string for file storage
    @Override
    public String toString() {
        return String.join(",",
            bookingID,
            date,
            status,
            String.valueOf(payment),
            salesmanID,
            customerID,
            carID,
            String.valueOf(price)
        );
    }

    // read all bookings from file
    public static List<SalesmanBookingData> readBookings(String filename) {
        List<SalesmanBookingData> bookings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    String bookingID = parts[0];
                    String date = parts[1];
                    String status = parts[2];
                    boolean payment = Boolean.parseBoolean(parts[3]);
                    String salesmanID = parts[4].trim();
                    String customerID = parts[5];
                    String carID = parts[6];
                    double price = Double.parseDouble(parts[7]);
                    
                    bookings.add(new SalesmanBookingData(bookingID, date, status, payment, salesmanID, customerID, carID, price));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading bookings: " + e.getMessage());
        }
        return bookings;
    }

    // save all bookings to file
    public static void saveBookings(String filename, List<SalesmanBookingData> bookings) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (SalesmanBookingData b : bookings) {
                writer.println(b.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving booking file: " + e.getMessage());
        }
    }

    // Convenience method to read all bookings from SalesmanBooking.txt
    public static List<SalesmanBookingData> readAllBookings() {
        return readBookings("SalesmanBooking.txt");
    }
}