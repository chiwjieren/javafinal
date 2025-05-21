import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class AdminPage implements ActionListener{
    JFrame jframe;
    Button mngSalesman, mngCustomer, mngCar, paymentAnalysis, feedbackAnalysis, report, logout;

    public static AdminManageSalesman manageSalesman;

    public AdminPage() {
        jframe = new JFrame("Admin Page");
        jframe.setSize(500,500);
        jframe.setLocation(400,100);

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

        jframe.setLayout(new GridLayout(7,1,5,5));
        jframe.add(mngSalesman);
        jframe.add(mngCustomer);
        jframe.add(mngCar);
        jframe.add(paymentAnalysis);
        jframe.add(feedbackAnalysis);    
        jframe.add(report);
        jframe.add(logout);

        jframe.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        jframe.setVisible(false);

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
