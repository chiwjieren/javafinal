import java.io.*;
import java.util.ArrayList;
import java.util.List;

// class to handle customer feedback and ratings
public class SalesmanFeedback {
    private String feedbackID;
    private String bookingID;
    private String salesmanID;
    private String feedback;
    private String paymentMethod;
    private int rating;  // 1-5 star rating

    // create a new feedback record
    public SalesmanFeedback(String feedbackID, String bookingID, String salesmanID, String feedback, String paymentMethod, int rating) {
        this.feedbackID = feedbackID;
        this.bookingID = bookingID;
        this.salesmanID = salesmanID;
        this.feedback = feedback;
        this.paymentMethod = paymentMethod;
        // make sure rating is between 1 and 5
        this.rating = Math.min(Math.max(rating, 1), 5);
    }

    // getters
    public String getFeedbackID() { return feedbackID; }
    public String getBookingID() { return bookingID; }
    public String getSalesmanID() { return salesmanID; }
    public String getFeedback() { return feedback; }
    public String getPaymentMethod() { return paymentMethod; }
    public int getRating() { return rating; }

    // get next available feedback ID
    public static String getNextFeedbackID(String filename) throws IOException {
        File file = new File(filename);

        // if file is empty or doesn't exist, start with F0001
        if (!file.exists() || file.length() == 0) return "F0001";

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
        return String.format("F%04d", num + 1);
    }

    // save a new feedback to file
    public static void saveFeedback(String filename, SalesmanFeedback feedback) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {
            pw.println(feedback.feedbackID + "," + feedback.bookingID + "," + 
                      feedback.salesmanID + "," + feedback.feedback + "," + 
                      feedback.paymentMethod + "," + feedback.rating);
        } catch (IOException e) {
            System.err.println("Error saving feedback: " + e.getMessage());
        }
    }

    // load all feedbacks from file
    public static List<SalesmanFeedback> loadFeedbacks(String filename) {
        List<SalesmanFeedback> feedbacks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    feedbacks.add(new SalesmanFeedback(
                        parts[0],  // feedbackID
                        parts[1],  // bookingID
                        parts[2],  // salesmanID
                        parts[3],  // feedback
                        parts[4],  // paymentMethod
                        Integer.parseInt(parts[5])  // rating
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading feedback file: " + e.getMessage());
        }
        return feedbacks;
    }
} 