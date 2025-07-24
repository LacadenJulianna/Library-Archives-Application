package src.guest.model;

import src.databaseConnection.DataAccess;
import src.guest.view.GuestDashboardView;
import src.guest.view.VoteSelectionView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static src.databaseConnection.DataAccess.closeCon;
import static src.databaseConnection.DataAccess.setConn;


/**
 * should add this to the schema constraints pala
 *
 * ALTER TABLE uservote
 * ADD CONSTRAINT unique_vote UNIQUE (User_Email, Category_ID);
 */
public class VoteSelectionModel {
    static VoteSelectionView view;
    private List<Long> isbnTracker;
    private List<String> categoryIDList;
    private Long[] votedNominees;
    private int selectedIndex;
    private static String userEmail;
    private static String competitionID;

    public VoteSelectionModel(VoteSelectionView view, String userEmail) {
        this.userEmail = userEmail;
        this.view = view;
        this.categoryIDList = new ArrayList<>();

        setCompetitionLabel();
        setCategoryDropdown();
        votedNominees = new Long[categoryIDList.size()];

        view.setVoteButton(new VoteButtonListener());
        view.setCancelButton(new CancelListener());
        view.setFinishButton(new FinishButtonListener());

        // connected to dropdown
        view.getComboBoxCategory().addItemListener(new CategoryDropdownHandler());

        // Track row selection
        view.getJTableBooks().getSelectionModel().addListSelectionListener(e -> handleRowSelection());
    }

    private void setCompetitionLabel() {
        try {
            setConn();
            String query = "SELECT Competition_Name, Competition_ID FROM competition WHERE Ending_Date IS NULL";
            Statement stmt = DataAccess.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                view.getjLabelComp_Name().setText(rs.getString(1));//set competition name label
                competitionID = rs.getString(2);
            } else {
                view.getjLabelComp_Name().setText("No Ongoing Competition (how did you get here??)");
            }
            closeCon();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setCategoryDropdown() {
        List<String> categories = new ArrayList<>();

        try {
            setConn();

            String hasVotedQuery = "SELECT COUNT(*) FROM uservote " +
                    "WHERE User_Email = ? " +
                    "AND Category_ID = ?";
            PreparedStatement hasVotedStmt = DataAccess.getConnection().prepareStatement(hasVotedQuery);
            ResultSet hasVotedRS;

            String query = "SELECT Category_ID, Category_Name FROM category WHERE Competition_ID = ?";
            PreparedStatement stmt = DataAccess.getConnection().prepareStatement(query);
            stmt.setString(1, competitionID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                hasVotedStmt.setString(1, userEmail);
                hasVotedStmt.setString(2, rs.getString(1));
                hasVotedRS = hasVotedStmt.executeQuery();
                if (hasVotedRS.next()){
                    if (hasVotedRS.getString(1).equals("0")) {
                        categoryIDList.add(rs.getString(1));
                        categories.add(rs.getString(2));
                    }
                }
            }

            if (!categories.isEmpty()) {
                String querySet = "SELECT book.ISBN, book.Title, book.Author " +
                        "FROM book " +
                        "INNER JOIN nominee ON nominee.ISBN = book.ISBN " +
                        "WHERE nominee.Category_ID = ?";
                PreparedStatement stmtSet = DataAccess.getConnection().prepareStatement(querySet);
                stmtSet.setString(1, categoryIDList.get(view.getComboBoxCategory().getSelectedIndex()));
                ResultSet rsSet = stmtSet.executeQuery();
                populateTable(rsSet);
            } else {
                JOptionPane.showMessageDialog(view,
                        "You have already voted for all categories in this competition.\n",
                        "Vote Limit Reached",
                        JOptionPane.ERROR_MESSAGE);
                closeCon();
                view.dispose();
                GuestDashboardView guestView = new GuestDashboardView();
                new GuestDashboardModel(guestView, userEmail);
            }

            closeCon();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        String[] categoriesArray = categories.toArray(new String[0]);
        view.getComboBoxCategory().setModel(new DefaultComboBoxModel<>(categoriesArray));
    }

    private void handleRowSelection() {
        selectedIndex = view.getJTableBooks().getSelectedRow();
    }

    public class CategoryDropdownHandler implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            try {
                setConn();

                String query = "SELECT book.ISBN, book.Title, book.Author " +
                        "FROM book " +
                        "INNER JOIN nominee ON nominee.ISBN = book.ISBN " +
                        "WHERE nominee.Category_ID = ?";
                PreparedStatement stmt = DataAccess.getConnection().prepareStatement(query);
                stmt.setString(1, categoryIDList.get(view.getComboBoxCategory().getSelectedIndex()));
                ResultSet rs = stmt.executeQuery();
                populateTable(rs);

                closeCon();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void populateTable(ResultSet rs){
        DefaultTableModel tableModel = new DefaultTableModel();
        isbnTracker = new ArrayList<>();
        String[] columnNames = {"Book", "Author"};

        tableModel.setRowCount(0); // Clear table
        tableModel.setColumnIdentifiers(columnNames);

        try {
            setConn();

            while (rs.next()) {
                isbnTracker.add(Long.parseLong(rs.getString(1)));
                tableModel.addRow(
                        new Object[]{
                                rs.getString(2),
                                rs.getString(3)
                        }
                );
                view.getJTableBooks().setModel(tableModel);
            }

            closeCon();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private class CancelListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.dispose();
            GuestDashboardView dashboardView = new GuestDashboardView();
            new GuestDashboardModel(dashboardView, userEmail);
        }
    }

    /*
    having all database communication occur in the FinishButtonListener class
    allows users to change their votes as many times as they want
    before they press the finish button
     */
    private class VoteButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            votedNominees[view.getComboBoxCategory().getSelectedIndex()] = isbnTracker.get(selectedIndex);
            JOptionPane.showMessageDialog(view,
                    "Your vote has been noted!\n" +
                            "You can still change your vote before pressing the finish button",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class FinishButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean hasVotes = false;
            try {
                setConn();

                String query = "INSERT INTO uservote VALUES (?, ?, ?)";
                PreparedStatement stmt = DataAccess.getConnection().prepareStatement(query);
                for (int i = 0; i < categoryIDList.size(); i++) {
                    if (votedNominees[i] != null) {
                        stmt.setString(1, userEmail);
                        stmt.setString(2, categoryIDList.get(i));
                        stmt.setLong(3, votedNominees[i]);
                        stmt.executeUpdate();
                        hasVotes = true;


                        //update vote count
                        CallableStatement updateVoteQuery = DataAccess.getConnection().prepareCall("{CALL update_vote_count(?, ?)}");
                        updateVoteQuery.setString(1, categoryIDList.get(i));
                        updateVoteQuery.setLong(2, votedNominees[i]);
                        updateVoteQuery.executeUpdate();
                    }
                    System.out.println("[DEBUG]: inserted uservote row: "+userEmail+", "+categoryIDList.get(i)+", "+votedNominees[i]);
                }

                if (hasVotes) {
                    JOptionPane.showMessageDialog(view,
                            "Your votes have been recorded!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view,
                            "You haven't selected any books to vote for.",
                            "No Votes Submitted",
                            JOptionPane.WARNING_MESSAGE);
                }

                /*
                i made both options lead to the dashboard anyway para ndi malock si user
                i mean yeah pwede pa rin naman gamitin ung cancel button but it's more of a ux thing
                if that makes sense?
                 */
                closeCon();
                view.dispose();
                GuestDashboardView guestView = new GuestDashboardView();
                new GuestDashboardModel(guestView, userEmail);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
