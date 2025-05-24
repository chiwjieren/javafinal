import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AdminManageCar implements ActionListener {
    JFrame jframe;
    JTable jtable;
    DefaultTableModel tableModel;
    Button add, delete, search, update, back;
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

        try (BufferedReader br = new BufferedReader(new FileReader("cars.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 6);
                if (parts.length < 2) continue;
                tableModel.addRow(new Object[]{ parts[0], parts[1], parts[2], parts[3], parts[4], parts[5] });
            }
        } catch (IOException e) {}
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
    
}
