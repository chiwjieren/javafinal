import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JButton;

public class CustomerViewHistory implements ActionListener {
    private final JFrame frame;
    private final JTable table;
    private final DefaultTableModel model;
    private final JButton btnBack, btnReview;
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

        JPanel btnPanel = new JPanel(new GridLayout(1,2,10,5));
        btnReview = new JButton("Add Review");
        btnBack = new JButton("Back");

        btnReview.addActionListener(this);
        btnBack.addActionListener(this);

        btnPanel.add(btnReview);
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

    private void saveReview(String saleID, String review) {
        try {
            File reviewFile = new File("customerreview.txt");
            
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(reviewFile, true))) {
                
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                bw.write(saleID + "," + Main.currentCustomerID + "," + review + "," + timestamp + "\n");
            }
            
            JOptionPane.showMessageDialog(frame,
                "Review saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error saving review: " + ex.getMessage(),
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

        if (e.getSource() == btnReview) {
            String saleID = JOptionPane.showInputDialog(frame, "Enter Sale ID to review:");
            if (saleID == null || saleID.isBlank()) return;

            boolean saleFound = false;
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).equals(saleID)) {
                    saleFound = true;
                    break;
                }
            }

            if (!saleFound) {
                JOptionPane.showMessageDialog(frame,
                    "Invalid Sale ID. Please enter a valid Sale ID from your history.",
                    "Invalid Sale ID",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String review = JOptionPane.showInputDialog(frame, "Enter your review:");
            if (review == null || review.isBlank()) return;

            saveReview(saleID, review);
        }
    }
} 