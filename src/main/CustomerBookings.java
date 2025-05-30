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
        

        String[] columnNames = {"ID", "Car Model", "Car Price", "Car Type", "Car Brand", "Car Category"};
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

        try (BufferedReader br = new BufferedReader(new FileReader("cars.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 7);
                if (parts.length < 7) continue;
                if (parts[6].equals("Booked")) {
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

        if (e.getSource() == pay) {
            String id = JOptionPane.showInputDialog(jframe, "Car ID to pay:");
            if (id == null || id.isBlank()) return;  

            try {

                Car car = Car.searchCar("cars.txt", id);
                if (car.getStatus().equals("Booked")) {
                    //add to sales.txt but the status got pending shud be after add to sales then considered paid dy
                    Payment.addPayment("payment.txt", Payment.getPaymentID(), car.getCarModel(), car.getCarPrice(), car.getCarType(), car.getCarBrand(), car.getCarCategory());
                    boolean ok = Car.updateStatus("payment.txt", id, "Pending");
                

                
                if (ok) {
                    JOptionPane.showMessageDialog(jframe,
                        "Booked CarID: " + id,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } 
                
                }
                else {
                    JOptionPane.showMessageDialog(jframe,
                        "CarID not available: " + id,
                        "Not Available",
                        JOptionPane.WARNING_MESSAGE);
                }
            }

            catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(jframe,
                    "Invalid ID format—no commas, quotes, or blanks allowed.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            }

            catch (IOException ex) {
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

