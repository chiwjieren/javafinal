import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class Book {
    private String bookingID;
    private String carID;
    private String customerID;
    private LocalDateTime timestamp;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getNextBookingID(String filename) throws IOException {
        File file = new File(filename);

        if (!file.exists() || file.length() == 0) return "B0001";

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

        return String.format("B%04d", num + 1);
    }

    public Book(String bookingID, String carID, String customerID, LocalDateTime timestamp) {
        this.bookingID = bookingID;
        this.carID = carID;
        this.customerID = customerID;
        this.timestamp = timestamp;
    }

    public String getBookingID() { return bookingID; }
    public String getCarID() { return carID; }
    public String getCustomerID() { return customerID; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public static Book getBooking(String line) {
        String[] f = line.split(",", 5);
        return new Book(f[0], f[1], f[2], LocalDateTime.parse(f[4], FMT));
    }

    public static List<Book> loadAll(String filename) throws IOException {
        List<Book> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String line;

            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) { list.add(getBooking(line)); }
            }
        }
        return list;
    }
    
    public static void addBooking(String filename, String bookingID, String carID, String customerID, LocalDateTime timestamp) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename, true))) {
            out.println(String.join(",", bookingID, carID, customerID, timestamp.format(FMT)));
        }
    }
    
}





