package src.personnel.model;

import src.databaseConnection.DataAccess;
import src.personnel.view.BookUploadView;
import src.personnel.view.CompetitionManagerView;
import src.personnel.view.PersonnelBookCatalogView;
import src.personnel.view.PersonnelDashboardView;
import src.personnel.view.AwardManagerView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;

import static src.databaseConnection.DataAccess.setConn;

/**
 * Handles business logic and navigation between different personnel management views.
 * Manages competition lifecycle operations including creation and closure.
 */
public class PersonnelDashboardModel {
    private final PersonnelDashboardView view;
    private String competitionId;

    public static void main(String[] args) {
        PersonnelDashboardView personnelDashboardView = new PersonnelDashboardView();
        new PersonnelDashboardModel(personnelDashboardView);
    }
    // Updated constructor to accept existing competitionId
    public PersonnelDashboardModel(PersonnelDashboardView view) {
        this.view = view;
        bindButtons();
    }

    /**
     * - Competition creation
     * - Book management
     * - Catalog viewing
     * - Competition closure
     */
    private void bindButtons() {
        view.setCreateCompetitionButton(new CreateCompetitionButtonListener());
        view.setAddBookButton(new AddBookButtonListener());
        view.setViewBooksButton(new ViewBooksButtonListener());
        view.setCloseCompetitionButton(new CloseCompetitionButtonListener());
    }

    private class CreateCompetitionButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.dispose();
            CompetitionManagerView compView = new CompetitionManagerView();
            new CompetitionManagerModel(compView);
            compView.setVisible(true);
        }
    }

    private class AddBookButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.dispose();
            BookUploadView bookView = new BookUploadView();
            new BookUploadModel(bookView);
            bookView.setVisible(true);
        }
    }

    private class ViewBooksButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.dispose();
            PersonnelBookCatalogView pvc = new PersonnelBookCatalogView();
            new PersonnelBookCatalogModel(pvc);
            pvc.setVisible(true);
        }
    }

    /**
     * - Validation of active competition
     * - Confirmation dialog
     * - Database update
     * - Navigation to award management
     */
    private class CloseCompetitionButtonListener implements ActionListener {
        /**
         * Handles competition closure process with confirmation
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                setConn();
                String query = "SELECT Competition_ID FROM competition " +
                        "WHERE Ending_Date IS NULL";
                Statement stmt = DataAccess.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next()){
                    competitionId = rs.getString(1);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            if (competitionId.isBlank()) {
                JOptionPane.showMessageDialog(view,
                        "No active competition to close.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                String details = getCompetitionDetails();
                int choice = showConfirmationDialog(details);
                if (choice != JOptionPane.YES_OPTION) return;
                closeCompetition(competitionId);
                navigateToAwardManager();
            }
        }

        /**
         * Retrieves competition details from database
         */
        private String getCompetitionDetails() {
            String sql = "SELECT Competition_Name, Starting_Date FROM competition WHERE Competition_ID = ?";
            try (Connection conn = DataAccess.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, competitionId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return String.format("ID: %s\nName: %s\nStart Date: %s",
                                competitionId,
                                rs.getString("Competition_Name"),
                                rs.getDate("Starting_Date"));
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return "Competition details unavailable";
        }

        /**
         * Shows confirmation dialog for competition closure
         */
        private int showConfirmationDialog(String details) {
            return JOptionPane.showOptionDialog(
                    view,
                    "Close the following competition?\n" + details,
                    "Close Competition",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new Object[]{"Proceed", "Cancel"},
                    "Cancel"
            );
        }

        /**
         * Closes competition in database by setting ending date to current date
         */
        private void closeCompetition(String compId) {
            setConn();
            try (Connection conn = DataAccess.getConnection()) {
                String updateSql = "UPDATE competition SET Ending_Date = ? WHERE Competition_ID = ?";
                try (PreparedStatement pst = conn.prepareStatement(updateSql)) {
                    pst.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                    pst.setString(2, compId);
                    pst.executeUpdate();
                }
            } catch (SQLException ex) {
                handleCloseError(ex);
            }
        }

        private void handleCloseError(SQLException ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(
                            view,
                            "Error closing competition:\n" + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE
                    )
            );
        }

        private void navigateToAwardManager() {
            view.dispose();
            AwardManagerView amv = new AwardManagerView();
            // pass competitionId to AwardManagerModel
            new AwardManagerModel(amv, competitionId);
            amv.setVisible(true);
        }
    }
}
