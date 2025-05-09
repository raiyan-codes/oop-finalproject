import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panel for user login and registration
 */
public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    
    /**
     * Constructor for the LoginPanel
     */
    public LoginPanel() {
        initializeUI();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 175, 80));
        JLabel titleLabel = new JLabel("Roommate Chore Manager");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(70, 175, 80));
        loginButton.setForeground(Color.BLACK);
        
        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(33, 150, 243));
        registerButton.setForeground(Color.BLACK);
        
        // Add components to the form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(loginButton, gbc);
        
        gbc.gridy = 3;
        formPanel.add(registerButton, gbc);
        
        // Create a wrapper panel to center the form
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(formPanel);
        
        // Add panels to the main panel
        add(titlePanel, BorderLayout.NORTH);
        add(wrapperPanel, BorderLayout.CENTER);
    }
    
    /**
     * Gets the username from the username field
     * @return The username
     */
    public String getUsername() {
        return usernameField.getText();
    }
    
    /**
     * Gets the password from the password field
     * @return The password
     */
    public String getPassword() {
        return new String(passwordField.getPassword());
    }
    
    /**
     * Clears the username and password fields
     */
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
    
    /**
     * Sets the action listener for the login button
     * @param listener The action listener
     */
    public void setLoginButtonListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }
    
    /**
     * Sets the action listener for the register button
     * @param listener The action listener
     */
    public void setRegisterButtonListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }
} 