import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class SalesmanPage implements ActionListener{
    JFrame jframe;
    Button editProfile, viewCars, collectPayment, viewSales, logout;

    public SalesmanPage() {
        jframe = new JFrame("Salesman Page");
        jframe.setSize(500,500);
        jframe.setLocation(400,100);

        editProfile = new Button("Edit Profile");
        viewCars = new Button("All Cars");
        collectPayment = new Button("Collect Payment");
        viewSales = new Button("Sales");
        logout = new Button("Logout");

        editProfile.addActionListener(this);
        viewCars.addActionListener(this);
        collectPayment.addActionListener(this);
        viewSales.addActionListener(this);
        logout.addActionListener(this);

        jframe.setLayout(new GridLayout(5,1,5,5));
        jframe.add(editProfile);
        jframe.add(viewCars);
        jframe.add(collectPayment);
        jframe.add(viewSales);
        jframe.add(logout);

        jframe.setVisible(true);
    }

    public JFrame getFrame() { return jframe; }

    @Override
    public void actionPerformed(ActionEvent e) {
        jframe.setVisible(false);

        if (e.getSource() == editProfile) {
           
        }

        else if (e.getSource() == viewCars) {

        }

        else if (e.getSource() == collectPayment) {

        }

        else if (e.getSource() == viewSales) {

        }

        else if (e.getSource() == logout) {
            new Page1();
        }
    }

}
