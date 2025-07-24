package src.personnel.model;

import src.databaseConnection.DataAccess;
import src.personnel.view.AwardManagerView;
import src.utilities.Award;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static src.databaseConnection.DataAccess.closeCon;
import static src.databaseConnection.DataAccess.setConn;

/**
 * AwardManagerModel handles award data retrieval and management for a specific competition.
 */
public class AwardManagerModel {
    private final AwardManagerView view;
    private final String competitionId;

    public AwardManagerModel(AwardManagerView view, String competitionId) {
        this.view = view;
        this.competitionId = validateCompetitionId(competitionId);

        initView();
        loadCategories();
        attachListeners();

        if (view.getComboBoxCategory().getItemCount() > 0) {
            view.getComboBoxCategory().setSelectedIndex(0);
            updateAwards();
        } else {
            showInfo("No award categories found for competition: " + this.competitionId, "No Categories");
        }
    }

    private String validateCompetitionId(String id) {
        if (id == null || id.isBlank()) {
            showError("Invalid competition ID. Cannot load awards.", new IllegalArgumentException("competitionId must not be blank"));
            throw new IllegalArgumentException("competitionId must not be blank");
        }
        return id;
    }

    private void initView() {
        view.setCompetitionName(competitionId);
    }

    private Connection connect() throws SQLException {
        Connection conn = DataAccess.getConnection();
        if (conn == null || conn.isClosed()) {
            setConn();
            conn = DataAccess.getConnection();
        }
        return conn;
    }

    /**
     * Fetches available categories for this competition.
     */
    public List<String> getCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT Category_ID FROM category WHERE Competition_ID = ? ORDER BY Category_ID";
        try (Connection conn = connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, competitionId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String cat = rs.getString("Category_ID");
                    if (cat != null && !cat.isBlank()) categories.add(cat);
                }
            }
        }
        return categories;
    }

    /**
     * Retrieves top N awards.
     */
    public List<Award> getTopAwards(String categoryId){
        List<Award> list = new ArrayList<>();
        Long[] isbn;
        int j = 0;
        try {
            setConn();

            //retrieve winners based on vote count
            String query = "SELECT ISBN FROM nominee " +
                    "WHERE Category_ID = ? " +
                    "ORDER BY Vote_Count DESC, Rating_At_Nomination DESC " +
                    "LIMIT ?";
            PreparedStatement stmt = DataAccess.getConnection().prepareStatement(query);
            stmt.setString(1, categoryId);
            if (categoryId.contains("BOY")) {
                stmt.setInt(2, 1);
                isbn = new Long[1];
            } else {
                stmt.setInt(2, 3);
                isbn = new Long[3];
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                isbn[j] = rs.getLong(1);
                j++;
            }

            //insert winners into award table
            String insertQuery = "REPLACE INTO award VALUES (?, ?, ?, ?)"; //awardID, categID, isbn, ranking
            PreparedStatement insertStatement = DataAccess.getConnection().prepareStatement(insertQuery);
            for (int i = 0; i < isbn.length; i++) {
                insertStatement.setString(1, categoryId+"-"+(i+1));
                insertStatement.setString(2, categoryId);
                insertStatement.setLong(3, isbn[i]);
                insertStatement.setInt(4, i+1);
                insertStatement.executeUpdate();
            }


            //build list to return for display
            String displayQuery = """
                    SELECT award.Award_ID, award.Category_ID,
                    award.ISBN, book.Title, award.Ranking,
                    book.Author
                    FROM book INNER JOIN
                    award ON book.ISBN = award.ISBN
                    WHERE Category_ID = ?
                    ORDER BY 5""";
            PreparedStatement pst = DataAccess.getConnection().prepareStatement(displayQuery);
            pst.setString(1, categoryId);
            ResultSet displayRs = pst.executeQuery();
            while (displayRs.next()) {
                list.add(new Award(
                        displayRs.getString(1),
                        displayRs.getString(2),
                        displayRs.getLong(3),
                        displayRs.getString(4),
                        displayRs.getInt(5),
                        displayRs.getString(6))
                );
            }
            closeCon();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private void loadCategories() {
        try {
            List<String> cats = getCategories();
            JComboBox<String> cb = view.getComboBoxCategory();
            cb.removeAllItems();
            for (String cat : cats) cb.addItem(cat);
        } catch (Exception ex) {
            showError("Failed to load categories", ex);
        }
    }

    private void attachListeners() {
        view.getComboBoxCategory().addActionListener(e -> updateAwards());
        view.getOkayButton().addActionListener(e -> view.dispose());
    }

    private void updateAwards() {
        String cat = (String) view.getComboBoxCategory().getSelectedItem();
        clearAwardsDisplay();
        try {
            List<Award> awards = getTopAwards(cat);
            for (int i = 0; i < awards.size(); i++)
                displayAward(i, awards);
        } catch (Exception ex) {
            showError("Error fetching awards for category: " + cat, ex);
        }
    }

    private void displayAward(int index, List<Award> awards) {
        JLabel authorLabel, titleLabel, votes, rating;
        if (awards.size() == 1) {
            authorLabel = view.getjLabel1stAuthor();
            titleLabel = view.getjLabel1stTitle();
            votes = view.getjLabel1stVotes();
            rating = view.getjLabel1stRating();
        } else {
            switch (index) {
                case 0 -> {
                    authorLabel = view.getjLabel1stAuthor();
                    titleLabel = view.getjLabel1stTitle();
                    votes = view.getjLabel1stVotes();
                    rating = view.getjLabel1stRating();
                }
                case 1 -> {
                    authorLabel = view.getjLabel2ndAuthor();
                    titleLabel = view.getjLabel2ndTitle();
                    votes = view.getjLabel2ndVotes();
                    rating = view.getjLabel2ndRating();
                }
                default -> {
                    authorLabel = view.getjLabel3rdAuthor();
                    titleLabel = view.getjLabel3rdTitle();
                    votes = view.getjLabel3rdVotes();
                    rating = view.getjLabel3rdRating();
                }
            }
        }
        Award award = awards.get(index);
        String[] awardContent = new String[4];
        try {
            setConn();

            String query = "SELECT Vote_Count, Rating_At_Nomination FROM nominee " +
                    "WHERE Category_ID = ? " +
                    "AND ISBN = ?";
            PreparedStatement stmt = DataAccess.getConnection().prepareStatement(query);
            stmt.setString(1, award.getCategoryId());
            stmt.setLong(2, award.getISBN());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                awardContent[0] = award.getTitle();
                awardContent[1] = award.getAuthor();
                awardContent[2] = rs.getString(1);
                awardContent[3] = rs.getString(2);
            }
            closeCon();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        fillAwardRow(authorLabel, titleLabel, votes, rating, awardContent);
    }

    private void clearAwardsDisplay() {
        JLabel authorLabel, titleLabel, votes, rating;

        authorLabel = view.getjLabel1stAuthor();
        titleLabel = view.getjLabel1stTitle();
        votes = view.getjLabel1stVotes();
        rating = view.getjLabel1stRating();
        fillAwardRow(authorLabel, titleLabel, votes, rating, null);

        authorLabel = view.getjLabel2ndAuthor();
        titleLabel = view.getjLabel2ndTitle();
        votes = view.getjLabel2ndVotes();
        rating = view.getjLabel2ndRating();
        fillAwardRow(authorLabel, titleLabel, votes, rating, null);

        authorLabel = view.getjLabel3rdAuthor();
        titleLabel = view.getjLabel3rdTitle();
        votes = view.getjLabel3rdVotes();
        rating = view.getjLabel3rdRating();
        fillAwardRow(authorLabel, titleLabel, votes, rating, null);
    }

    private void fillAwardRow(JLabel authorLabel, JLabel titleLabel, JLabel isbnLabel, JLabel rankLabel, String[] award) {
        if (award != null) {
            authorLabel.setText(award[0]);
            titleLabel.setText(award[1]);
            isbnLabel.setText(award[2]);
            rankLabel.setText(award[3]);
        } else {
            authorLabel.setText("-"); titleLabel.setText("-"); isbnLabel.setText("-"); rankLabel.setText("0");
        }
    }

    private void showError(String message, Exception exception) {
        exception.printStackTrace();
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(view, message + "\n" + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
    }

    private void showInfo(String message, String title) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(view, message, title, JOptionPane.INFORMATION_MESSAGE));
    }
}
