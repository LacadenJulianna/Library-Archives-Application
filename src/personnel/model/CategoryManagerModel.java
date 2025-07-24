package src.personnel.model;

import src.databaseConnection.DataAccess;
import src.personnel.view.CategoryManagerView;
import src.personnel.view.NomineesManagerView;
import src.personnel.view.PersonnelDashboardView;

import javax.swing.*;
import java.sql.*;
import java.util.*;

import static src.databaseConnection.DataAccess.*;

/**
 * Provides functionality to load the competition context, persist selected categories
 * and handle user navigation (cancel or proceed to nominees management)
 */
public class CategoryManagerModel {

    private final CategoryManagerView view;
    private final String competitionId;

    /**
     * Constructs the model, initializes database connection, and sets up view listeners
     */
    public CategoryManagerModel(CategoryManagerView view, String competitionId) {
        this.view = view;
        this.competitionId = competitionId;

        DataAccess.setConn();
        view.setCompetitionName(competitionId);
        attachListeners();
    }

    /**
     * Ensures a valid database connection is available, reconnecting if necessary.
     */
    private Connection connect() throws SQLException {
        Connection conn = DataAccess.getConnection();
        if (conn == null || conn.isClosed()) {
            DataAccess.setConn();
            conn = DataAccess.getConnection();
        }
        return conn;
    }

    private void attachListeners() {
        view.getCancelButton().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    view,
                    "Canceling will delete any progress made. Continue?",
                    "Confirm Cancel",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteProgress();
                view.dispose();
            }
        });

        view.getNextButton().addActionListener(e -> saveCategories());
    }

    /**
     * Deletes the competition record and associated progress from the database
     */
    private void deleteProgress() {
        if (competitionId == null) {
            return;
        }
        String deleteCompSql = "DELETE FROM competition WHERE Competition_ID = ?";

        try (Connection conn = connect();
             PreparedStatement pstDelComp = conn.prepareStatement(deleteCompSql)) {

            pstDelComp.setString(1, competitionId);
            int countComp = pstDelComp.executeUpdate();

            JOptionPane.showMessageDialog(view,
                    countComp + " competition record/s deleted.",
                    "Cancel Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            view.dispose();
            PersonnelDashboardView personnelDashboardView = new PersonnelDashboardView();
            new PersonnelDashboardModel(personnelDashboardView);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Error deleting competition: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validates that at least one category is chosen, retrieves the competition year
     * and inserts a record for each selected category in the 'category' table
     * On success, transitions to the NomineesManagerView
     */
    private void saveCategories() {
        List<String> selected = view.getSelectedCategories();
        int year = 0;

        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Please select at least one category.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            setConn();

            // Retrieve competition year
            String query = "SELECT Year FROM competition WHERE Competition_ID = ?";
            PreparedStatement stmt = DataAccess.getConnection().prepareStatement(query);
            stmt.setString(1, competitionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                year = rs.getInt(1);
            }

            // Insert each selected category
            for (String catId : selected) {
                String categName = switch (catId) {
                    case "BOY" -> view.getBOYCheckbox().getText();
                    case "HOR" -> view.getHORCheckbox().getText();
                    case "CLA" -> view.getCLACheckbox().getText();
                    case "DRA" -> view.getDRACheckbox().getText();
                    case "CHI" -> view.getCHICheckbox().getText();
                    case "MYS" -> view.getMYSCheckbox().getText();
                    case "COM" -> view.getCOMCheckbox().getText();
                    case "FIC" -> view.getFICCheckbox().getText();
                    case "FAN" -> view.getFANCheckbox().getText();
                    case "HIS" -> view.getHISCheckbox().getText();
                    case "HIF" -> view.getHIFCheckbox().getText();
                    case "ROM" -> view.getROMCheckbox().getText();
                    case "SCI" -> view.getSCICheckbox().getText();
                    case "NON" -> view.getNONCheckbox().getText();
                    default -> throw new IllegalStateException("Unexpected category ID: " + catId);
                };

                String insertCategory = "INSERT INTO category VALUES (?, ?, ?)";
                PreparedStatement insertSTMT = DataAccess.getConnection().prepareStatement(insertCategory);
                insertSTMT.setString(1, catId + "-" + year);
                insertSTMT.setString(2, categName);
                insertSTMT.setString(3, competitionId);
                insertSTMT.executeUpdate();
            }

            closeCon();

            // Build and show success message
            StringBuilder message = new StringBuilder("Successfully added categories:\n");
            for (String catId : selected) {
                message.append("- ")
                        .append(switch (catId) {
                            case "BOY" -> view.getBOYCheckbox().getText();
                            case "HOR" -> view.getHORCheckbox().getText();
                            case "CLA" -> view.getCLACheckbox().getText();
                            case "DRA" -> view.getDRACheckbox().getText();
                            case "CHI" -> view.getCHICheckbox().getText();
                            case "MYS" -> view.getMYSCheckbox().getText();
                            case "COM" -> view.getCOMCheckbox().getText();
                            case "FIC" -> view.getFICCheckbox().getText();
                            case "FAN" -> view.getFANCheckbox().getText();
                            case "HIS" -> view.getHISCheckbox().getText();
                            case "HIF" -> view.getHIFCheckbox().getText();
                            case "ROM" -> view.getROMCheckbox().getText();
                            case "SCI" -> view.getSCICheckbox().getText();
                            case "NON" -> view.getNONCheckbox().getText();
                            default -> throw new IllegalStateException("Unexpected category ID: " + catId);
                        })
                        .append("\n");
            }

            JOptionPane.showMessageDialog(
                    view,
                    message.toString(),
                    "Categories Added",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Transition to nominees management
            view.dispose();
            NomineesManagerView nomView = new NomineesManagerView();
            new NomineesManagerModel(nomView, competitionId);
            nomView.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    view,
                    "Error saving categories:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
