package src.login.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PersonnelLoginView extends JFrame {
    private JPanel personnelLoginPanel, topPanel;
    private JLabel topLabel;
    private JPasswordField pPasswd;
    private JTextField pUsername;
    private JButton logInButton, cancelButton;

    public PersonnelLoginView() {
        // Initialize components
        personnelLoginPanel = new JPanel();
        topPanel = new JPanel();
        topLabel = new JLabel("Personnel Log In");

        pUsername = new JTextField(15);
        pPasswd = new JPasswordField(15);
        logInButton = new JButton("Log In");
        cancelButton = new JButton("Cancel");

        // Layout
        personnelLoginPanel.setLayout(new GridLayout(5, 1, 10, 10));
        personnelLoginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add components
        personnelLoginPanel.add(topLabel);
        personnelLoginPanel.add(pUsername);
        personnelLoginPanel.add(pPasswd);
        personnelLoginPanel.add(logInButton);
        personnelLoginPanel.add(cancelButton);

        setContentPane(personnelLoginPanel);
        setTitle("Personnel Log In");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setVisible(true);

        // Placeholder behavior for username
        pUsername.setText("Username");
        pUsername.setForeground(Color.GRAY);
        pUsername.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (pUsername.getText().equals("Username")) {
                    pUsername.setText("");
                    pUsername.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (pUsername.getText().isEmpty()) {
                    pUsername.setText("Username");
                    pUsername.setForeground(Color.GRAY);
                }
            }
        });

        // Placeholder behavior for password
        pPasswd.setEchoChar((char) 0); // Unmasked initially
        pPasswd.setText("Password");
        pPasswd.setForeground(Color.GRAY);
        pPasswd.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (String.valueOf(pPasswd.getPassword()).equals("Password")) {
                    pPasswd.setText("");
                    pPasswd.setEchoChar('•');
                    pPasswd.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (String.valueOf(pPasswd.getPassword()).isEmpty()) {
                    pPasswd.setText("Password");
                    pPasswd.setEchoChar((char) 0);
                    pPasswd.setForeground(Color.GRAY);
                }
            }
        });

        // Enable Enter key to trigger login
        getRootPane().setDefaultButton(logInButton);
    }

    // Getters
    public JPanel getPersonnelLoginPanel() { return personnelLoginPanel; }

    public JPanel getTopPanel() { return topPanel; }

    public JLabel getTopLabel() { return topLabel; }

    public String getpPasswd() { return new String(pPasswd.getPassword()); }

    public String getpUsername() { return pUsername.getText(); }

    public JButton getLogInButton() { return logInButton; }

    public void setLogInButton(ActionListener listener) {
        logInButton.addActionListener(listener);
    }

    public void setCancelButton(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    // Entry point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PersonnelLoginView::new);
    }
}