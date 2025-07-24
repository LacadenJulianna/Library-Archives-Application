package src.login.model;

import src.databaseConnection.DataAccess;
import src.guest.model.GuestDashboardModel;
import src.guest.view.GuestDashboardView;
import src.login.view.LoginView;
import src.login.view.UserIdentification;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//User Email Identification
public class LoginModel {
    static LoginView view;

    public LoginModel(LoginView view){
        this.view = view;

        this.view.setOkButton(new OkButtonListener());
        this.view.setCancelButton(new CancelButtonListener());
        this.view.getRootPane().setDefaultButton(this.view.getOkButton());
    }

    public boolean validateLogin(String email) throws SQLException {
        String checkQuery = "SELECT * FROM Users WHERE User_Email = ?";
        String insertQuery = "INSERT INTO Users (User_Email) VALUES (?)";

        if (email == null){
            throw new IllegalArgumentException("Email is null");
        }

        try (PreparedStatement checkStmt = DataAccess.getConnection().prepareStatement(checkQuery)) {
            checkStmt.setString(1, email);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Email exists
                    return true;
                }
            }

            // Email doesn't exist, insert it
            try (PreparedStatement insertStmt = DataAccess.getConnection().prepareStatement(insertQuery)) {
                insertStmt.setString(1, email);
                insertStmt.executeUpdate();
                System.out.println("New account created for: " + email);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return false; // email didn't exist before
    }

    private boolean isValidEmailFormat(String email){
        return email.matches("^[A-Za-z0-9+_.-]+@((gmail\\.com)|(yahoo\\.com)|(slu\\.edu\\.ph))$");
    }


    private class OkButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            String email = view.getUserEmail();
            if (email == null || email.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter an email address.");
                return;
            }

            if (!isValidEmailFormat(email)) {
                JOptionPane.showMessageDialog(null, "Please enter a valid email address.");
                return;
            }

            try {
                if (validateLogin(email)) {
                    System.out.println("Email entered: " + email);
                    System.out.println("Login successful for: " + email);
                    view.dispose();
                    GuestDashboardView dashboardView = new GuestDashboardView();
                    new GuestDashboardModel(dashboardView, email);
                    dashboardView.setVisible(true);
                } else {
                    System.out.println("New account created. Please log in again.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred during login.");
            }
        }
    }
    private class CancelButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            view.dispose(); //dispose current window
            //Directs to the previous window
            UserIdentification userIdentificationView = new UserIdentification();
            new UserIdentificationModel(userIdentificationView);
            userIdentificationView.setVisible(true);
        }
    }
}
