import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class SalesmanCollectPayment extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String[] PAYMENT_METHODS = {"Touch n Go", "Cash", "Card", "Bank"};
    private String salesmanID;

    // This file has been renamed. Please use SalesmanCollectPayment.java instead.

    public SalesmanCollectPayment(List<SalesmanBookingData> bookings, String salesmanID) {
        this.salesmanID = salesmanID;
        setTitle("Collect Payment");
        setSize(800, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setupTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadData(bookings);
        setVisible(true);
    }

    private void setupTable() {
        String[] columnNames = {"Booking ID", "Car ID", "Customer ID", "Booking Date", "Status", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor());
    }

    private void loadData(List<SalesmanBookingData> bookings) {
        tableModel.setRowCount(0);
        for (SalesmanBookingData booking : bookings) {
            if ("Booked".equals(booking.getStatus()) && salesmanID.equals(booking.getSalesmanID())) {
                Object[] row = {
                    booking.getBookingID(),
                    booking.getCarID(),
                    booking.getCustomerID(),
                    booking.getBookingDate(),
                    booking.getStatus(),
                    "Collect Payment"
                };
                tableModel.addRow(row);
            }
        }
    }

    private void showPaymentMethodDialog(String bookingID) {
        String paymentMethod = (String) JOptionPane.showInputDialog(
            this,
            "Select payment method:",
            "Payment Method",
            JOptionPane.QUESTION_MESSAGE,
            null,
            PAYMENT_METHODS,
            PAYMENT_METHODS[0]
        );
        if (paymentMethod != null) {
            // Feedback dialog
            String feedback = JOptionPane.showInputDialog(
                this,
                "Please provide feedback for this booking:",
                "Feedback",
                JOptionPane.QUESTION_MESSAGE
            );
            if (feedback == null) feedback = "";
            // Rating dialog
            String[] ratings = {"1", "2", "3", "4", "5"};
            int ratingIndex = JOptionPane.showOptionDialog(
                this,
                "Please rate this booking (1-5):",
                "Rating",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                ratings,
                ratings[4]
            );
            String rating = (ratingIndex >= 0 && ratingIndex < ratings.length) ? ratings[ratingIndex] : "5";

            // Save payment to SalesmanPayment.txt
            try {
                String paymentID = getNextPaymentID("SalesmanPayment.txt");
                String currentDate = java.time.LocalDate.now().toString();
                String amount = "0.00";
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (bookingID.equals(tableModel.getValueAt(i, 0))) {
                        break;
                    }
                }
                String paymentLine = String.format("%s,%s,%s,%s,%s", paymentID, amount, paymentMethod, currentDate, bookingID);
                java.nio.file.Files.write(java.nio.file.Paths.get("SalesmanPayment.txt"), (paymentLine + System.lineSeparator()).getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);

                // Save feedback to SalesmanFeedback.txt
                String feedbackID = getNextFeedbackID("SalesmanFeedback.txt");
                String feedbackLine = String.format("%s,%s,%s,%s,%s", feedbackID, bookingID, salesmanID, feedback.replace(",", " "), rating);
                java.nio.file.Files.write(java.nio.file.Paths.get("SalesmanFeedback.txt"), (feedbackLine + System.lineSeparator()).getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);

                // Update booking status to Paid in SalesmanBooking.txt
                java.util.List<String> bookingLines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get("SalesmanBooking.txt"));
                for (int i = 0; i < bookingLines.size(); i++) {
                    String[] parts = bookingLines.get(i).split(",");
                    if (parts.length >= 8 && parts[0].equals(bookingID)) {
                        parts[2] = "Paid";   // status
                        parts[3] = "true";   // payment done
                        bookingLines.set(i, String.join(",", parts));
                        break;
                    }
                }
                java.nio.file.Files.write(java.nio.file.Paths.get("SalesmanBooking.txt"), bookingLines, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.CREATE);

                // Refresh table data after payment
                List<SalesmanBookingData> updatedBookings = SalesmanBookingData.readAllBookings();
                loadData(updatedBookings);

                JOptionPane.showMessageDialog(this, "Payment and feedback saved!\nPayment: " + paymentLine + "\nFeedback: " + feedbackLine);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving payment/feedback: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper to generate next payment ID (P0001, P0002, ...)
    private String getNextPaymentID(String fileName) {
        try {
            java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(fileName));
            int max = 0;
            for (String line : lines) {
                if (line.startsWith("P")) {
                    String num = line.substring(1, 5);
                    try {
                        int n = Integer.parseInt(num);
                        if (n > max) max = n;
                    } catch (NumberFormatException ignored) {}
                }
            }
            return String.format("P%04d", max + 1);
        } catch (Exception e) {
            return "P0001";
        }
    }

    // Helper to generate next feedback ID (F0001, F0002, ...)
    private String getNextFeedbackID(String fileName) {
        try {
            java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(fileName));
            int max = 0;
            for (String line : lines) {
                if (line.startsWith("F")) {
                    String num = line.substring(1, 5);
                    try {
                        int n = Integer.parseInt(num);
                        if (n > max) max = n;
                    } catch (NumberFormatException ignored) {}
                }
            }
            return String.format("F%04d", max + 1);
        } catch (Exception e) {
            return "F0001";
        }
    }

    // Update booking status to Paid in SalesmanBooking.txt
    private void updateBookingStatusToPaid(String bookingID) {
        try {
            java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get("SalesmanBooking.txt"));
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length > 0 && parts[0].equals(bookingID)) {
                    // Assume status is at index 4 (Booking ID, Car ID, Customer ID, Booking Date, Status, ...)
                    if (parts.length > 4) {
                        parts[4] = "Paid";
                        lines.set(i, String.join(",", parts));
                    }
                }
            }
            java.nio.file.Files.write(java.nio.file.Paths.get("SalesmanBooking.txt"), lines);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating booking status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Simple button renderer for Action column
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Interactive button editor for Action column
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String bookingID;
        private boolean isPushed;

        public ButtonEditor() {
            super(new JTextField());
            setClickCountToStart(1); // Activate on single click
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            bookingID = (String) table.getValueAt(row, 0);
            button.setText((value == null) ? "" : value.toString());
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                showPaymentMethodDialog(bookingID);
            }
            isPushed = false;
            return button.getText();
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
