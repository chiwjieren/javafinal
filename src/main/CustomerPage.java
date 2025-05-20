import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class CustomerPage implements ActionListener {
    JFrame jframe;
    Button viewAvailableCars, AffordableCars, LocalCars, LuxuryCars, FindCar, History, Logout;

    public CustomerPage() {
        jframe = new JFrame();
        jframe.setSize(500,500);
        jframe.setLocation(500,200);

        viewAvailableCars = new Button("View Available Cars");
        AffordableCars = new Button("Affordable Cars");
        LocalCars = new Button("Local Cars");
        LuxuryCars = new Button("Luxury Cars");
        FindCar = new Button("Find Car");
        History = new Button("History");
        Logout = new Button("Logout");

        viewAvailableCars.addActionListener(this);
        AffordableCars.addActionListener(this);
        LocalCars.addActionListener(this);
        LuxuryCars.addActionListener(this);
        FindCar.addActionListener(this);
        History.addActionListener(this);
        Logout.addActionListener(this);

        jframe.setLayout(new GridLayout(6,1,5,5));
        jframe.add(viewAvailableCars);
        jframe.add(AffordableCars);
        jframe.add(LocalCars);
        jframe.add(LuxuryCars);
        jframe.add(FindCar);
        jframe.add(History);
        jframe.add(Logout);

        jframe.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        jframe.setVisible(false);

        if (e.getSource() == viewAvailableCars) {
            //viewAvailableCars = new ViewAvailableCars(this);
        }    

        else if (e.getSource() == AffordableCars) {
            //AffordableCars = new AffordableCars(this);

        }

        else if (e.getSource() == LocalCars) {
            //LocalCars = new LocalCars(this);
        }
        
        else if (e.getSource() == LuxuryCars) {
            //LuxuryCars = new LuxuryCars(this);
        }

        else if (e.getSource() == FindCar) {
            //FindCar = new FindCar(this);
        }
        
        else if (e.getSource() == History) {
            //History = new History(this);
        }

        else if (e.getSource() == Logout) {
            Main.first = new Page1();
        }
        

        
        
        
    }
}
