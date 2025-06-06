import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public class AdminManageSalesman implements ActionListener{
    JFrame jframe;
    JTable jtable;
    DefaultTableModel tableModel;
    Button add, delete, search, update, back;
    AdminPage adminPage;

    public AdminManageSalesman(AdminPage adminPage) {
        this.adminPage = adminPage;

        jframe = new JFrame("All Salesmans");
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
        add = new Button("Add");
        delete = new Button("Delete");
        search = new Button("Search");
        update = new Button("Update");
        back   = new Button("Back");

        add.addActionListener(this);
        delete.addActionListener(this);
        search.addActionListener(this);
        update.addActionListener(this);
        back.addActionListener(this);

        panel.add(add);
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

        try (BufferedReader br = new BufferedReader(new FileReader("salesman.txt"))) {
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
        if (e.getSource() == add) {
            try {
                String id = Salesman.getNextSalesmanID("salesman.txt");
                String username = JOptionPane.showInputDialog(jframe, "Enter salesman username: ");
                String password = JOptionPane.showInputDialog(jframe,"Enter password: ");

                ArrayList<String> newSalesman = new ArrayList<>();
                newSalesman.add(id);
                newSalesman.add(username);
                newSalesman.add(password);

                Salesman.register("salesman.txt", newSalesman);

                JOptionPane.showMessageDialog(jframe,
                    "Registration Successful!\nSalesmanID is " + id,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );

            } 
            
            catch (IllegalArgumentException ex) {
                
                JOptionPane.showMessageDialog(
                    jframe,
                    "Please don’t use commas, quotes, line breaks or leave blanks in username/password.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
                );
            }

            catch (IOException ex) {

                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    jframe,
                    "Could not save registration. Please try again.",
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }

            refreshTable();
            
        }

        else if (e.getSource() == search) {
            String id = JOptionPane.showInputDialog(jframe, "Enter Salesman ID to search:");
            if (id == null || id.isBlank()) return;

            Salesman existing = Salesman.searchSalesman("salesman.txt", id);
            if (existing == null) {
                JOptionPane.showMessageDialog(jframe,
                    "No record found for SalesmanID: " + id,
                    "Not Found",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Salesman Details:\n")
            .append("ID: ").append(id).append("\n")
            .append("Username: ").append(existing.getUsername()).append("\n")
            .append("Password: ").append(existing.getPassword()).append("\n\n")
            .append("Sales:\n");

            try (BufferedReader br = new BufferedReader(new FileReader("sales.txt"))) {
                String line;
                boolean anySale = false;
                while ((line = br.readLine()) != null) {
                    String[] f = line.split(",", 8);
                    if (f.length < 3) continue;
                    if (f[2].equals(id)) {
                        sb.append("• SaleID: ").append(f[0])
                        .append(" | Customer: ").append(f[1])
                        .append(" | Car: ").append(f[3])
                        .append(" | Date: ").append(f[4]);
                        anySale = true;
                    }
                }
                if (!anySale) {
                    sb.append("  (No sales found for this salesman.)");
                }
            } 

            catch (IOException ioe) {
                ioe.printStackTrace();
                sb.append("\n\nError reading sales.txt: ").append(ioe.getMessage());
            }

            JTextArea ta = new JTextArea(sb.toString());
            ta.setEditable(false);
            ta.setCaretPosition(0);
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(450, 300));

            JOptionPane.showMessageDialog(jframe,
                sp,
                "Search Results for " + id,
                JOptionPane.INFORMATION_MESSAGE);
        }

        if (e.getSource() == delete) {
            String id = JOptionPane.showInputDialog(jframe, "Salesman ID to delete:");
            if (id == null || id.isBlank()) return;  

            try {
                boolean ok = Salesman.delete("salesman.txt", id);
                
                if (ok) {
                    JOptionPane.showMessageDialog(jframe,
                        "Deleted SalesmanID: " + id,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } 
                
                else {
                    JOptionPane.showMessageDialog(jframe,
                        "No record found for SalesmanID: " + id,
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
                    "Could not delete salesman. Please try again.",
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE);
            }

            refreshTable();
        }
        
        if (e.getSource() == update) {
            String id = JOptionPane.showInputDialog(jframe, "Enter Salesman ID to update:");
            if (id == null || id.isBlank()) return;

            Salesman existing = Salesman.searchSalesman("salesman.txt", id);

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
                boolean ok = Salesman.update("salesman.txt", id, newUser, newPass);
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
