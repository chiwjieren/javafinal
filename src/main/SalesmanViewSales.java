import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SalesmanViewSales implements ActionListener {
    private final JFrame frame;
    private final JTable table;
    private final DefaultTableModel model;
    private final JButton btnBack, btnComment;
    private final SalesmanPage salesmanPage;

    public SalesmanViewSales(SalesmanPage salesmanPage) {
        this.salesmanPage = salesmanPage;
        frame = new JFrame("View Sales");
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(salesmanPage.getFrame());
        frame.setLayout(new BorderLayout(10,10));

        String[] cols = { "Sale ID", "Car ID", "Timestamp" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1,2,10,5));
        btnComment = new JButton("Add Comment");
        btnBack = new JButton("Back");

        btnComment.addActionListener(this);
        btnBack.addActionListener(this);

        btnPanel.add(btnComment);
        btnPanel.add(btnBack);
        frame.add(btnPanel, BorderLayout.SOUTH);

        refreshTable();
        frame.setVisible(true);
        salesmanPage.getFrame().setVisible(false);
    }

    private void refreshTable() {
        model.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader("salesmanhistory.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 4);
                if (parts.length < 4) continue;
                if (parts[1].equals(Main.currentSalesmanID)) {
                    model.addRow(new Object[]{
                        parts[0],  // Sale ID
                        parts[2],  // Car ID
                        parts[3]   // Timestamp
                    });
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error reading salesmanhistory.txt: " + ex.getMessage(),
                "I/O Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveComment(String saleID, String comment) {
        try {
            File commentFile = new File("salesmancomment.txt");
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(commentFile, true))) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                bw.write(saleID + "," + Main.currentSalesmanID + "," + comment + "," + timestamp + "\n");
            }
            
            JOptionPane.showMessageDialog(frame,
                "Comment saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error saving comment: " + ex.getMessage(),
                "I/O Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            frame.dispose();
            salesmanPage.getFrame().setVisible(true);
        }

        if (e.getSource() == btnComment) {
            String saleID = JOptionPane.showInputDialog(frame, "Enter Sale ID to comment:");
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

            String comment = JOptionPane.showInputDialog(frame, "Enter your comment:");
            if (comment == null || comment.isBlank()) return;

            saveComment(saleID, comment);
        }
    }
}
