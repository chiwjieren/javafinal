import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.JButton;

public class CustomerReview implements ActionListener {
    private final JFrame frame;
    private final JTable table;
    private final DefaultTableModel model;
    private final JButton btnBack;
    private final CustomerPage customerPage;

    public CustomerReview(CustomerPage customerPage) {
        this.customerPage = customerPage;
        frame = new JFrame("Customer Reviews");
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(customerPage.getFrame());
        frame.setLayout(new BorderLayout(10,10));

        String[] cols = { "Sale ID", "Customer ID", "Review", "Timestamp" };
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
        try (BufferedReader br = new BufferedReader(new FileReader("customerreview.txt"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",", 4);
                if (parts.length < 4) continue;
                
                model.addRow(new Object[]{
                    parts[0],  // Sale ID
                    parts[1],  // Customer ID
                    parts[2],  // Review
                    parts[3]   // Timestamp
                });
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error reading customerreview.txt: " + ex.getMessage(),
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
