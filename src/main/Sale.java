import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Sale {
    private String salesID;
    private String customerID;
    private String salesmanID;
    private String carID;
    private LocalDateTime timestamp;
    private int rating;
    private String customerReview;
    private String salesmanComment;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Sale(String salesID, String customerID, String salesmanID,
                String carID, LocalDateTime timestamp, int rating,
                String customerReview, String salesmanComment) {
        this.salesID         = salesID;
        this.customerID      = customerID;
        this.salesmanID      = salesmanID;
        this.carID           = carID;
        this.timestamp       = timestamp;
        this.rating          = rating;
        this.customerReview  = customerReview;
        this.salesmanComment = salesmanComment;
    }

    public String getSalesID()          { return salesID; }
    public String getCustomerID()       { return customerID; }
    public String getSalesmanID()       { return salesmanID; }
    public String getCarID()            { return carID; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getRating()              { return rating; }
    public String getCustomerReview()   { return customerReview; }
    public String getSalesmanComment()  { return salesmanComment; }

    public static Sale getSale(String line) {
        String[] f = line.split(",", 8);
        return new Sale(
            f[0], f[1], f[2], f[3],
            LocalDateTime.parse(f[4], FMT),
            Integer.parseInt(f[5]),
            f[6], f[7]
        );
    }

    public static List<Sale> loadAll(String filename) throws IOException {
        List<Sale> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) list.add(getSale(line));
            }
        }
        return list;
    }

    public void save(String filename) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename, true))) {
            out.println(String.join(",",
                salesID,
                customerID,
                salesmanID,
                carID,
                timestamp.format(FMT),
                Integer.toString(rating),
                customerReview,
                salesmanComment
            ));
        }
    }

    public static boolean delete(String filename, String salesID) throws IOException {
        File in  = new File(filename);
        File tmp = new File(in.getParent(), "sales.tmp");
        boolean removed = false;

        try (BufferedReader r = new BufferedReader(new FileReader(in));
             PrintWriter   w = new PrintWriter(new FileWriter(tmp))) {
                
            String line;
            
            while ((line = r.readLine()) != null) {

                if (line.startsWith(salesID + ",")) {
                    removed = true;
                } 
                
                else {
                    w.println(line);
                }
            }
        }
        if (removed) {
            if (!in.delete() || !tmp.renameTo(in))
                throw new IOException("Could not replace sales file");
        } 
        
        else {
            tmp.delete();
        }
        return removed;
    }
}
