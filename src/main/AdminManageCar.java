import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AdminManageCar implements ActionListener {
    JFrame jframe;
    JTable jtable;
    DefaultTableModel tableModel;
    Button add, delete, update, back;
    AdminPage adminPage;

    public AdminManageCar(AdminPage adminPage) {
        this.adminPage = adminPage;

        jframe = new JFrame("All Cars");
        jframe.setSize(500,500);
        jframe.setLocation(400,100);

        tableModel = new DefaultTableModel(new String[] {"ID", "Car Model", "Car Price", "Car Type", "Car Brand", "Car Category"}, 0) {
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
        update = new Button("Update");
        back   = new Button("Back");

        add.addActionListener(this);
        delete.addActionListener(this);
        update.addActionListener(this);
        back.addActionListener(this);

        panel.add(add);
        panel.add(delete);
        panel.add(update);
        panel.add(back);

        jframe.add(panel, BorderLayout.SOUTH);
        
        refreshTable();

        jframe.setVisible(true);

    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        try (BufferedReader br = new BufferedReader(new FileReader("cars.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 6);
                if (parts.length < 6) continue;
                tableModel.addRow(new Object[]{ parts[0], parts[1], parts[2], parts[3], parts[4], parts[5] });
            }
        } catch (IOException e) {}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add) {
            try {
                String id = Car.getNextCarID("cars.txt");
                String carModel = JOptionPane.showInputDialog(jframe, "Enter Car Model:");
                String priceStr = JOptionPane.showInputDialog(jframe, "Enter Car Price:");
                int carPrice;

                try {
                    carPrice = Integer.parseInt(priceStr);
                } 
                
                catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(jframe,
                        "Car Price must be a number.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String carType = JOptionPane.showInputDialog(jframe, "Enter Car Type:");
                String carBrand = JOptionPane.showInputDialog(jframe, "Enter Car Brand:");

                String[] categories = { "Luxury", "Affordable" };
                String carCategory = (String) JOptionPane.showInputDialog(jframe, "Select Car Category:", "Category", JOptionPane.QUESTION_MESSAGE, null, categories, categories[0]);

                if (!Car.cleanInput(carModel) || !Car.cleanInput(carType) || !Car.cleanInput(carBrand) || !Car.cleanInput(carCategory)) {
                    throw new IllegalArgumentException("Invalid Input");
                }

                Car.addCar("cars.txt", id, carModel, carPrice, carType, carBrand, carCategory);

                JOptionPane.showMessageDialog(jframe,
                    "Car added!\nCarID is " + id,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }

            catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    jframe,
                    "Please don’t use commas, quotes, line breaks or leave blanks in any field.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
                );
            }

            catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    jframe,
                    "Could not save car. Please try again.",
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }

            refreshTable();
        }

        if (e.getSource() == delete) {
            String id = JOptionPane.showInputDialog(jframe, "Car ID to delete:");
            if (id == null || id.isBlank()) return;  

            try {
                boolean ok = Car.delete("cars.txt", id);
                
                if (ok) {
                    JOptionPane.showMessageDialog(jframe,
                        "Deleted CarID: " + id,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } 
                
                else {
                    JOptionPane.showMessageDialog(jframe,
                        "No record found for CarID: " + id,
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
                    "Could not delete car. Please try again.",
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE);
            }

            refreshTable();
        }

        if (e.getSource() == update) {
            
        }

        if (e.getSource() == back) {
            jframe.dispose();
            adminPage.jframe.setVisible(true);          
        }

    }
    
}
