import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class AdminPage implements ActionListener{
    JFrame x;
    Button mngSalesman, mngCustomer, mngCar, paymentAnalysis, feedbackAnalysis, report, logout;

    public static AdminManageSalesman manageSalesman;

    public AdminPage() {
        x = new JFrame();
        x.setSize(500,500);
        x.setLocation(500,200);

        mngSalesman = new Button("All Salesmans");
        mngCustomer = new Button("All Customers");
        mngCar = new Button("All Cars");
        paymentAnalysis = new Button("Payments");
        feedbackAnalysis = new Button("Feedbacks");
        report = new Button("Reports");
        logout = new Button("Logout");

        mngSalesman.addActionListener(this);
        mngCustomer.addActionListener(this);
        mngCar.addActionListener(this);
        paymentAnalysis.addActionListener(this);
        feedbackAnalysis.addActionListener(this);
        report.addActionListener(this);
        logout.addActionListener(this);

        x.setLayout(new GridLayout(7,1,5,5));
        x.add(mngSalesman);
        x.add(mngCustomer);
        x.add(mngCar);
        x.add(paymentAnalysis);
        x.add(feedbackAnalysis);    
        x.add(report);
        x.add(logout);

        x.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        x.setVisible(false);

        if (e.getSource() == mngSalesman) {
            manageSalesman = new AdminManageSalesman(this);
        }

        else if (e.getSource() == mngCustomer) {
            
        }

        else if (e.getSource() == mngCar) {
            
        }

        else if (e.getSource() == paymentAnalysis) {
            
        }

        else if (e.getSource() == feedbackAnalysis) {
            
        }

        else if (e.getSource() == report) {
            
        }

        else if (e.getSource() == logout) {
            Main.first = new Page1();
        }
    }

}
