package src.personnel.model;

import src.databaseConnection.DataAccess;
import src.personnel.view.NomineesManagerView;
import src.personnel.view.PersonnelDashboardView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static src.databaseConnection.DataAccess.closeCon;
import static src.databaseConnection.DataAccess.setConn;

/**
 * Handles database operations and business logic for nominee selection process
 * Communicates with the NomineesManagerView for UI interactions
 */
public class NomineesManagerModel {
    private static final int MAX_NOMINEES = 10;        // ← max nominees per category

    private final NomineesManagerView view;
    private final String competitionId;

    public NomineesManagerModel(NomineesManagerView view, String competitionId) {
        this.view = view;
        this.competitionId = competitionId;
        setConn();
        loadCategories();
        attachListeners();
        updateAddButtonState();                       // ← initial enable/disable
    }

    private void loadCategories() {
        try {
            setConn();
            String sql = "SELECT Category_Name FROM category WHERE Competition_ID = ? " +
                    "AND Category_ID NOT IN (SELECT DISTINCT Category_ID FROM nominee WHERE Competition_ID = ?)";
            PreparedStatement pst = DataAccess.getConnection().prepareStatement(sql);
            pst.setString(1, competitionId);
            pst.setString(2, competitionId);
            ResultSet rs = pst.executeQuery();

            List<String> categories = new ArrayList<>();
            while (rs.next()) {
                categories.add(rs.getString("Category_Name"));
            }

            String[] arr = categories.toArray(new String[0]);
            view.getCategories().setModel(new DefaultComboBoxModel<>(arr));
            if (arr.length > 0) {
                view.getCategories().setSelectedIndex(0);
                updateTablesForCategory(getSelectedCategoryId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Failed to load categories:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attachListeners() {
        view.getCategories().addActionListener(e -> updateTablesForCategory(getSelectedCategoryId()));
        view.getRemoveNominee().addActionListener(e -> {
            removeSelectedNominee();
            updateAddButtonState();                   // ← re-enable if below max
        });
        view.getAddCandidate().addActionListener(e -> {
            addSelectedCandidate();
            updateAddButtonState();                   // ← disable if hit max
        });
        view.getCancelButton().addActionListener(e -> returnToDashboard());
        view.getAddNomineesButton().addActionListener(e -> addNominees());
        view.getFinishButton().addActionListener(e -> finish());
    }

    private String getSelectedCategoryId() {
        String selectedName = (String) view.getCategories().getSelectedItem();
        if (selectedName == null) return null;
        try {
            setConn();
            String sql = "SELECT Category_ID FROM category WHERE Competition_ID = ? AND Category_Name = ?";
            PreparedStatement stmt = DataAccess.getConnection().prepareStatement(sql);
            stmt.setString(1, competitionId);
            stmt.setString(2, selectedName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void updateTablesForCategory(String categoryId) {
        if (categoryId == null) return;
        view.getNominees().setModel(buildTableModel(categoryId, MAX_NOMINEES, 0));
        view.getCandidates().setModel(buildTableModel(categoryId, MAX_NOMINEES, MAX_NOMINEES));
        updateAddButtonState();                       // ← ensure button matches current table
    }

    private DefaultTableModel buildTableModel(String categoryId, int limit, int offset) {
        String[] columns = {"ISBN", "Title", "Author", "Genre", "Publication Date", "Page Count", "Ratings"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        try {
            setConn();
            String q1 = "SELECT Category_Name FROM category WHERE Category_ID = ?";
            PreparedStatement p1 = DataAccess.getConnection().prepareStatement(q1);
            p1.setString(1, categoryId);
            ResultSet r1 = p1.executeQuery();
            if (!r1.next()) return model;
            String categoryName = r1.getString(1);
            if (categoryName == null || categoryName.isBlank()) return model;

            boolean all = "Book of the Year".equals(categoryName);
            String sql = all
                    ? "SELECT * FROM book WHERE Rating>=8 ORDER BY Rating DESC LIMIT ? OFFSET ?"
                    : "SELECT * FROM book WHERE Genre=? AND Rating>=8 ORDER BY Rating DESC LIMIT ? OFFSET ?";

            PreparedStatement stmt = DataAccess.getConnection().prepareStatement(sql);
            int idx = 1;
            if (!all) {
                String genre = categoryName.replaceFirst("^Best in ", "");
                stmt.setString(idx++, genre);
            }
            stmt.setInt(idx++, limit);
            stmt.setInt(idx, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[columns.length];
                for (int i = 0; i < columns.length; i++) row[i] = rs.getObject(i + 1);
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    private void updateAddButtonState() {
        int count = view.getNominees().getRowCount();
        view.getAddCandidate().setEnabled(count < MAX_NOMINEES);
    }

    private void removeSelectedNominee() {
        int r = view.getNominees().getSelectedRow();
        if (r >= 0) {
            ((DefaultTableModel) view.getNominees().getModel()).removeRow(r);
        } else {
            JOptionPane.showMessageDialog(view, "Select a nominee to remove.");
        }
    }

    private void addSelectedCandidate() {
        int r = view.getCandidates().getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(view, "Select a candidate to add.");
            return;
        }
        DefaultTableModel cM = (DefaultTableModel) view.getCandidates().getModel();
        DefaultTableModel nM = (DefaultTableModel) view.getNominees().getModel();
        Object[] data = new Object[cM.getColumnCount()];
        for (int i = 0; i < data.length; i++) {
            data[i] = cM.getValueAt(r, i);
        }
        nM.addRow(data);
        cM.removeRow(r);
        view.getCandidates().clearSelection();
    }

    private void addNominees() {
        try {
            setConn();
            DefaultTableModel nM = (DefaultTableModel) view.getNominees().getModel();
            String ins = "INSERT INTO nominee VALUES (?, ?, ?, ?)";
            PreparedStatement pst = DataAccess.getConnection().prepareStatement(ins);
            String getRating = "SELECT Rating FROM book WHERE ISBN = ?";
            PreparedStatement pr = DataAccess.getConnection().prepareStatement(getRating);

            for (int i = 0; i < nM.getRowCount(); i++) {
                String isbn = nM.getValueAt(i, 0).toString();
                pr.setString(1, isbn);
                ResultSet rr = pr.executeQuery();
                double rating = rr.next() ? rr.getDouble(1) : 0;

                pst.setString(1, getSelectedCategoryId());
                pst.setLong(2, Long.parseLong(isbn));
                pst.setInt(3, 0);
                pst.setDouble(4, rating);
                pst.executeUpdate();
            }
            // once saved, remove this category from the combo
            view.getCategories().removeItemAt(view.getCategories().getSelectedIndex());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void returnToDashboard() {
        int c = JOptionPane.showConfirmDialog(
                view, "Canceling will delete the entire competition progress. Continue?",
                "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try (Connection conn = DataAccess.getConnection()) {
            PreparedStatement d = conn.prepareStatement("DELETE FROM competition WHERE Competition_ID = ?");
            PreparedStatement dC = conn.prepareStatement("DELETE FROM category WHERE Competition_ID = ?");
            PreparedStatement dN = conn.prepareStatement("DELETE FROM nominee WHERE Category_ID LIKE ?");

            PreparedStatement getYear = conn.prepareStatement("SELECT Year FROM competition WHERE Competition_ID = ?");
            getYear.setString(1, competitionId);
            ResultSet rs = getYear.executeQuery();
            int year = (rs.next()) ? rs.getInt(1) : 0;

            d.setString(1, competitionId);
            dC.setString(1, competitionId);
            dN.setString(1, "%" + year);
            if (d.executeUpdate() > 0 &&
                    dC.executeUpdate() > 0 &&
                    dN.executeUpdate() > 0)
            {
                JOptionPane.showMessageDialog(view,
                        "Competition and all related data deleted successfully",
                        "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error deleting competition: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        view.dispose();
        PersonnelDashboardView dash = new PersonnelDashboardView();
        new PersonnelDashboardModel(dash);
    }

    private void finish() {
        if (view.getCategories().getItemCount() > 0) {
            JOptionPane.showMessageDialog(view,
                    "There are still categories to nominate books for.",
                    "Unfinished nomination", JOptionPane.WARNING_MESSAGE);
            loadCategories();
            return;
        }
        try { closeCon(); } catch (Exception ignored) {}
        view.dispose();
        PersonnelDashboardView dash = new PersonnelDashboardView();
        new PersonnelDashboardModel(dash);
    }
}
