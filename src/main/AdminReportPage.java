import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AdminReportPage implements ActionListener {
    private final JFrame frame;
    private final JButton btnTopModels, btnRevenueTrend, btnBack;
    private final AdminPage parent;

    public AdminReportPage(AdminPage parent) {
        this.parent = parent;

        frame = new JFrame("Reports: Top Models & Revenue Trend");
        frame.setSize(450, 150);
        frame.setLocation(500, 300);
        frame.setLayout(new FlowLayout(10, 20, 20));

        btnTopModels = new JButton("Top Selling Brands");
        btnRevenueTrend = new JButton("Daily Revenue");
        btnBack = new JButton("Back");

        btnTopModels.addActionListener(this);
        btnRevenueTrend.addActionListener(this);
        btnBack.addActionListener(this);

        frame.add(btnTopModels);
        frame.add(btnRevenueTrend);
        frame.add(btnBack);
        frame.setVisible(true);
    }

    private double getRevenueOnDate(List<Payment> payments, LocalDate targetDate) {
        double total = 0;
        for (Payment payment : payments) {
            if (payment.getStatus().equalsIgnoreCase("paid")) {
                LocalDateTime paymentTime = payment.getTimestamp();
                if (paymentTime.toLocalDate().equals(targetDate)) {
                    total += payment.getAmount();
                }
            }
        }
        return total;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnTopModels) {
                Sale.showBestSellingBrands();
            } 
            else if (e.getSource() == btnRevenueTrend) {
                String input = JOptionPane.showInputDialog(
                    frame,
                    "Enter date (YYYY-MM-DD):",
                    LocalDate.now().toString()
                );
                
                if (input == null || input.isBlank()) return;

                LocalDate date;
                try {
                    date = LocalDate.parse(input);
                } catch (Exception pe) {
                    JOptionPane.showMessageDialog(frame,
                        "Invalid date format. Please use YYYY-MM-DD.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                List<Payment> payments = Payment.loadAll("payment.txt");
                double revenue = getRevenueOnDate(payments, date);

                JOptionPane.showMessageDialog(frame,
                    String.format("Revenue on %s: RM%.2f", date, revenue),
                    "Revenue on " + date,
                    JOptionPane.INFORMATION_MESSAGE);
            }
            else if (e.getSource() == btnBack) {
                frame.dispose();
                parent.getFrame().setVisible(true);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
