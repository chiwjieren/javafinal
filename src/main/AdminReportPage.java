import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnTopModels) {
            List<Sale> sales = Sale.loadAll("sales.txt");
            Map<String,Long> topBrands = Analysis.topSellingBrands(sales, 5);

            StringBuilder sb = new StringBuilder("Top Selling Brands:\n");
            topBrands.forEach((brand, count) ->
                sb.append(brand)
                .append(" → ")
                .append(count)
                .append(" sales\n")
            );

            JOptionPane.showMessageDialog(frame,
                sb.toString(),
                "Top Brands",
                JOptionPane.INFORMATION_MESSAGE);            
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
                } 
                
                catch (Exception pe) {
                    JOptionPane.showMessageDialog(frame,
                        "Invalid date format. Please use YYYY-MM-DD.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                List<Payment> payments = Payment.loadAll("payment.txt");
                double rev = Analysis.revenueOnDate(payments, date);

                JOptionPane.showMessageDialog(frame,
                    String.format("Revenue on %s: RM%.2f", date, rev),
                    "Revenue on " + date,
                    JOptionPane.INFORMATION_MESSAGE);
            }

            else { 
                frame.dispose();
                parent.getFrame().setVisible(true);
            }
        } 

        catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "I/O Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
