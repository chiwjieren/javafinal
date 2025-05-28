import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SalesmanManageCar implements ActionListener {
    private final JFrame frame;
    private final JTable table;
    private final DefaultTableModel model;
    private final JButton btnBook, btnMarkPaid, btnAvailable, btnBack;

    public SalesmanManageCar(SalesmanPage parent) {
        frame = new JFrame("Manage Cars");
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(parent.getFrame());
        frame.setLayout(new BorderLayout(10,10));

        String[] cols = { "CarID", "Model", "Price", "Type", "Brand", "Category", "Status" };

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1,4,10,5));
        btnBook     = new JButton("Mark Booked");
        btnMarkPaid = new JButton("Mark Paid");
        btnAvailable= new JButton("Mark Available");
        btnBack     = new JButton("Back");

        btnBook.addActionListener(this);
        btnMarkPaid.addActionListener(this);
        btnAvailable.addActionListener(this);
        btnBack.addActionListener(e -> {
            frame.dispose();
            parent.getFrame().setVisible(true);
        });

        btnPanel.add(btnBook);
        btnPanel.add(btnMarkPaid);
        btnPanel.add(btnAvailable);
        btnPanel.add(btnBack);
        frame.add(btnPanel, BorderLayout.SOUTH);

        refreshTable();

        frame.setVisible(true);
        parent.getFrame().setVisible(false);
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
                model.addRow(parts);
            }
        } 
        
        catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error reading cars.txt: " + ex.getMessage(),
                "I/O Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(frame,
                "Please select a car first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String carID   = (String) model.getValueAt(row, 0);
        String newStatus;
        if (e.getSource() == btnBook)      newStatus = "Booked";
        else if (e.getSource() == btnMarkPaid) newStatus = "Paid";
        else if (e.getSource() == btnAvailable) newStatus = "Available";
        else return;

        try {
            boolean updated = Car.updateStatus("cars.txt", carID, newStatus);

            if (updated) {
                JOptionPane.showMessageDialog(frame,
                    "Car " + carID + " marked “" + newStatus + "”.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
            
            else {
                JOptionPane.showMessageDialog(frame,
                    "Could not find car " + carID + ".",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } 
        
        catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "I/O error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
