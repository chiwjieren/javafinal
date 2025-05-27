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

        tableModel = new DefaultTableModel(new String[] {"ID", "Car Model", "Car Price", "Car Type", "Car Brand", "Car Category", "Status"}, 0) {
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
                String[] parts = line.split(",", 7);
                if (parts.length < 7) continue;
                tableModel.addRow(new Object[]{ parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6] });
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

            String id = JOptionPane.showInputDialog(jframe, "Enter Car ID to update:");

            if (id == null || id.isBlank()) return;

            Car existing;

            try {
                existing = Car.searchCar("cars.txt", id);
            } 
            
            catch (IOException ioe) {
                JOptionPane.showMessageDialog(jframe,
                    "I/O error reading car data:\n" + ioe.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (existing == null) {
                JOptionPane.showMessageDialog(jframe,
                    "No record found for CarID: " + id,
                    "Not Found",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String newModel = JOptionPane.showInputDialog(
                jframe, 
                "New model:", 
                existing.getCarModel()
            );

            if (newModel == null || newModel.isBlank()) return;

            String priceStr = JOptionPane.showInputDialog(
                jframe,
                "New price:",
                Integer.toString(existing.getCarPrice())
            );

            if (priceStr == null || priceStr.isBlank()) return;
            
            int newPrice;

            try {
                newPrice = Integer.parseInt(priceStr);
            } 
            
            catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(jframe,
                    "Price must be a number.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String newType = JOptionPane.showInputDialog(
                jframe,
                "New type:",
                existing.getCarType()
            );

            if (newType == null || newType.isBlank()) return;

            String newBrand = JOptionPane.showInputDialog(
                jframe,
                "New brand:",
                existing.getCarBrand()
            );

            if (newBrand == null || newBrand.isBlank()) return;

            String[] categories = { "Luxury", "Affordable" };

            String newCategory = (String) JOptionPane.showInputDialog(
                jframe,
                "New category:",
                "Category",
                JOptionPane.QUESTION_MESSAGE,
                null,
                categories,
                existing.getCarCategory()
            );

            if (newCategory == null) return;

            String[] statuses = { "Available", "Rented", "Maintenance" };

            String newStatus = (String) JOptionPane.showInputDialog(
                jframe,
                "New status:",
                "Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statuses,
                existing.getStatus()
            );

            if (newStatus == null) return;

            if (!Car.cleanInput(newModel) || !Car.cleanInput(newType) || !Car.cleanInput(newBrand) || !Car.cleanInput(newCategory) || !Car.cleanInput(newStatus)) {
                JOptionPane.showMessageDialog(jframe,
                    "Invalid input: no commas, quotes, line breaks, or blanks allowed.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                boolean ok = Car.update( "cars.txt", id, newModel, newPrice, newType, newBrand, newCategory, newStatus);

                if (ok) {
                    JOptionPane.showMessageDialog(jframe,
                        "Successfully updated CarID: " + id,
                        "Updated",
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshTable();
                } 
                
                else {
                    JOptionPane.showMessageDialog(jframe,
                        "Update failed: no such ID.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }

            } 
            
            catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(jframe,
                    iae.getMessage(),
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            } 
            
            catch (IOException ioe) {
                JOptionPane.showMessageDialog(jframe,
                    "I/O error updating car:\n" + ioe.getMessage(),
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
