import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class CustomerViewHistory implements ActionListener {
    private final JFrame frame;
    private final JTable table;
    private final DefaultTableModel model;
    private final JButton btnBack;
    private final CustomerPage customerPage;

    public CustomerViewHistory(CustomerPage customerPage) {
        this.customerPage = customerPage;
        frame = new JFrame("Purchase History");
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(customerPage.getFrame());
        frame.setLayout(new BorderLayout(10,10));

        String[] cols = { "Sale ID", "Car ID", "Amount", "Timestamp" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnBack = new JButton("Back");
        btnBack.addActionListener(this);
        btnPanel.add(btnBack);
        frame.add(btnPanel, BorderLayout.SOUTH);

        refreshTable();
        frame.setVisible(true);
        customerPage.getFrame().setVisible(false);
    }

    private void refreshTable() {
        model.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader("customerhistory.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 5);
                if (parts.length < 5) continue;
                if (parts[1].equals(Main.currentCustomerID)) {
                    model.addRow(new Object[]{
                        parts[0],  // Sale ID
                        parts[2],  // Car ID
                        parts[3],  // Amount
                        parts[4]   // Timestamp
                    });
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error reading customerhistory.txt: " + ex.getMessage(),
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
    }
} 