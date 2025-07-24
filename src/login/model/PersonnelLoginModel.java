package src.login.model;

import src.databaseConnection.DataAccess;
import src.login.view.PersonnelLoginView;
import src.login.view.UserIdentification;
import src.personnel.model.PersonnelDashboardModel;
import src.personnel.view.PersonnelDashboardView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonnelLoginModel {
    static PersonnelLoginView view;
    public PersonnelLoginModel(PersonnelLoginView view){
        this.view = view;

        this.view.setLogInButton(new LogInButtonListener());
        this.view.setCancelButton(new CancelButtonListener());
        this.view.getRootPane().setDefaultButton(this.view.getLogInButton());
    }

    public int validateLogin(String username, String password) throws SQLException {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }

        String query = "SELECT Password FROM personnel_account WHERE Username = ?";
        try (PreparedStatement stmt = DataAccess.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Extract the stored password
                    String storedPassword = rs.getString("Password");

                    // Perform case-sensitive password check
                    if (storedPassword.equals(password)) {
                        return 1; // Success
                    } else {
                        return -2; // Incorrect password
                    }
                } else {
                    return -1; // Username not found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private class LogInButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getpUsername();
            String password = String.valueOf(view.getpPasswd());

            if (username == null || username.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter your username");
                return;
            }
            if (password == null || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a password");
                return;
            }

            try {
                int result = validateLogin(username, password);
                switch (result) {
                    case 1:
                        // Login successful
                        System.out.println("Personnel Login Successful.");
                        view.dispose(); // Close the login window

                        // Open the dashboard
                        PersonnelDashboardView dashboardView = new PersonnelDashboardView();
                        new PersonnelDashboardModel(dashboardView);
                        dashboardView.setVisible(true);
                        break;
                    case -1:
                        JOptionPane.showMessageDialog(null, "Incorrect username. Please try again.");
                        break;
                    case -2:
                        JOptionPane.showMessageDialog(null, "Incorrect password. Please try again.");
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Unknown login error. Please contact support.");
                        break;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred while trying to login.");
            }
        }
    }

    private class CancelButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            view.dispose(); //dispose the current window
            UserIdentification userIdentificationView = new UserIdentification();
            new UserIdentificationModel(userIdentificationView);
            userIdentificationView.setVisible(true);
        }
    }
}
