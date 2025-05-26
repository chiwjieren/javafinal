import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;

// main dashboard window for salesmen
public class SalesmanMain extends JFrame {
    private List<SalesmanData> salesmenlist;
    private List<SalesmanCar> cars;
    private List<SalesmanBookingData> bookings;
    private String salesmanID;

    public SalesmanMain(String salesmanID) {
        this.salesmanID = salesmanID;
        
        // initialize lists
        salesmenlist = new ArrayList<>();
        cars = new ArrayList<>();
        bookings = new ArrayList<>();
        
        // load all data
        loadData();

        // setup the window
        setTitle("Salesman Dashboard");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1));

        // create all the buttons
        setupButtons();

        setVisible(true);
    }

    private void setupButtons() {
        // create buttons
        JButton profileButton = new JButton("Profile");
        JButton viewBookings = new JButton("View Available Bookings");
        JButton updateCar = new JButton("Cancel Booking");
        JButton collectPayment = new JButton("Collect Car Payment");
        JButton viewSales = new JButton("View My Sales");
        JButton exit = new JButton("Logout");

        // add action listeners
        profileButton.addActionListener(e -> {
            if (!salesmenlist.isEmpty()) {
                new SalesmanViewProfile(salesmenlist.get(0), salesmenlist);
            } else {
                JOptionPane.showMessageDialog(this, "No salesman data found!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewBookings.addActionListener(e -> {
            if (bookings == null || bookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No bookings available!", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new SalesmanBooking(bookings, salesmenlist); // Changed from SalesmanViewBookings to SalesmanBooking
        });

        updateCar.addActionListener(e -> {
            if (cars == null || cars.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No cars available!", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new SalesmanCancel(cars, bookings, salesmanID);
        });

        collectPayment.addActionListener(e -> {
            if (cars == null || cars.isEmpty() || bookings == null || bookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No data available!", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new SalesmanCollectPayment(bookings, salesmanID);
        });

        viewSales.addActionListener(e -> {
            if (cars == null || cars.isEmpty() || bookings == null || bookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No data available!", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new SalesmanViewSales(bookings, cars, salesmanID);
        });

        exit.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                new Page1();
                dispose();
            }
        });

        // add buttons to frame
        add(profileButton);
        add(viewBookings);
        add(updateCar);
        add(collectPayment);
        add(viewSales);
        add(exit);
    }
    
    private void loadData() {
        try {
            // load all salesmen first
            List<SalesmanData> allSalesmen = SalesmanData.loadSalesmen("salesman.txt");
            if (allSalesmen == null || allSalesmen.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No salesmen data found in salesman.txt", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // filter to get only the logged-in salesman
            salesmenlist = allSalesmen.stream()
                .filter(s -> s.getID().equals(salesmanID))
                .collect(Collectors.toList());
            
            if (salesmenlist.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Logged in salesman not found in database", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
                
            // load bookings
            bookings = SalesmanBookingData.readBookings("SalesmanBooking.txt");
            if (bookings == null) {
                JOptionPane.showMessageDialog(this, "Error loading bookings from SalesmanBooking.txt", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // load cars
            cars = SalesmanCar.loadCarsFromFile("SalesmanCar.txt");
            if (cars == null) {
                JOptionPane.showMessageDialog(this, "Error loading cars from SalesmanCar.txt", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // print debug info
            System.out.println("Data loaded successfully:");
            System.out.println("Salesmen: " + salesmenlist.size());
            System.out.println("Bookings: " + bookings.size());
            System.out.println("Cars: " + cars.size());
            
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // for testing
    public static void main(String[] args) {
        new SalesmanMain("S0001"); // or any valid salesman ID for testing
    }
}