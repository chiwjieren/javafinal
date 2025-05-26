import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SalesmanViewSales extends JFrame {
    private List<SalesmanBookingData> bookings;
    private List<SalesmanCar> cars;
    private String salesmanID;
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JLabel summaryLabel; // Moved summaryLabel declaration here

    public SalesmanViewSales(List<SalesmanBookingData> bookings, List<SalesmanCar> cars, String salesmanID) {
        this.bookings = bookings;
        this.cars = cars;
        this.salesmanID = salesmanID;

        // Setup the frame
        setTitle("Sales History");
        setSize(1000, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create table model
        String[] columns = {"Booking ID", "Date", "Status", "Customer ID", "Car Details", "Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        salesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(salesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add buttons and summary panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        JButton exitButton = new JButton("Exit");
        refreshButton.addActionListener(e -> refreshTable());
        exitButton.addActionListener(e -> dispose());
        buttonPanel.add(refreshButton);
        buttonPanel.add(exitButton);

        // Summary label
        summaryLabel = new JLabel();
        summaryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Add summary label above the buttons
        bottomPanel.add(summaryLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load initial data
        refreshTable();
        setVisible(true);
    }

    private void refreshTable() {
        // Clear existing data
        tableModel.setRowCount(0);

        double totalSales = 0;
        int totalBookings = 0;

        // Add bookings to table
        for (SalesmanBookingData booking : bookings) {
            if (booking.getSalesmanID().equals(salesmanID) && "Paid".equalsIgnoreCase(booking.getStatus())) {
                // Find car details
                String carDetails = "";
                for (SalesmanCar car : cars) {
                    if (car.getCarID().equals(booking.getCarID())) {
                        carDetails = car.getBrand() + " " + car.getModel() + " (" + car.getCarID() + ")";
                        break;
                    }
                }

                Object[] row = {
                    booking.getBookingID(),
                    booking.getDate(),
                    booking.getStatus(), // Status: 'Paid' means booking is finished and payment is received
                    booking.getCustomerID(),
                    carDetails,
                    String.format("RM%.2f", booking.getPrice())
                };
                tableModel.addRow(row);

                totalSales += booking.getPrice();
                totalBookings++;
            }
        }

        // Update summary label
        String summaryText = String.format("Sales Summary:  Total Bookings: %d    Total Sales: RM%.2f", totalBookings, totalSales);
        summaryLabel.setText(summaryText);
        revalidate();
        repaint();
    }
}