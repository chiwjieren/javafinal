import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.awt.event.ActionEvent;


public class CustomerViewAvailableCars implements ActionListener{
    JFrame jframe;
    JTable jtable;
    DefaultTableModel tableModel;
    Button back, details, book;
    CustomerPage customerPage;

    class tableModel extends DefaultTableModel{
        public boolean isCellEditable(int row, int column){
            return false;
        }
    }

    public CustomerViewAvailableCars(CustomerPage customerPage) {
        this.customerPage = customerPage;
        jframe = new JFrame();
        jframe.setTitle("View Available Cars");
        jframe.setSize(500,500);
        jframe.setLocation(500,200);
        

        String[] columnNames = {"ID", "Car Model", "Car Price", "Car Type", "Car Brand", "Car Category"};
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        jtable = new JTable(tableModel);
        

        
        jframe.add(new JScrollPane(jtable), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(1,5,5,5));
        back = new Button("Back");
        details = new Button("Details");
        book = new Button("Book");

        back.addActionListener(this);
        details.addActionListener(this);
        book.addActionListener(this);

        panel.add(back);
        panel.add(details);
        panel.add(book);

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
                tableModel.addRow(parts);
            }
            
        } catch (IOException e) {}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == back) {
            jframe.setVisible(false);
            customerPage.jframe.setVisible(true);
        }
        
        if (e.getSource() == details) {
           // String carID = jtable.getValueAt(jtable.getSelectedRow(), 0).toString();
           // CustomerViewCarDetails viewCarDetails = new CustomerViewCarDetails(this, carID);
            
        }

        if (e.getSource() == book) {
           
        }
        
    }

}