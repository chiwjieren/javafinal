import java.awt.BorderLayout;
import java.awt.Button;
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
import javax.swing.table.DefaultTableModel;

public class AdminManageSalesman implements ActionListener{
    JFrame jframe;
    JTable jtable;
    DefaultTableModel tableModel;
    Button add, delete, search, update, back;
    AdminPage adminPage;

    public AdminManageSalesman(AdminPage adminPage) {
        this.adminPage = adminPage;

        jframe = new JFrame("All Salesman");
        jframe.setSize(500,500);
        jframe.setLocation(400,100);

        tableModel = new DefaultTableModel(new String[] {"ID", "Username"}, 0);

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

        if (e.getSource() == delete) {
            try {
                String id = JOptionPane.showInputDialog(jframe, "Salesman ID to delete: ");
                Salesman.delete("salesman.txt", id);

                JOptionPane.showMessageDialog(jframe,
                    "Deleted SalesmanID: " + id,
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
        
        if (e.getSource() == search) {

        }

        if (e.getSource() == update) {

        }

        if (e.getSource() == back) {

        }
    }
    
}
