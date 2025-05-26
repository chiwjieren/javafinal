import java.awt.*;
import java.util.List;
import javax.swing.*;

public class SalesmanViewProfile extends JFrame {
    private SalesmanData salesman;
    private List<SalesmanData> salesmenList;
    
    // Text fields for profile information
    private JTextField idField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JButton editButton;
    private JButton saveButton;
    private JButton cancelButton;
    
    public SalesmanViewProfile(SalesmanData s, List<SalesmanData> salesmenList) {
        this.salesman = s;
        this.salesmenList = salesmenList;
        
        // Set up the frame
        setTitle("Profile - " + s.getName());
        setSize(400, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ID
        mainPanel.add(new JLabel("ID:"));
        idField = new JTextField(s.getID());
        idField.setEditable(false);
        mainPanel.add(idField);

        // Name
        mainPanel.add(new JLabel("Name:"));
        nameField = new JTextField(s.getName());
        nameField.setEditable(false);
        mainPanel.add(nameField);

        // Password
        mainPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(s.getPassword());
        passwordField.setEditable(false);
        mainPanel.add(passwordField);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        editButton = new JButton("Edit Password");
        saveButton = new JButton("Save Changes");
        cancelButton = new JButton("Exit");

        // Initially disable save button
        saveButton.setEnabled(false);

        // Add action listeners
        editButton.addActionListener(e -> enableEditing());
        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to frame
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
    
    private void enableEditing() {
        // Only enable password editing
        passwordField.setEditable(true);
        editButton.setEnabled(false);
        saveButton.setEnabled(true);
    }

    private void saveChanges() {
        // Only update password
        String newPassword = new String(passwordField.getPassword());
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        salesman.setPassword(newPassword);
        try {
            SalesmanData.saveSalesmen("Salesman.txt", salesmenList);
            JOptionPane.showMessageDialog(this, "Password updated successfully!");
            passwordField.setEditable(false);
            editButton.setEnabled(true);
            saveButton.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving changes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}