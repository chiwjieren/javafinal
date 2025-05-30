import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.JButton;

public class SalesmanViewComment implements ActionListener {
    private final JFrame frame;
    private final JTable table;
    private final DefaultTableModel model;
    private final JButton btnBack;
    private final SalesmanPage salesmanPage;

    public SalesmanViewComment(SalesmanPage salesmanPage) {
        this.salesmanPage = salesmanPage;
        frame = new JFrame("View Comments");
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(salesmanPage.getFrame());
        frame.setLayout(new BorderLayout(10,10));

        String[] cols = { "Sale ID", "Salesman ID", "Comment", "Timestamp" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnBack = new JButton("Back");
        btnBack.addActionListener(this);
        btnPanel.add(btnBack);
        frame.add(btnPanel, BorderLayout.SOUTH);

        refreshTable();
        frame.setVisible(true);
        salesmanPage.getFrame().setVisible(false);
    }

    private void refreshTable() {
        model.setRowCount(0);
        File commentFile = new File("salesmancomment.txt");
        
        if (!commentFile.exists()) {
            JOptionPane.showMessageDialog(frame,
                "No comments found.",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(commentFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 4);
                if (parts.length < 4) {
                    System.out.println("Invalid line format: " + line);
                    continue;
                }

                model.addRow(new Object[]{
                    parts[0].trim(),  
                    parts[1].trim(),  
                    parts[2].trim(),  
                    parts[3].trim()   
                });
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error reading salesmancomment.txt: " + ex.getMessage(),
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
    }
}
