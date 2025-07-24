package src.personnel.model;

import src.databaseConnection.DataAccess;
import src.personnel.view.CategoryManagerView;
import src.personnel.view.CompetitionManagerView;
import src.personnel.view.PersonnelDashboardView;

import javax.swing.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * Handles creation and deletion of competition records,
 * After creating a competition, transitions to category management.
 */
public class CompetitionManagerModel {
    private final CompetitionManagerView view;
    private String compId = null;

    private static final int MAX_NAME_LENGTH     = 18;
    private static final int MAX_LOCATION_LENGTH = 21;
    private static final int MAX_HOST_LENGTH     = 27;

    public CompetitionManagerModel(CompetitionManagerView view) {
        this.view = view;
        DataAccess.setConn();

        // Populate year combo with current year through +5
        JComboBox<Integer> yearCb = view.getYearCB();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearCb.removeAllItems();
        for (int y = currentYear; y <= currentYear + 5; y++) {
            yearCb.addItem(y);
        }

        attachListeners();
    }

    /**
     * Ensures a valid database connection, reconnecting if necessary
     */
    private Connection connect() throws SQLException {
        Connection conn = DataAccess.getConnection();
        if (conn == null || conn.isClosed()) {
            DataAccess.setConn();
            conn = DataAccess.getConnection();
        }
        return conn;
    }

    /**
     * Attaches action listeners for "Cancel" and "Save" buttons
     */
    private void attachListeners() {
        view.getCancelButton().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    view,
                    "Canceling will delete the competition and all associated data. Continue?",
                    "Confirm Cancel",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                view.dispose();
                PersonnelDashboardView personnelDashboardView = new PersonnelDashboardView();
                new PersonnelDashboardModel(personnelDashboardView);
            }
        });
        view.getSaveButton().addActionListener(e -> saveCompetition());
    }

    /**
     * Validates input fields, creates a new competition record
     * and navigates to category management upon success
     */
    private void saveCompetition() {
        String name     = view.getNameTF().getText().trim();
        Integer year    = (Integer) view.getYearCB().getSelectedItem();
        String location = view.getLocationTF().getText().trim();
        String host     = view.getHostNameTF().getText().trim();

        // Required-field check
        if (name.isEmpty() || year == null || location.isEmpty() || host.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Please complete all required fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Length validations
        if (name.length() > MAX_NAME_LENGTH) {
            JOptionPane.showMessageDialog(view,
                    String.format("Competition Name may not exceed %d characters (entered %d).", MAX_NAME_LENGTH, name.length()),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (location.length() > MAX_LOCATION_LENGTH) {
            JOptionPane.showMessageDialog(view,
                    String.format("Location may not exceed %d characters (entered %d).", MAX_LOCATION_LENGTH, location.length()),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (host.length() > MAX_HOST_LENGTH) {
            JOptionPane.showMessageDialog(view,
                    String.format("Host Name may not exceed %d characters (entered %d).", MAX_HOST_LENGTH, host.length()),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Build competition ID
        compId = "C" + year;

        // Check for duplicate ID
        String checkSql = "SELECT COUNT(*) FROM competition WHERE Competition_ID = ?";
        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, compId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(view,
                            "Competition ID '" + compId + "' already exists.",
                            "Duplicate Competition",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view,
                    "Error checking existing competition: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insert competition
        LocalDate startDate = LocalDate.now();
        String insertSql = """
            INSERT INTO competition
            (Competition_ID, Year, Competition_Name, Location, Host_name, Starting_Date)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(insertSql)) {

            pst.setString(1, compId);
            pst.setInt(2, year);
            pst.setString(3, name);
            pst.setString(4, location);
            pst.setString(5, host);
            pst.setDate(6, Date.valueOf(startDate));
            pst.executeUpdate();

            JOptionPane.showMessageDialog(view,
                    "Competition '" + compId + "' created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Proceed to category manager
            view.dispose();
            CategoryManagerView catView = new CategoryManagerView();
            new CategoryManagerModel(catView, compId);
            catView.setCompetitionName(compId);
            catView.setVisible(true);

        } catch (SQLException ex) {
            String msg = ex.getMessage();
            if (msg.contains("Data too long for column 'Competition_Name'")) {
                JOptionPane.showMessageDialog(view,
                        "Competition Name too long (max " + MAX_NAME_LENGTH + " chars).",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (msg.contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(view,
                        "Competition '" + compId + "' already exists.",
                        "Duplicate Competition",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view,
                        "Database error: " + msg,
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
