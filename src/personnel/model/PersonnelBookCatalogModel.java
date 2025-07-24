package src.personnel.model;

import src.databaseConnection.DataAccess;
import src.personnel.view.PersonnelBookCatalogView;
import src.personnel.view.PersonnelDashboardView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static src.databaseConnection.DataAccess.closeCon;
import static src.databaseConnection.DataAccess.setConn; //set connection to the database

public class PersonnelBookCatalogModel {
    static PersonnelBookCatalogView view;
    private List<Long> isbnTracker;
    private Long selectedBookISBN;

    public PersonnelBookCatalogModel(PersonnelBookCatalogView view){
        PersonnelBookCatalogModel.view = view;
        view.setCompetitionJComboBox(false);
        this.view.setMainMenuButton(new MainMenuButtonListener());
        this.view.setSearchButton(new SearchButtonListener());
        this.view.setSortAndFilterButton(new SortAndFilterButtonListener());
        this.view.setClearFilterButton(new ClearFiltersButtonListener());
        //set radio button listeners to update dropdown behavior
        this.view.setRadioButtonListener(new RadioButtonListener());

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

    }

    private class MainMenuButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            view.dispose(); //dispose the current window
            PersonnelDashboardView dashboardView = new PersonnelDashboardView();
            new PersonnelDashboardModel(dashboardView);
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
            StringBuilder searchQuery = new StringBuilder("SELECT * FROM book "+
                    "WHERE LOWER(Title) LIKE ? "+
                    "OR LOWER(Author) LIKE ? ");
            if (awardeeSelected || nomineeSelected){
                searchQuery.append("OR LOWER(Competition_Name LIKE ? " +
                                    "OR LOWER(Category_Name) LIKE ?");
            }else{
                searchQuery.append("OR LOWER(Genre) LIKE ? " +
                                   "OR LOWER(Publication_Date LIKE ? ");
            }
            searchQuery.append("ORDER BY Title ASC");
            try {
                setConn();
                PreparedStatement statement = DataAccess.getConnection().prepareStatement(String.valueOf(searchQuery));
                String wildcardSearch = "%" + searchText + "%";
                statement.setString(1, wildcardSearch);
                statement.setString(2, wildcardSearch);
                statement.setString(3, wildcardSearch);
                statement.setString(4, wildcardSearch);

                ResultSet resultSet = statement.executeQuery();
                populateTable(resultSet);
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
            String selectedCompetitionYear = Integer.toString(view.getCompetitionJComboBox());
            String selectedSortBy = view.getSortByFilter();
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
                if (nomineeSelected || awardeeSelected) {
                    query.append(
                                    "OR LOWER(Competition_Name) LIKE ? " +
                                            "OR LOWER(Category_Name) LIKE ? " +
                                            " AND Year = ")
                            .append(selectedCompetitionYear);
                } else
                    query.append(
                            "OR LOWER(Genre) LIKE ? " +
                                    "OR LOWER(Publication_Date) LIKE ? "
                    );
            } else if (nomineeSelected || awardeeSelected)
                query.append("WHERE Year = ").append(selectedCompetitionYear);

            // Sorting logic based on selected criteria
            // defaultSorts[] in GuestBookCatalogView was modified to make the following code work
            selectedSortBy = selectedSortBy.replace(' ', '_');
            query.append(" ORDER BY ").append(selectedSortBy);

            //if sorted by pubDate/rating, books are sorted in descending order
            if (selectedSortBy.equals("Publication_Date") || selectedSortBy.equals("Rating"))
                query.append(" DESC");

            //if sorted by anything other than title, title is used for secondary sorting (ORDER BY Genre, Title)
            if (!selectedSortBy.equals("Title"))
                query.append(", Title");

            // Debugging: Print the constructed SQL query for inspection
            System.out.println("SQL Query: " + query);


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

            System.out.println("[DEBUG] Filters cleared, table reset.");
        }
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
        ResultSet resultSet = stmt.executeQuery("SELECT Year FROM `competition` ORDER BY Year");
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
