import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.List;

public class AdminAnalysisPage implements ActionListener {
    private JFrame frame;
    private JButton btnPayments, btnFeedback, btnBack;
    private AdminPage parent;

    public AdminAnalysisPage(AdminPage parent) {
        this.parent = parent;
        frame = new JFrame("Payment & Feedback Analysis");
        frame.setSize(400,200);
        frame.setLocation(500,300);
        frame.setLayout(new FlowLayout());

        btnPayments = new JButton("Show Payment Stats");
        btnFeedback = new JButton("Show Feedback Stats");
        btnBack     = new JButton("Back");

        btnPayments.addActionListener(this);
        btnFeedback.addActionListener(this);
        btnBack.addActionListener(this);

        frame.add(btnPayments);
        frame.add(btnFeedback);
        frame.add(btnBack);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnFeedback) {
                frame.setVisible(false);

                List<Sale> sales = Sale.loadAll("sales.txt");

                String[] cols = {
                    "SaleID", "Timestamp",
                    "Rating", "Customer Review", "Salesman Comment"
                };

                Object[][] data = new Object[sales.size()][cols.length];

                for (int i = 0; i < sales.size(); i++) {
                    Sale sale = sales.get(i);
                    data[i][0] = sale.getSaleID();
                    data[i][1] = sale.getTimestamp();
                    data[i][2] = sale.getRating();
                    data[i][3] = sale.getCustomerReview();
                    data[i][4] = sale.getSalesmanComment();
                }

                JTable table = new JTable(data, cols);
                table.setEnabled(false);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                table.getColumnModel().getColumn(0).setPreferredWidth(60); 
                table.getColumnModel().getColumn(1).setPreferredWidth(120); 
                table.getColumnModel().getColumn(2).setPreferredWidth(50);  
                table.getColumnModel().getColumn(3).setPreferredWidth(300); 
                table.getColumnModel().getColumn(4).setPreferredWidth(500); 
                JScrollPane scrollPane = new JScrollPane(table);                
                
                double avgRating = Sale.calculateAverageRating();
                Map<Integer, Long> dist = Sale.calculateRatingDistribution();

                StringBuilder ratingBreakdown = new StringBuilder();
                for (int i = 1; i <= 5; i++) {
                    long count = dist.getOrDefault(i, 0L);
                    ratingBreakdown.append(String.format("Rating %d: %d    ", i, count));
                }

                JLabel lblAvg = new JLabel(String.format("Average Rating: %.2f", avgRating));
                JLabel lblDist = new JLabel(ratingBreakdown.toString());

                JButton btnBackToAnalysis = new JButton("Back");

                JFrame feedbackFrame = new JFrame("Feedback Analysis");
                feedbackFrame.setSize(900, 400);
                feedbackFrame.setLocationRelativeTo(frame);
                feedbackFrame.setLayout(new BorderLayout(10, 10));

                JPanel bottomPanel = new JPanel(new GridLayout(3, 1, 5, 5));
                bottomPanel.add(lblAvg);
                bottomPanel.add(lblDist);
                bottomPanel.add(btnBackToAnalysis);

                feedbackFrame.add(scrollPane, BorderLayout.CENTER);
                feedbackFrame.add(bottomPanel, BorderLayout.SOUTH);
                feedbackFrame.setVisible(true);

                btnBackToAnalysis.addActionListener(ae -> {
                    feedbackFrame.dispose();
                    frame.setVisible(true);
                });            
            }

            else if (e.getSource() == btnPayments) {
                frame.setVisible(false);
                List<Payment> payments = Payment.loadAll("payment.txt");

                String[] cols = { "PaymentID", "CustomerID", "Amount", "Timestamp", "Status" };
                Object[][] data = new Object[payments.size()][cols.length];
                double totalPaid = 0, totalPending = 0;
                for (int i = 0; i < payments.size(); i++) {
                    Payment p = payments.get(i);
                    data[i][0] = p.getPaymentID();
                    data[i][1] = p.getCustomerID();
                    data[i][2] = p.getAmount();
                    data[i][3] = p.getTimestamp().toString();
                    data[i][4] = p.getStatus();
                    if (p.getStatus().equalsIgnoreCase("paid")) {
                        totalPaid += p.getAmount();
                    } else if (p.getStatus().equalsIgnoreCase("pending")) {
                        totalPending += p.getAmount();
                    }
                }

                JTable table = new JTable(data, cols);
                table.setEnabled(false);
                JScrollPane scrollPane = new JScrollPane(table);

                JLabel lblPaid = new JLabel(String.format("Total Paid Revenue:   RM%.2f", totalPaid));
                JLabel lblPending = new JLabel(String.format("Total Pending Amount: RM%.2f", totalPending));

                JButton btnBackToAnalysis = new JButton("Back");

                JFrame analysisFrame = new JFrame("Payment Analysis");
                analysisFrame.setSize(700, 400);
                analysisFrame.setLocationRelativeTo(frame);
                analysisFrame.setLayout(new BorderLayout(10, 10));

                JPanel bottomPanel = new JPanel(new GridLayout(3, 1, 5, 5));
                bottomPanel.add(lblPaid);
                bottomPanel.add(lblPending);
                bottomPanel.add(btnBackToAnalysis);

                analysisFrame.add(scrollPane, BorderLayout.CENTER);
                analysisFrame.add(bottomPanel, BorderLayout.SOUTH);
                analysisFrame.setVisible(true);

                btnBackToAnalysis.addActionListener(ae -> {
                    analysisFrame.dispose();
                    frame.setVisible(true);
                });            
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
