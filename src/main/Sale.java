import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Sale {
    private String saleID;
    private String customerID;
    private String carID;
    private String amount;
    private String timestamp;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Sale(String saleID, String customerID, String carID, String amount, String timestamp) {
        this.saleID = saleID;
        this.customerID = customerID;
        this.carID = carID;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getSaleID() { return saleID; }
    public String getCustomerID() { return customerID; }
    public String getCarID() { return carID; }
    public String getAmount() { return amount; }
    public String getTimestamp() { return timestamp; }

    public String getRating() {
        try (BufferedReader br = new BufferedReader(new FileReader("customerratings.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 3);
                if (parts.length >= 2 && parts[0].equals(saleID)) {
                    return parts[1] + " stars";
                }
            }
        } catch (IOException ex) {
            // If file doesn't exist or can't be read, return "No rating"
        }
        return "No rating";
    }

    public String getCustomerReview() {
        try (BufferedReader br = new BufferedReader(new FileReader("customerreview.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 4);
                if (parts.length >= 4 && parts[0].equals(saleID)) {
                    return parts[2];
                }
            }
        } catch (IOException ex) {
            // If file doesn't exist or can't be read, return "No review"
        }
        return "No review";
    }

    public String getSalesmanComment() {
        try (BufferedReader br = new BufferedReader(new FileReader("salesmancomment.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 4);
                if (parts.length >= 4 && parts[0].equals(saleID)) {
                    return parts[2];
                }
            }
        } catch (IOException ex) {
            // If file doesn't exist or can't be read, return "No comment"
        }
        return "No comment";
    }

    public static List<Sale> loadAll(String filename) throws IOException {
        List<Sale> sales = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 5);
                if (parts.length >= 5) {
                    sales.add(new Sale(parts[0], parts[1], parts[2], parts[3], parts[4]));
                }
            }
        }
        return sales;
    }

    public static double calculateAverageRating() {
        double total = 0;
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("customerratings.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 3);
                if (parts.length >= 2) {
                    try {
                        total += Integer.parseInt(parts[1]);
                        count++;
                    } catch (NumberFormatException ex) {
                        // Skip invalid ratings
                    }
                }
            }
        } catch (IOException ex) {
            // If file doesn't exist or can't be read, return 0
        }
        return count > 0 ? total / count : 0;
    }

    public static Map<Integer, Long> calculateRatingDistribution() {
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0L);
        }

        try (BufferedReader br = new BufferedReader(new FileReader("customerratings.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 3);
                if (parts.length >= 2) {
                    try {
                        int rating = Integer.parseInt(parts[1]);
                        if (rating >= 1 && rating <= 5) {
                            distribution.put(rating, distribution.get(rating) + 1);
                        }
                    } catch (NumberFormatException ex) {
                        // Skip invalid ratings
                    }
                }
            }
        } catch (IOException ex) {
            // If file doesn't exist or can't be read, return empty distribution
        }
        return distribution;
    }

    public void save(String filename) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename, true))) {
            out.println(String.join(",",
                saleID,
                customerID,
                carID,
                amount,
                timestamp
            ));
        }
    }

    public static boolean delete(String filename, String saleID) throws IOException {
        File in  = new File(filename);
        File tmp = new File(in.getParent(), "sales.tmp");
        boolean removed = false;

        try (BufferedReader r = new BufferedReader(new FileReader(in));
             PrintWriter   w = new PrintWriter(new FileWriter(tmp))) {
                
            String line;
            
            while ((line = r.readLine()) != null) {
                if (line.startsWith(saleID + ",")) {
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
