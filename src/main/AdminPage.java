import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class AdminPage implements ActionListener{
    JFrame jframe;
    Button mngSalesman, mngCustomer, mngCar, analysis, report, logout;

    public AdminPage() {
        jframe = new JFrame("Admin Page");
        jframe.setSize(500,500);
        jframe.setLocation(400,100);

        mngSalesman = new Button("All Salesmans");
        mngCustomer = new Button("All Customers");
        mngCar = new Button("All Cars");
        analysis = new Button("Payment & Feedback Analysis");
        report = new Button("Reports");
        logout = new Button("Logout");

        mngSalesman.addActionListener(this);
        mngCustomer.addActionListener(this);
        mngCar.addActionListener(this);
        analysis.addActionListener(this);
        report.addActionListener(this);
        logout.addActionListener(this);

        jframe.setLayout(new GridLayout(6,1,5,5));
        jframe.add(mngSalesman);
        jframe.add(mngCustomer);
        jframe.add(mngCar);
        jframe.add(analysis);
        jframe.add(report);
        jframe.add(logout);

        jframe.setVisible(true);
    }

    public JFrame getFrame() { return jframe; }

    @Override
    public void actionPerformed(ActionEvent e) {
        jframe.setVisible(false);

        if (e.getSource() == mngSalesman) {
           new AdminManageSalesman(this);
        }

        else if (e.getSource() == mngCustomer) {
           new AdminManageCustomer(this);
        }

        else if (e.getSource() == mngCar) {
            new AdminManageCar(this);
        }

        else if (e.getSource() == analysis) {
            new AdminAnalysisPage(this);
        }

        else if (e.getSource() == report) {
            
        }

        else if (e.getSource() == logout) {
            new Page1();
        }
    }

}
