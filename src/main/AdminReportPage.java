import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

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

    private Map<String, Long> getTopSellingBrands(List<Sale> sales, int limit) {
        Map<String, Long> brandCounts = new HashMap<>();
        
        // Debug print
        System.out.println("Number of sales: " + sales.size());
        
        // Count sales for each brand directly from sales.txt
        try (BufferedReader br = new BufferedReader(new FileReader("sales.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 4) {  // Make sure we have enough parts
                    String brand = parts[3];  // Brand is at index 4
                    brandCounts.merge(brand, 1L, Long::sum);
                    // Debug print
                    System.out.println("Found brand: " + brand);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return brandCounts;
        }

        // Debug print
        System.out.println("Brand counts before sorting: " + brandCounts);

        // Sort by count in descending order and limit to top N
        Map<String, Long> result = brandCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                HashMap::new
            ));
            
        // Debug print
        System.out.println("Final result: " + result);
        
        return result;
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
                List<Sale> sales = Sale.loadAll("sales.txt");
                // Debug print
                System.out.println("Loaded sales from sales.txt");
                
                Map<String, Long> topBrands = getTopSellingBrands(sales, 5);

                StringBuilder sb = new StringBuilder("Top Selling Brands:\n\n");
                if (topBrands.isEmpty()) {
                    sb.append("No sales data available.");
                } else {
                    topBrands.forEach((brand, count) ->
                        sb.append(String.format("%-15s → %d sales\n", brand, count))
                    );
                }

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

            else { 
                frame.dispose();
                parent.getFrame().setVisible(true);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "I/O Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
