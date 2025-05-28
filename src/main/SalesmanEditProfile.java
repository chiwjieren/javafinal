import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class SalesmanEditProfile implements ActionListener {
    private final JFrame frame;
    private final JTextField txtUsername;
    private final JTextField txtPassword;
    private final JButton btnSave, btnBack;
    private final String salesmanID;
    private final SalesmanPage parent;

    public SalesmanEditProfile(SalesmanPage parent, String salesmanID) throws IOException {
        this.parent     = parent;
        this.salesmanID = salesmanID;

        frame = new JFrame("Edit Profile - " + salesmanID);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(parent.getFrame());
        frame.setLayout(new GridLayout(4, 2, 5, 5));

        txtUsername = new JTextField();
        txtPassword = new JTextField();
        btnSave = new JButton("Save");
        btnBack = new JButton("Back");

        Salesman existing = Salesman.searchSalesman("salesman.txt", salesmanID);
        if (existing == null) {
            parent.getFrame().setVisible(true);
            throw new IllegalStateException("Salesman " + salesmanID + " not found");        
        }

        txtUsername.setText(existing.getUsername());
        txtPassword.setText(existing.getPassword());

        frame.add(new JLabel("Username:"));
        frame.add(txtUsername);
        frame.add(new JLabel("Password:"));
        frame.add(txtPassword);
        frame.add(btnSave);
        frame.add(btnBack);

        btnSave.addActionListener(this);
        btnBack.addActionListener(this);

        frame.setVisible(true);
        parent.getFrame().setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSave) {
            String newUser = txtUsername.getText().trim();
            String newPass = txtPassword.getText().trim();

            if (newUser.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Username and password cannot be blank.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                boolean ok = Salesman.update(
                    "salesman.txt",
                    salesmanID,
                    newUser,
                    newPass
                );

                if (ok) {
                    JOptionPane.showMessageDialog(frame,
                        "Profile updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
                else {
                    JOptionPane.showMessageDialog(frame,
                        "Update failed: ID not found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } 
            
            catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(frame,
                    "Invalid input: no commas, quotes, line breaks allowed.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            } 
            
            catch (IOException ioe) {
                ioe.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                    "I/O error: " + ioe.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        frame.dispose();
        parent.getFrame().setVisible(true);
    }
}
