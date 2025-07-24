package src.login.view;

import javax.swing.*;
import java.awt.event.ActionListener;

public class UserIdentification extends JFrame {
    public JPanel login, topPanel;
    public JLabel header;
    public JButton personnelButton, guestButton;

    public JPanel getLogin() {
        return login;
    }
    public JPanel getTopPanel() {
        return topPanel;
    }
    public JLabel getHeader() {
        return header;
    }
    public JButton getPersonnelButton() {
        return personnelButton;
    }
    public JButton getGuestButton() {
        return guestButton;
    }

    public void setGuestButton(ActionListener listener) {
        guestButton.addActionListener(listener);
    }

    public void setPersonnelButton(ActionListener listener) {
        personnelButton.addActionListener(listener);
    }

    public UserIdentification() {
        setContentPane(login);
        setTitle("Log In");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new UserIdentification();
    }
}
