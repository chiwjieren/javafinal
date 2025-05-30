import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class CustomerPage implements ActionListener {
    JFrame jframe;
    Button AvailableCars, AffordableCars, LocalCars, LuxuryCars, History, Logout, Bookings, editProfile;

    public static CustomerViewAvailableCars viewAvailableCars;
    public static CustomerBookings viewBookings;

    public CustomerPage() {
        jframe = new JFrame();
        jframe.setSize(500,500);
        jframe.setLocation(500,200);

        AvailableCars = new Button("View Available Cars");
        AffordableCars = new Button("Affordable Cars");
        LocalCars = new Button("Local Cars");
        LuxuryCars = new Button("Luxury Cars");
        History = new Button("History");
        Logout = new Button("Logout");
        Bookings = new Button("Bookings");
        editProfile = new Button("Edit Profile");

        AvailableCars.addActionListener(this);
        AffordableCars.addActionListener(this);
        LocalCars.addActionListener(this);
        LuxuryCars.addActionListener(this);
        History.addActionListener(this);
        Logout.addActionListener(this);
        Bookings.addActionListener(this);
        editProfile.addActionListener(this);

        jframe.setLayout(new GridLayout(6,1,5,5));
        jframe.add(AvailableCars);
        jframe.add(AffordableCars);
        jframe.add(LocalCars);
        jframe.add(LuxuryCars);
        jframe.add(History);
        jframe.add(Logout);
        jframe.add(Bookings);
        jframe.add(editProfile);

        jframe.setVisible(true);
    }

    public JFrame getFrame() { return jframe; }


    @Override
    public void actionPerformed(ActionEvent e) {
        jframe.setVisible(false);

        if (e.getSource() == AvailableCars) {
            viewAvailableCars = new CustomerViewAvailableCars(this);
        }    

        else if (e.getSource() == AffordableCars) {
            new CustomerAffordableCars(this);
        }

        else if (e.getSource() == LocalCars) {
            new CustomerViewLocalCars(this);
        }
        
        else if (e.getSource() == LuxuryCars) {
            new CustomerViewLuxuryCars(this);
        }

        
        else if (e.getSource() == History) {
            new CustomerViewHistory(this);
        }

        else if (e.getSource() == Logout) {
            Main.first = new Page1();
        }

        else if (e.getSource() == Bookings) {
            viewBookings = new CustomerBookings(this);
        }

        if (e.getSource() == editProfile) {

            try {
                new CustomerEditProfile(this, Main.currentCustomerID);
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
    
    }
}
