import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.awt.event.ActionEvent;

public class CustomerBookings implements ActionListener{
    JFrame jframe;
    JTable jtable;
    DefaultTableModel tableModel;
    Button pay, back, cancel;
    CustomerPage customerPage;

    class tableModel extends DefaultTableModel{
        public boolean isCellEditable(int row, int column){
            return false;
        }
    }

    public CustomerBookings(CustomerPage customerPage) {
        this.customerPage = customerPage;
        jframe = new JFrame();
        jframe.setTitle("View Bookings");
        jframe.setSize(500,500);
        jframe.setLocation(500,200);
        
        String[] columnNames = {"ID", "Car ID", "Car Price","Status"};
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        jtable = new JTable(tableModel);
        
        jframe.add(new JScrollPane(jtable), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(1,5,5,5));
        pay = new Button("Pay");
        back = new Button("Back");
        cancel = new Button("Cancel Booking");

        pay.addActionListener(this);
        back.addActionListener(this);
        cancel.addActionListener(this);

        panel.add(pay);
        panel.add(back);
        panel.add(cancel);

        jframe.add(panel, BorderLayout.SOUTH);

        refreshTable();

        jframe.setVisible(true);
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);

        try (BufferedReader br = new BufferedReader(new FileReader("booking.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 5);
                if (parts.length < 5) continue;
                
                if (parts[2].equals(Main.currentCustomerID) && 
                    (parts[3].equals("Booked") || parts[3].equals("Pending"))) {
                    try {
                        Car car = Car.searchCar("cars.txt", parts[1]);
                        if (car != null) {
                            Object[] row = {
                                parts[0],
                                parts[1],
                                "RM" + car.getCarPrice(),
                                parts[3]
                            };
                            tableModel.addRow(row);
                        }
                    } catch (IOException ex) {
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(jframe,
                "Could not load bookings: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private BookingInfo findBooking(String bookingID) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("booking.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 5);
                if (parts.length >= 5 && parts[0].equals(bookingID) && 
                    parts[2].equals(Main.currentCustomerID) && 
                    parts[3].equals("Booked")) {
                    return new BookingInfo(parts[1], line);
                }
            }
        }
        return null;
    }

    private static class BookingInfo {
        final String carID;
        final String bookingLine;

        BookingInfo(String carID, String bookingLine) {
            this.carID = carID;
            this.bookingLine = bookingLine;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == back) {
            jframe.setVisible(false);
            customerPage.jframe.setVisible(true);
        }

        if (e.getSource() == pay) {
            String bookingID = JOptionPane.showInputDialog(jframe,
                "Enter Booking ID:",
                "Payment",
                JOptionPane.QUESTION_MESSAGE);
                
            if (bookingID == null || bookingID.trim().isEmpty()) {
                return;
            }

            try {
                BookingInfo booking = findBooking(bookingID);
                if (booking == null) {
                    JOptionPane.showMessageDialog(jframe,
                        "Invalid booking ID or booking not found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Car car = Car.searchCar("cars.txt", booking.carID);
                if (car != null) {
                    try {
                        Payment.addPayment("payment.txt", 
                            Payment.getNextPaymentID("payment.txt"), 
                            Main.currentCustomerID,
                            booking.carID,
                            car.getCarPrice(), 
                            "Pending", 
                            LocalDateTime.now());
                        
                        String[] parts = booking.bookingLine.split(",", 5);
                        String newBookingLine = parts[0] + "," + parts[1] + "," + parts[2] + ",Pending," + parts[4];
                        
                        java.util.List<String> lines = new java.util.ArrayList<>();
                        try (BufferedReader br = new BufferedReader(new FileReader("booking.txt"))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                if (line.equals(booking.bookingLine)) {
                                    lines.add(newBookingLine);
                                } else {
                                    lines.add(line);
                                }
                            }
                        }
                        
                        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter("booking.txt"))) {
                            for (String line : lines) {
                                writer.println(line);
                            }
                        }
                        
                        JOptionPane.showMessageDialog(jframe,
                            "Payment initiated for booking " + bookingID + "\nAmount: RM" + car.getCarPrice(),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        refreshTable();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(jframe,
                            "Could not create payment: " + ex.getMessage(),
                            "Payment Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(jframe,
                        "Could not find car details.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(jframe,
                    "Could not process payment. Please try again.",
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == cancel) {
            String bookingID = JOptionPane.showInputDialog(jframe,
                "Enter Booking ID:",
                "Cancel Booking",
                JOptionPane.QUESTION_MESSAGE);
                
            if (bookingID == null || bookingID.trim().isEmpty()) {
                return;
            }

            try {
                BookingInfo booking = findBooking(bookingID);
                if (booking == null) {
                    JOptionPane.showMessageDialog(jframe,
                        "Invalid booking ID or booking not found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean ok = Car.updateStatus("cars.txt", booking.carID, "Available");
                if (ok) {
                    java.util.List<String> lines = new java.util.ArrayList<>();
                    try (BufferedReader br = new BufferedReader(new FileReader("booking.txt"))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (!line.equals(booking.bookingLine)) {
                                lines.add(line);
                            }
                        }
                    }
                    
                    try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter("booking.txt"))) {
                        for (String line : lines) {
                            writer.println(line);
                        }
                    }

                    JOptionPane.showMessageDialog(jframe,
                        "Booking " + bookingID + " has been cancelled.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshTable();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(jframe,
                    "Could not cancel booking. Please try again.",
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

