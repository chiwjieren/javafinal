import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class SalesmanCollectPayment implements ActionListener {
    private final JFrame frame;
    private final JTable table;
    private final DefaultTableModel model;
    private final JButton btnCollect, btnBack;

    public SalesmanCollectPayment(SalesmanPage salesmanPage) {
        frame = new JFrame("Collect Payment");
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(salesmanPage.getFrame());
        frame.setLayout(new BorderLayout(10,10));

        String[] cols = { "Payment ID", "Customer ID", "Car ID", "Amount", "Status", "Timestamp" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1,2,10,5));
        btnCollect = new JButton("Collect Payment");
        btnBack = new JButton("Back");

        btnCollect.addActionListener(this);
        btnBack.addActionListener(e -> {
            frame.dispose();
            salesmanPage.getFrame().setVisible(true);
        });

        btnPanel.add(btnCollect);
        btnPanel.add(btnBack);
        frame.add(btnPanel, BorderLayout.SOUTH);

        refreshTable();
        frame.setVisible(true);
        salesmanPage.getFrame().setVisible(false);
    }

    private void refreshTable() {
        model.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader("payment.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 6);
                if (parts.length < 6) continue;
                if (parts[5].equals("Pending")) {
                    model.addRow(parts);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error reading payment.txt: " + ex.getMessage(),
                "I/O Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCollect) {
            String paymentID = JOptionPane.showInputDialog(frame,
                "Enter Payment ID:",
                "Collect Payment",
                JOptionPane.QUESTION_MESSAGE);
                
            if (paymentID == null || paymentID.trim().isEmpty()) {
                return;
            }

            try {
                String customerID = null;
                String carID = null;
                String amount = null;
                boolean found = false;

                try (BufferedReader br = new BufferedReader(new FileReader("payment.txt"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith(paymentID + ",")) {
                            String[] parts = line.split(",", 6);
                            if (parts.length >= 6 && parts[5].equals("Pending")) {
                                customerID = parts[1];
                                carID = parts[2];
                                amount = parts[3];
                                found = true;
                                break;
                            }
                        }
                    }
                }

                if (!found) {
                    JOptionPane.showMessageDialog(frame,
                        "Invalid payment ID or payment is not pending.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                List<String> paymentLines = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader("payment.txt"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith(paymentID + ",")) {
                            String[] parts = line.split(",", 6);
                            parts[5] = "Paid";
                            paymentLines.add(String.join(",", parts));
                        } else {
                            paymentLines.add(line);
                        }
                    }
                }

                try (PrintWriter writer = new PrintWriter(new FileWriter("payment.txt"))) {
                    for (String line : paymentLines) {
                        writer.println(line);
                    }
                }

                String salesID = "SA" + String.format("%03d", getNextSaleID());
                LocalDateTime now = LocalDateTime.now();
                try (PrintWriter writer = new PrintWriter(new FileWriter("sales.txt", true))) {
                    writer.println(String.join(",",
                        salesID,
                        customerID,
                        Main.currentSalesmanID,
                        carID,
                        now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    ));
                }

                try (PrintWriter writer = new PrintWriter(new FileWriter("customerhistory.txt", true))) {
                    writer.println(String.join(",",
                        salesID,
                        customerID,
                        carID,
                        amount,
                        now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    ));
                }

                try (PrintWriter writer = new PrintWriter(new FileWriter("salesmanhistory.txt", true))) {
                    writer.println(String.join(",",
                        salesID,
                        Main.currentSalesmanID,
                        carID,
                        now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    ));
                }

                List<String> bookingLines = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader("booking.txt"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",", 5);
                        if (parts.length >= 5 && parts[1].equals(carID) && parts[2].equals(customerID)) {
                            continue;
                        }
                        bookingLines.add(line);
                    }
                }

                try (PrintWriter writer = new PrintWriter(new FileWriter("booking.txt"))) {
                    for (String line : bookingLines) {
                        writer.println(line);
                    }
                }

                List<String> carLines = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader("cars.txt"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.startsWith(carID + ",")) {
                            carLines.add(line);
                        }
                    }
                }

                try (PrintWriter writer = new PrintWriter(new FileWriter("cars.txt"))) {
                    for (String line : carLines) {
                        writer.println(line);
                    }
                }

                JOptionPane.showMessageDialog(frame,
                    "Payment collected successfully!\nSale ID: " + salesID,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

                refreshTable();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                    "Error processing payment: " + ex.getMessage(),
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getNextSaleID() throws IOException {
        File file = new File("sales.txt");
        if (!file.exists() || file.length() == 0) return 1;

        String lastLine = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lastLine = line;
                }
            }
        }

        String[] parts = lastLine.split(",", 2);
        String lastID = parts[0];
        return Integer.parseInt(lastID.substring(2)) + 1;
    }
}