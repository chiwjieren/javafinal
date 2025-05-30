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


public class CustomerViewAvailableCars implements ActionListener{
    JFrame jframe;
    JTable jtable;
    DefaultTableModel tableModel;
    Button back, book;
    CustomerPage customerPage;

    class tableModel extends DefaultTableModel{
        public boolean isCellEditable(int row, int column){
            return false;
        }
    }

    public CustomerViewAvailableCars(CustomerPage customerPage) {
        this.customerPage = customerPage;
        jframe = new JFrame();
        jframe.setTitle("View Available Cars");
        jframe.setSize(500,500);
        jframe.setLocation(500,200);
        

        String[] columnNames = {"ID", "Car Model", "Car Price", "Car Type", "Car Brand", "Car Category", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        jtable = new JTable(tableModel);
        

        
        jframe.add(new JScrollPane(jtable), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(1,5,5,5));
        back = new Button("Back");
        book = new Button("Book");

        back.addActionListener(this);
        book.addActionListener(this);

        panel.add(back);
        panel.add(book);

        jframe.add(panel, BorderLayout.SOUTH);

        refreshTable();

        jframe.setVisible(true);
    }
    

    private void refreshTable() {
        tableModel.setRowCount(0);

        try (BufferedReader br = new BufferedReader(new FileReader("cars.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 7);
                if (parts.length < 7) continue;
                if (parts[6].equals("Available")) {
                    tableModel.addRow(parts);
                }
            }
            
        } catch (IOException e) {}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == back) {
            jframe.setVisible(false);
            customerPage.jframe.setVisible(true);
        }

        if (e.getSource() == book) {
            String id = JOptionPane.showInputDialog(jframe, "Car ID to book:");
            if (id == null || id.isBlank()) return;
        
            try {
                Car car = Car.searchCar("cars.txt", id);
        
                if (car.getStatus().equalsIgnoreCase("Available")) {
                    String bookingID = Book.getNextBookingID("booking.txt");
                    Book.addBooking("booking.txt", bookingID, id, Main.currentCustomerID, "Booked", LocalDateTime.now());
                    boolean ok = Car.updateStatus("cars.txt", id, "Booked");
                    
                    try {
                        Payment.addPayment("payment.txt", Payment.getNextPaymentID("payment.txt"), Main.currentCustomerID, car.getCarID(), car.getCarPrice(), "Pending", LocalDateTime.now());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(jframe,
                            "Warning: Payment record could not be created: " + ex.getMessage(),
                            "Payment Warning",
                            JOptionPane.WARNING_MESSAGE);
                    }
        
                    if (ok) {
                        JOptionPane.showMessageDialog(jframe,
                            "Booked CarID: " + id + "\nBooking ID: " + bookingID + "\nAmount: RM" + car.getCarPrice(),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(jframe,
                            "Failed to update car status for: " + id,
                            "Update Error",
                            JOptionPane.WARNING_MESSAGE);
                    }
        
                } else {
                    JOptionPane.showMessageDialog(jframe,
                        "This car is not available for booking.",
                        "Not Available",
                        JOptionPane.WARNING_MESSAGE);
                }
        
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(jframe,
                    "Could not book car. Please try again.",
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        
            refreshTable();
        }
        
           
    }
        
}

