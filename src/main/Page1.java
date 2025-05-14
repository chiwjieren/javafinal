import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.Button;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Page1 implements ActionListener {

    JFrame x;
    Button register, login, exit;

    public Page1() {
        x = new JFrame();
        x.setSize(300,100);
        x.setLocation(1000, 200);

        register = new Button("Register");
        login = new Button("Login");
        exit = new Button("Exit");

        register.addActionListener(this);
        login.addActionListener(this);
        exit.addActionListener(this);

        x.setLayout(new FlowLayout());
        x.add(register);
        x.add(login);
        x.add(exit);
        x.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == register) {
            String username = JOptionPane.showInputDialog("Enter username:");
            String password = JOptionPane.showInputDialog("Enter password:");

            try {

                String id = Customer.getNextCustomerID("customer.txt");

                ArrayList<String> newUser = new ArrayList<>();
                newUser.add(id);
                newUser.add(username);
                newUser.add(password);

                Customer.register("customer.txt", newUser);

                JOptionPane.showMessageDialog(x,
                    "Registration Successful!\nYour Customer ID is " + id,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );

            }

            catch (IllegalArgumentException ex) {
                
                JOptionPane.showMessageDialog(
                    x,
                    "Please don’t use commas, quotes, line breaks or leave blanks in your username/password.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
                );
            }

            catch (IOException ex) {

                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    x,
                    "Could not save registration. Please try again.",
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }

        else if (e.getSource() == login) {
            String id = JOptionPane.showInputDialog("Enter ID:");
            String password = JOptionPane.showInputDialog("Enter password:");

            try {
                
                ArrayList<String> data = new ArrayList<>();
                
                data.add(id);
                data.add(password);

                int role = id.charAt(0);
                boolean loginSuccess;

                switch (role) {
                    case 'A':
                    loginSuccess = Admin.login("admin.txt", data);
                    break;

                    case 'C':
                    loginSuccess = Customer.login("customer.txt", data);
                    break;

                    case 'S':
                    loginSuccess = Salesman.login("salesman.txt", data);
                    break;

                    default:
                    loginSuccess = false;
                    break;
                }

                if (loginSuccess) {
                    JOptionPane.showMessageDialog(x, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } 
                
                else {
                    JOptionPane.showMessageDialog(x, "Invalid ID or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }

            } 

            catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(x, "Login error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }

        else if (e.getSource() == exit) {
            System.exit(0);
        }

    }
} 
