import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

            if (e.getSource() == btnPayments) {
                List<Payment> payments = Payment.loadAll("payment.txt");
                double total = Analysis.totalRevenue(payments);
                Map<String,Double> byStatus = Analysis.revenueByStatus(payments);

                JOptionPane.showMessageDialog(frame,
                    String.format("Total Revenue (paid only): RM%.2f\nBy Status: %s",
                                  total, byStatus),
                    "Payment Analysis",
                    JOptionPane.INFORMATION_MESSAGE);
            }

            else if (e.getSource() == btnFeedback) {
                List<Sale> sales = Sale.loadAll("sales.txt");
                double avg  = Analysis.averageRating(sales);
                Map<Integer,Long> dist = Analysis.ratingDistribution(sales);

                JOptionPane.showMessageDialog(frame,
                    String.format("Average Rating: %.2f\nRating: %s",
                                  avg, dist),
                    "Feedback Analysis",
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
