package src.login.view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JPanel MainPanel;
    private JTextField textFieldEmail;
    private JButton cancelButton;
    private JButton okButton;

    private String userEmail; // Stores the input email

    public LoginView() {
        setContentPane(MainPanel);
        setTitle("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setVisible(true);

        textFieldEmail.setText("Email");
        textFieldEmail.setForeground(java.awt.Color.GRAY); // shows "email" in the field
        textFieldEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                if (textFieldEmail.getText().equals("Email")) {
                    textFieldEmail.setText("");
                    textFieldEmail.setForeground(java.awt.Color.BLACK);
                }
            }
        });
    }

    public JTextField getTextFieldEmail() {
        return textFieldEmail;
    }

    public void setTextFieldEmail(JTextField textFieldEmail) {
        this.textFieldEmail = textFieldEmail;
    }

    public JButton getOkButton() {
        return okButton;
    }

    public void setCancelButton(ActionListener listener){
        cancelButton.addActionListener(listener);
    }

    public void setOkButton(ActionListener listener) {
        okButton.addActionListener(listener);
    }


    public String getUserEmail() {
        return textFieldEmail.getText();
    }

    public static void main(String[] args) {
        new LoginView();
    }
}
