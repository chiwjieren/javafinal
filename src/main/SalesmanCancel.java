import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

// window to handle booking cancellations
public class SalesmanCancel extends JFrame {
    private List<SalesmanCar> cars;
    private List<SalesmanBookingData> bookings;
    private String salesmanID;
    private JTable table;
    private DefaultTableModel tableModel;
    private Timer statusTimer;  // timer to check for expired cancellations

    public SalesmanCancel(List<SalesmanCar> cars, List<SalesmanBookingData> bookings, String salesmanID) {
        this.cars = cars;
        this.bookings = bookings;
        this.salesmanID = salesmanID;

        // setup the window
        setTitle("Cancel Booking");
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

        // start timer to check for expired cancellations
        startStatusTimer();

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

    private void startStatusTimer() {
        // create timer to check for expired cancellations every minute
        statusTimer = new Timer(true);
        statusTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateCancelledBookings();
            }
        }, 0, 60000); // check every minute
    }

    private void loadData() {
        tableModel.setRowCount(0);
        
        // show only bookings for this salesman that are in "Booked" status
        for (SalesmanBookingData booking : bookings) {
            if (salesmanID.equals(booking.getSalesmanID()) && "Booked".equals(booking.getStatus())) {
                Object[] row = {
                    booking.getBookingID(),
                    booking.getCarID(),
                    booking.getCustomerID(),
                    booking.getBookingDate(),
                    booking.getStatus(),
                    "Cancel Booking"
                };
                tableModel.addRow(row);
            }
        }

        // show message if no bookings to cancel
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No booked cars to cancel!", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateCancelledBookings() {
        long currentTime = System.currentTimeMillis();
        boolean changesMade = false;

        // check each cancelled booking
        for (SalesmanBookingData booking : bookings) {
            if ("Cancelled".equals(booking.getStatus())) {
                // if booking was cancelled more than 24 hours ago, make it available again
                long bookingTime = booking.getLastStatusChangeTime();
                if (currentTime - bookingTime > 24 * 60 * 60 * 1000) { // 24 hours in milliseconds
                    booking.setStatus("Available");
                    booking.setSalesmanID(null); // remove salesman assignment
                    changesMade = true;
                }
            }
        }

        // save changes if any were made
        if (changesMade) {
            try {
                SalesmanBookingData.saveBookings("SalesmanBooking.txt", bookings);
                SwingUtilities.invokeLater(this::loadData);
            } catch (Exception e) {
                System.err.println("Error updating cancelled bookings: " + e.getMessage());
            }
        }
    }

    private void cancelBooking(String bookingID) {
        // find and update the booking
        for (SalesmanBookingData booking : bookings) {
            if (booking.getBookingID().equals(bookingID)) {
                booking.setStatus("Cancelled");
                booking.setLastStatusChangeTime(System.currentTimeMillis());
                try {
                    // save changes to file
                    SalesmanBookingData.saveBookings("SalesmanBooking.txt", bookings);
                    loadData();  // refresh the table
                    JOptionPane.showMessageDialog(this, 
                        "Booking " + bookingID + " has been cancelled. It will be available again in 24 hours.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error saving booking status: " + e.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
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
                cancelBooking(bookingID);
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

} 