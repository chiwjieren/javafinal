import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JButton;

public class CustomerViewAffordableCars implements ActionListener {
    private final JFrame frame;
    private final JTable table;
    private final DefaultTableModel model;
    private final JButton btnBack, btnBook;
    private final CustomerPage customerPage;

    public CustomerViewAffordableCars(CustomerPage customerPage) {
        this.customerPage = customerPage;
        frame = new JFrame("Affordable Cars");
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(customerPage.getFrame());
        frame.setLayout(new BorderLayout(10,10));

        String[] cols = { "ID", "Model", "Price", "Type", "Brand", "Category", "Status" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1,2,10,5));
        btnBook = new JButton("Book");
        btnBack = new JButton("Back");

        btnBook.addActionListener(this);
        btnBack.addActionListener(this);

        btnPanel.add(btnBook);
        btnPanel.add(btnBack);
        frame.add(btnPanel, BorderLayout.SOUTH);

        refreshTable();
        frame.setVisible(true);
        customerPage.getFrame().setVisible(false);
    }

    private void refreshTable() {
        model.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader("cars.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 7);
                if (parts.length < 7) continue;
                if (parts[5].equals("Affordable") && parts[6].equals("Available")) {
                    model.addRow(parts);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error reading cars.txt: " + ex.getMessage(),
                "I/O Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            frame.dispose();
            customerPage.getFrame().setVisible(true);
        }

        if (e.getSource() == btnBook) {
            String carID = JOptionPane.showInputDialog(frame, "Enter Car ID to book:");
            if (carID == null || carID.isBlank()) return;

            try {
                Car car = Car.searchCar("cars.txt", carID);
                if (car != null && car.getStatus().equals("Available")) {
                    String bookingID = Book.getNextBookingID("booking.txt");
                    Book.addBooking("booking.txt", bookingID, carID, Main.currentCustomerID, "Booked", LocalDateTime.now());
                    boolean ok = Car.updateStatus("cars.txt", carID, "Booked");

                    if (ok) {
                        JOptionPane.showMessageDialog(frame,
                            "Booked CarID: " + carID + "\nBooking ID: " + bookingID + "\nAmount: RM" + car.getCarPrice(),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(frame,
                            "Failed to update car status.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame,
                        "This car is no longer available.",
                        "Not Available",
                        JOptionPane.WARNING_MESSAGE);
                    refreshTable();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                    "Error processing booking: " + ex.getMessage(),
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
