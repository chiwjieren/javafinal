import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
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
    Button search, delete, update, back;
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
        search = new Button("Search");
        update = new Button("Update");
        back   = new Button("Back");

        delete.addActionListener(this);
        search.addActionListener(this);
        update.addActionListener(this);
        back.addActionListener(this);

        panel.add(delete);
        panel.add(search);
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
        if (e.getSource() == search) {
            String id = JOptionPane.showInputDialog(jframe, "Enter Customer ID to search:");
            if (id == null || id.isBlank()) return;

            Customer existing = Customer.searchCustomer("customer.txt", id);

            if (existing == null) {
                JOptionPane.showMessageDialog(jframe,
                    "No record found for CustomerID: " + id,
                    "Not Found",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Customer Details:\n")
              .append("ID: ").append(id).append("\n")
              .append("Username: ").append(existing.getUsername()).append("\n\n")
              .append("Sales:\n");

            boolean anySale = false;
            try (BufferedReader br = new BufferedReader(new FileReader("sales.txt"))) {
                String line;

                while ((line = br.readLine()) != null) {
                    String[] f = line.split(",", 8);

                    if (f.length < 4) continue;

                    if (f[1].equals(id)) {  
                        sb.append("• SaleID: ").append(f[0])
                          .append(" | Salesman: ").append(f[1])
                          .append(" | Car: ").append(f[3])
                          .append(" | Date: ").append(f[4]);
                        anySale = true;
                    }
                }
            } 
            
            catch (IOException ioe) {
                ioe.printStackTrace();
                sb.append("\nError reading sales.txt: ").append(ioe.getMessage());
            }

            if (!anySale) {
                sb.append("  (No sales found for this customer.)");
            }

            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            area.setCaretPosition(0);
            JScrollPane sp = new JScrollPane(area);
            sp.setPreferredSize(new Dimension(450, 300));

            JOptionPane.showMessageDialog(jframe,
                sp,
                "Search Results for " + id,
                JOptionPane.INFORMATION_MESSAGE);

            return;
        }
        
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
