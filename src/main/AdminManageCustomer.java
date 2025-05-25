import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AdminManageCustomer implements ActionListener{
    JFrame jframe;
    JTable jtable;
    DefaultTableModel tableModel;
    Button delete, update, back;
    AdminPage adminPage;

    public AdminManageCustomer(AdminPage adminPage) {
        this.adminPage = adminPage;
        
        jframe = new JFrame("All Customers");
        jframe.setSize(500,500);
        jframe.setLocation(400,100);

        tableModel = new DefaultTableModel(new String[] {"ID", "Username"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        jtable = new JTable(tableModel);
        jframe.add(new JScrollPane(jtable), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(1,5,5,5));
        delete = new Button("Delete");
        update = new Button("Update");
        back   = new Button("Back");

        delete.addActionListener(this);
        update.addActionListener(this);
        back.addActionListener(this);

        panel.add(delete);
        panel.add(update);
        panel.add(back);

        jframe.add(panel, BorderLayout.SOUTH);
        
        refreshTable();

        jframe.setVisible(true);

    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        try (BufferedReader br = new BufferedReader(new FileReader("customer.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 3);
                if (parts.length < 2) continue;
                tableModel.addRow(new Object[]{ parts[0], parts[1] });
            }
        } catch (IOException e) {}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == delete) {
            String id = JOptionPane.showInputDialog(jframe, "Customer ID to delete:");
            if (id == null || id.isBlank()) return;  

            try {
                boolean ok = Customer.delete("customer.txt", id);
                
                if (ok) {
                    JOptionPane.showMessageDialog(jframe,
                        "Deleted CustomerID: " + id,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } 
                
                else {
                    JOptionPane.showMessageDialog(jframe,
                        "No record found for CustomerID: " + id,
                        "Not Found",
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
                    "Could not delete customer. Please try again.",
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE);
            }

            refreshTable();
        }
        
        if (e.getSource() == update) {
            String id = JOptionPane.showInputDialog(jframe, "Enter Customer ID to update:");
            if (id == null || id.isBlank()) return;

            Customer existing = Customer.searchCustomer("customer.txt", id);

            if (existing == null) {
                JOptionPane.showMessageDialog(jframe,
                    "No record found for ID " + id,
                    "Not Found",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String newUser = JOptionPane.showInputDialog(
                jframe,
                "New username:",
                existing.getUsername()
            );

            if (newUser == null || newUser.isBlank()) return;

            String newPass = JOptionPane.showInputDialog(
                jframe,
                "New password:",
                existing.getPassword()
            );

            if (newPass == null || newPass.isBlank()) return;

            try {
                boolean ok = Customer.update("customer.txt", id, newUser, newPass);
                if (ok) {
                    JOptionPane.showMessageDialog(jframe,
                        "Successfully updated " + id,
                        "Updated",
                        JOptionPane.INFORMATION_MESSAGE);
                        
                    refreshTable();
                } 
                
                else {
                    JOptionPane.showMessageDialog(jframe,
                        "Update failed; ID not found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } 
            
            catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(jframe,
                    "I/O error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == back) {
            jframe.dispose();
            adminPage.jframe.setVisible(true);
        }
    }
}
