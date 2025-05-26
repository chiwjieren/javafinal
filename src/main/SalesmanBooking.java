import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

// window to show and manage available bookings
public class SalesmanBooking extends JFrame {
    private List<SalesmanBookingData> bookings;
    private List<SalesmanData> salesmen;
    private JTable table;
    private DefaultTableModel tableModel;

    public SalesmanBooking(List<SalesmanBookingData> bookings, List<SalesmanData> salesmen) {
        this.bookings = bookings;
        this.salesmen = salesmen;

        // setup the window
        setTitle("View Available Bookings");
        setSize(800, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);  // center on screen

        // create the table
        setupTable();
        
        // add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // add close button at bottom
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // load the data and show window
        loadData();
        setVisible(true);
    }

    private void setupTable() {
        // column names for the table
        String[] columnNames = {"Booking ID", "Car ID", "Customer ID", "Booking Date", "Status", "Action"};
        
        // create table model that only allows editing the Action column
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // only the Action column is editable
            }
        };

        // create and setup the table
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // add the button column
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void loadData() {
        tableModel.setRowCount(0);
        
        // get only available bookings that don't have a salesman assigned
        List<SalesmanBookingData> availableBookings = bookings.stream()
            .filter(b -> "Available".equals(b.getStatus()) && (b.getSalesmanID() == null || b.getSalesmanID().isEmpty()))
            .collect(Collectors.toList());

        // show message if no bookings available
        if (availableBookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available bookings to assign!", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // add each booking to the table
        for (SalesmanBookingData booking : availableBookings) {
            Object[] row = {
                booking.getBookingID(),
                booking.getCarID(),
                booking.getCustomerID(),
                booking.getDate(),
                booking.getStatus(),
                "Assign to Me"
            };
            tableModel.addRow(row);
        }
    }

    private void assignBooking(String bookingID) {
        // check if we have salesman data
        if (salesmen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No salesman data found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // find and update the booking
        for (SalesmanBookingData booking : bookings) {
            if (booking.getBookingID().equals(bookingID)) {
                booking.setSalesmanID(salesmen.get(0).getID());
                booking.setStatus("Booked");
                try {
                    // save changes to file
                    SalesmanBookingData.saveBookings("SalesmanBooking.txt", bookings);
                    loadData();  // refresh the table
                    JOptionPane.showMessageDialog(this, 
                        "Booking " + bookingID + " has been assigned to you!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error saving booking assignment: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            }
        }
    }

    // button renderer for the table
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // button editor for the table
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            this.row = row;
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                String bookingID = (String) table.getValueAt(row, 0);
                assignBooking(bookingID);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    // for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<SalesmanBookingData> bookings = SalesmanBookingData.readBookings("SalesmanBooking.txt");
            List<SalesmanData> salesmen = SalesmanData.loadSalesmen("Salesman.txt");
            new SalesmanBooking(bookings, salesmen);
        });
    }
} 