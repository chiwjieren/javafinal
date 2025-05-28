import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class CustomerPage implements ActionListener {
    JFrame jframe;
    Button AvailableCars, AffordableCars, LocalCars, LuxuryCars, FindCar, History, Logout, Bookings;

    public static CustomerViewAvailableCars viewAvailableCars;

    public CustomerPage() {
        jframe = new JFrame();
        jframe.setSize(500,500);
        jframe.setLocation(500,200);

        AvailableCars = new Button("View Available Cars");
        AffordableCars = new Button("Affordable Cars");
        LocalCars = new Button("Local Cars");
        LuxuryCars = new Button("Luxury Cars");
        FindCar = new Button("Find Car");
        History = new Button("History");
        Logout = new Button("Logout");
        Bookings = new Button("Bookings");

        AvailableCars.addActionListener(this);
        AffordableCars.addActionListener(this);
        LocalCars.addActionListener(this);
        LuxuryCars.addActionListener(this);
        FindCar.addActionListener(this);
        History.addActionListener(this);
        Logout.addActionListener(this);
        Bookings.addActionListener(this);

        jframe.setLayout(new GridLayout(6,1,5,5));
        jframe.add(AvailableCars);
        jframe.add(AffordableCars);
        jframe.add(LocalCars);
        jframe.add(LuxuryCars);
        jframe.add(FindCar);
        jframe.add(History);
        jframe.add(Logout);
        jframe.add(Bookings);

        jframe.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        jframe.setVisible(false);

        if (e.getSource() == AvailableCars) {
            viewAvailableCars = new CustomerViewAvailableCars(this);
        }    

        else if (e.getSource() == AffordableCars) {
            //ViewAffordableCars = new CustomerAffordableCars(this);

        }

        else if (e.getSource() == LocalCars) {
            //ViewLocalCars = new CustomerLocalCars(this);
        }
        
        else if (e.getSource() == LuxuryCars) {
            //ViewLuxuryCars = new CustomerLuxuryCars(this);
        }

        else if (e.getSource() == FindCar) {
            //FindCarFilter = new CustomerFindCar(this);
        }
        
        else if (e.getSource() == History) {
            //ViewHistory = new CustomerHistory(this);
        }

        else if (e.getSource() == Logout) {
            Main.first = new Page1();
        }

        else if (e.getSource() == Bookings) {
            //ViewBookings = new CustomerBookings(this);
        }

        
        
        
    }
}
