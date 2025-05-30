import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class CustomerPage implements ActionListener {
    JFrame jframe;
    Button editProfile, viewCars, affordableCars, localCars, luxuryCars, History, Review, Bookings, logout;

    public static CustomerViewAvailableCars viewAvailableCars;
    public static CustomerBookings viewBookings;

    public CustomerPage() {
        jframe = new JFrame("Customer Page");
        jframe.setSize(500,500);
        jframe.setLocation(400,100);

        editProfile = new Button("Edit Profile");
        viewCars = new Button("All Cars");
        affordableCars = new Button("Affordable Cars");
        localCars = new Button("Local Cars");
        luxuryCars = new Button("Luxury Cars");
        History = new Button("History");
        Review = new Button("Review");
        Bookings = new Button("Bookings");
        logout = new Button("Logout");

        editProfile.addActionListener(this);
        viewCars.addActionListener(this);
        affordableCars.addActionListener(this);
        localCars.addActionListener(this);
        luxuryCars.addActionListener(this);
        History.addActionListener(this);
        Review.addActionListener(this);
        Bookings.addActionListener(this);
        logout.addActionListener(this);

        jframe.setLayout(new GridLayout(6,2,5,5));
        jframe.add(editProfile);
        jframe.add(viewCars);
        jframe.add(affordableCars);
        jframe.add(localCars);
        jframe.add(luxuryCars);
        jframe.add(History);
        jframe.add(Review);
        jframe.add(Bookings);
        jframe.add(logout);

        jframe.setVisible(true);
    }

    public JFrame getFrame() { return jframe; }

    @Override
    public void actionPerformed(ActionEvent e) {
        jframe.setVisible(false);

        if (e.getSource() == editProfile) {
            try {
                new CustomerEditProfile(this, Main.currentCustomerID);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(jframe,
                    "Could not load profile: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                jframe.setVisible(true);
            }
        }
        else if (e.getSource() == viewCars) {
            new CustomerViewAvailableCars(this);
        }
        else if (e.getSource() == affordableCars) {
            new CustomerViewAffordableCars(this);
        }
        else if (e.getSource() == localCars) {
            new CustomerViewLocalCars(this);
        }
        else if (e.getSource() == luxuryCars) {
            new CustomerViewLuxuryCars(this);
        }
        else if (e.getSource() == History) {
            new CustomerViewHistory(this);
        }
        else if (e.getSource() == Review) {
            new CustomerReview(this);
        }
        else if (e.getSource() == Bookings) {
            new CustomerBookings(this);
        }
        else if (e.getSource() == logout) {
            new Page1();
        }
    }
}
