import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SalesmanViewBookings extends JFrame {
    private List<SalesmanBookingData> bookings;
    private List<SalesmanData> salesmen;
    private JTable bookingTable;
    private DefaultTableModel tableModel;

    public SalesmanViewBookings(List<SalesmanBookingData> bookings, List<SalesmanData> salesmen) {
        this.bookings = bookings;
        this.salesmen = salesmen;

        // Setup the frame
        setTitle("Available Bookings");
        setSize(800, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create table model
        String[] columns = {"Booking ID", "Date", "Status", "Customer ID", "Car ID", "Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        bookingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        JButton exitButton = new JButton("Exit");

        refreshButton.addActionListener(e -> refreshTable());
        exitButton.addActionListener(e -> dispose());

        buttonPanel.add(refreshButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load initial data
        refreshTable();
        setVisible(true);
    }

    private void refreshTable() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Add bookings to table
        for (SalesmanBookingData booking : bookings) {
            if (booking.getStatus().equals("Pending")) {
                Object[] row = {
                    booking.getBookingID(),
                    booking.getDate(),
                    booking.getStatus(),
                    booking.getCustomerID(),
                    booking.getCarID(),
                    String.format("RM%.2f", booking.getPrice())
                };
                tableModel.addRow(row);
            }
        }

        // Show message if no bookings
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No pending bookings available.",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 