package src.guest.model;

import src.databaseConnection.DataAccess;
import src.guest.view.GuestBookCatalogView;
import src.guest.view.GuestDashboardView;
import src.guest.view.RateManagerView;

import java.awt.event.ActionListener;
import java.sql.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static src.databaseConnection.DataAccess.closeCon;
import static src.databaseConnection.DataAccess.setConn;

public class GuestBookCatalogModel {
    static GuestBookCatalogView view;
    private List<Long> isbnTracker;
    private Long selectedBookISBN;
    private final String userEmail;

    public GuestBookCatalogModel(GuestBookCatalogView view, String userEmail) {
        GuestBookCatalogModel.view = view;
        this.userEmail = userEmail;

        view.setMainMenuButton(new MainMenuListener());
        view.setAddRatingButton(new AddRatingListener());
        view.setSearchButton(new SearchButtonListener());
        view.setSortAndFilterButton(new SortAndFilterButtonListener());
        view.setClearFilterButton(new ClearFiltersButtonListener());

        try {
            //Default table content: all books from database sorted by title
            setConn();
            String query = "SELECT * FROM book ORDER BY Title";
            Statement stmt = DataAccess.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            populateTable(rs);
            closeCon();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Track row selection
        view.getBookTable().getSelectionModel().addListSelectionListener(e -> handleRowSelection());

        //set radio button listeners to update dropdown behavior
        this.view.setRadioButtonListener(new RadioButtonListener());
    }

    public void populateTable(ResultSet rs) throws SQLException {
        DefaultTableModel tableModel = view.getTableModel();
        isbnTracker = new ArrayList<>();
        boolean awardeeSelected = view.getAwardeeButton().isSelected();

        tableModel.setRowCount(0); // Clear table
        setConn();

        while (rs.next()) {
            isbnTracker.add(Long.parseLong(rs.getString(1)));
            if (awardeeSelected) {
                tableModel.addRow(
                    new Object[]{
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8)
                    }
                );
            } else {
                //Default table layout
                // this is also used by the nominee table format since the tables have the same number of columns
                tableModel.addRow(
                    new Object[]{
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7)
                    }
                );
            }
        }

        rs.close();

        //updates the competition year dropdown
        List<Integer> years = new ArrayList<>();
        int temporary;
        Statement stmt = DataAccess.getConnection().createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT Year FROM `competition`ORDER BY Year");
        while (resultSet.next()) {
            temporary = Integer.parseInt(resultSet.getString(1));
            years.add(temporary);
        }
        view.setCompetitionYears(years);
    }

    private void handleRowSelection() {
        int selectedRow = view.getBookTable().getSelectedRow();
        if (selectedRow >= 0) {
            selectedBookISBN = isbnTracker.get(selectedRow);
            System.out.println("[DEBUG] Selected book: " + selectedBookISBN);
        } else
            selectedBookISBN = null;
    }

    private class MainMenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.dispose();
            GuestDashboardView dashboardView = new GuestDashboardView();
            new GuestDashboardModel(dashboardView, userEmail);
        }
    }

    private class AddRatingListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedBookISBN != null) {
                view.dispose();
                RateManagerView rateManagerView = new RateManagerView();
                new RateManagerModel(rateManagerView, selectedBookISBN, userEmail);
            }
        }
    }

    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchText = view.getSearchField().getText().trim().toLowerCase();
            boolean nomineeSelected = view.getNomineeButton().isSelected();
            boolean awardeeSelected = view.getAwardeeButton().isSelected();

            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Please enter a search term.", "Search", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder searchQuery = new StringBuilder("SELECT * FROM book " +
                    "WHERE LOWER(Title) LIKE ? " +
                    "OR LOWER(Author) LIKE ? ");

            if (awardeeSelected || nomineeSelected) { //allows user to search competitions and categories
                searchQuery.append(
                        "OR LOWER(Competition_Name) LIKE ? " +
                        "OR LOWER(Category_Name) LIKE ? ");
            } else {
                searchQuery.append(
                        "OR LOWER(Genre) LIKE ? " +
                        "OR LOWER(Publication_Date) LIKE ? ");
            }

            searchQuery.append("ORDER BY Title ASC");

            try {
                setConn();

                PreparedStatement stmt = DataAccess.getConnection().prepareStatement(String.valueOf(searchQuery));
                // Set search parameters for all 4 fields
                String wildcardSearch = "%" + searchText + "%";
                stmt.setString(1, wildcardSearch);
                stmt.setString(2, wildcardSearch);
                stmt.setString(3, wildcardSearch);
                stmt.setString(4, wildcardSearch);

                ResultSet rs = stmt.executeQuery();
                populateTable(rs);
                System.out.println("[DEBUG] Search matched " + isbnTracker.size() + " books for: " + searchText);

                closeCon();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "Database error occurred while searching.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }



    private class SortAndFilterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean nomineeSelected = view.getNomineeButton().isSelected();
            boolean awardeeSelected = view.getAwardeeButton().isSelected();
            String selectedCompetitionYear = Integer.toString(view.getCompetitionJComboBoxSelected());  // Assuming this gives a valid year from the `competition` table
            String selectedSortBy = view.getSortByJComboBox();
            StringBuilder query = new StringBuilder();
            String searchText = view.getSearchField().getText().trim().toLowerCase();
            ResultSet rs;
            // Default headers
            String[] defaultHeaders = {"Title", "Author", "Genre", "Publication Date", "Page Count", "Rating"};
            String[] nomineeHeaders = {"Competition Name", "Category Name", "Title", "Author", "Rating At Nomination", "Vote Count"};
            String[] awardeeHeaders = {"Competition Name", "Category Name", "Title", "Author", "Rating At Nomination", "Vote Count", "Ranking"};

            // Default sort options
            String[] defaultSorts = {"Title", "Author", "Genre", "Publication Date", "Page Count", "Rating"};
            String[] nomineeSorts = {"Competition Name", "Category Name", "Title", "Author", "Rating At Nomination", "Vote Count"};
            String[] awardeeSorts = {"Competition Name", "Category Name", "Title", "Author", "Rating At Nomination", "Vote Count", "Ranking"};

            if (awardeeSelected) {
                query.append("SELECT * FROM t_awardee ");
                view.setTableHeaders(awardeeHeaders);
                view.setSortOptions(awardeeSorts);
            } else if (nomineeSelected) {
                query.append("SELECT * FROM t_nominee ");
                view.setTableHeaders(nomineeHeaders);
                view.setSortOptions(nomineeSorts);
            } else {
                query.append("SELECT * FROM book ");
                view.setTableHeaders(defaultHeaders);
                view.setSortOptions(defaultSorts);
            }

            if (!searchText.isEmpty()) {
                query.append(
                        "WHERE LOWER(Title) LIKE ? " +
                        "OR LOWER(Author) LIKE ? "
                );
                if (nomineeSelected || awardeeSelected)
                    query.append(
                            "OR LOWER(Competition_Name) LIKE ? " +
                            "OR LOWER(Category_Name) LIKE ? " +
                            " AND Year = ")
                            .append(selectedCompetitionYear);
                else
                    query.append(
                            "OR LOWER(Genre) LIKE ? " +
                            "OR LOWER(Publication_Date) LIKE ? "
                    );
            } else
                if (nomineeSelected || awardeeSelected)
                    query.append("WHERE Year = ").append(selectedCompetitionYear);

            // Sorting logic based on selected criteria
            selectedSortBy = selectedSortBy.replace(' ','_');
            query.append(" ORDER BY ").append(selectedSortBy);

            //if sorted by pubDate/rating, books are sorted in descending order
            if (selectedSortBy.equals("Publication_Date") || selectedSortBy.equals("Rating"))
                query.append(" DESC");

            //if sorted by anything other than title, title is used for secondary sorting (ORDER BY Genre, Title)
            if (!selectedSortBy.equals("Title"))
                query.append(", Title");

            try {
                setConn();

                if (!searchText.isEmpty()) {
                    /*
                    this block of code allows users to use sort and filter in tandem with the search function
                    meaning they can apply sort/filter functions without it overwriting their search

                    ex. input "The" in the searchbar > press Search > default search is executed (all books, sorted by title)
                    select "genre" in sort dropdown > press Sort & Filter > only books containing "the" are sorted and shown
                     */
                    PreparedStatement stmt = DataAccess.getConnection().prepareStatement(String.valueOf(query));
                    String wildcardSearch = "%" + searchText + "%";
                    stmt.setString(1, wildcardSearch);
                    stmt.setString(2, wildcardSearch);
                    stmt.setString(3, wildcardSearch);
                    stmt.setString(4, wildcardSearch);
                    rs = stmt.executeQuery();
                } else {
                    Statement stmt = DataAccess.getConnection().createStatement();
                    rs = stmt.executeQuery(String.valueOf(query));
                }

                populateTable(rs);

                closeCon();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    private class ClearFiltersButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            view.getSearchField().setText("");
            view.getSeeAllBooksButton().setSelected(true);
            view.setTableHeaders(view.getDefaultHeaders());
            view.setSortOptions(view.getDefaultSorts());

            try {
                //Default table content: all books from database sorted by title
                setConn();
                String query = "SELECT * FROM book ORDER BY Title";
                Statement stmt = DataAccess.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query);
                populateTable(rs);
                closeCon();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    private class RadioButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //enable or disable the competition year dropdown based on the selected radio button
            if (view.getSeeAllBooksButton().isSelected()) {
                //Disable the year dropdown
                view.setCompetitionJComboBox(false); //disable if 'See all books'
            }else if (view.getNomineeButton().isSelected() || view.getAwardeeButton().isSelected()){
                view.setCompetitionJComboBox(true); //enable if 'Nominee' or 'Awardee'
            }
        }
    }

}
