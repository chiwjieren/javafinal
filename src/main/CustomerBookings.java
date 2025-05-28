import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Button;

public class CustomerBookings {
    JFrame jframe;
    JTable jtable;
    DefaultTableModel tableModel;
    Button pay, cancel;
    CustomerPage customerPage;

    class tableModel extends DefaultTableModel{
        public boolean isCellEditable(int row, int column){
            return false;
        }
    }

    public CustomerBookings(CustomerPage customerPage){
        this.customerPage = customerPage;
        jframe = new JFrame();
        jframe.setTitle("Bookings");
        jframe.setSize(500,500);
        jframe.setLocation(500,200);

        String[] columnNames = {"ID", "Car Model", "Car Price", "Car Type", "Car Brand", "Car Category"};
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
    };
    jtable = new JTable(tableModel);

    jframe.add(new JScrollPane(jtable), BorderLayout.CENTER);

    JPanel panel = new JPanel(new GridLayout(1,5,5,5));
    pay = new Button("Pay");
    cancel = new Button("Cancel");

    pay.addActionListener(this);
    cancel.addActionListener(this);

    panel.add(pay);
    panel.add(cancel);

    jframe.add(panel, BorderLayout.SOUTH);

    refreshTable();

    jframe.setVisible(true);

    
    
    
}
