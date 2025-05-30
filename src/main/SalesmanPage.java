import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class SalesmanPage implements ActionListener{
    JFrame jframe;
    Button editProfile, viewCars, collectPayment, viewSales, review, logout;

    public SalesmanPage() {
        jframe = new JFrame("Salesman Page");
        jframe.setSize(500,500);
        jframe.setLocation(400,100);

        editProfile = new Button("Edit Profile");
        viewCars = new Button("All Cars");
        collectPayment = new Button("Collect Payment");
        viewSales = new Button("Sales");
        review = new Button("Comment");
        logout = new Button("Logout");

        editProfile.addActionListener(this);
        viewCars.addActionListener(this);
        collectPayment.addActionListener(this);
        viewSales.addActionListener(this);
        review.addActionListener(this);
        logout.addActionListener(this);

        jframe.setLayout(new GridLayout(5,1,5,5));
        jframe.add(editProfile);
        jframe.add(viewCars);
        jframe.add(collectPayment);
        jframe.add(viewSales);
        jframe.add(review);
        jframe.add(logout);

        jframe.setVisible(true);
    }

    public JFrame getFrame() { return jframe; }

    @Override
    public void actionPerformed(ActionEvent e) {
        jframe.setVisible(false);

        if (e.getSource() == editProfile) {

           try {
                new SalesmanEditProfile(this, Main.currentSalesmanID);
            } 
            
            catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(jframe,
                    "Could not load profile: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                jframe.setVisible(true);
            }

        }

        else if (e.getSource() == viewCars) {
            new SalesmanManageCar(this);
        }

        else if (e.getSource() == collectPayment) {
            new SalesmanCollectPayment(this);
        }

        else if (e.getSource() == viewSales) {
            new SalesmanViewSales(this);
        }

        else if (e.getSource() == review) {
            new SalesmanViewComment(this);
        }

        else if (e.getSource() == logout) {
            new Page1();
        }
    }

}
