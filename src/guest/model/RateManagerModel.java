package src.guest.model;

import src.databaseConnection.DataAccess;
import src.guest.view.GuestBookCatalogView;
import src.guest.view.RateManagerView;
import src.utilities.Book;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static src.databaseConnection.DataAccess.*;

public class RateManagerModel {
    static RateManagerView view;
    private Book selectedBook;
    private String userEmail;

    public RateManagerModel(RateManagerView view, Long selectedBookISBN, String userEmail) {
        this.view = view;
        this.userEmail = userEmail;

        view.setCancelButton(new CancelListener());
        view.setOkayButton(new OkayListener());

        //limit spinner values
        SpinnerModel value = new SpinnerNumberModel(5, 1, 10, 1);
        view.getRatingSpinner().setModel(value);

        try {
            setConn();

            String query = "SELECT * FROM book WHERE ISBN = ?";
            PreparedStatement stmt = DataAccess.getConnection().prepareStatement(query);
            stmt.setLong(1, selectedBookISBN);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                selectedBook = new Book(
                        Long.parseLong(rs.getString(1)),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        Integer.parseInt(rs.getString(5)),
                        Integer.parseInt(rs.getString(6)),
                        Double.parseDouble(rs.getString(7))
                );
            closeCon();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        view.setBookTitle(selectedBook.getTitle());
        view.setISBN(Long.toString(selectedBook.getISBN()));
        view.setAuthor(selectedBook.getAuthor());
        view.setGenre(selectedBook.getGenre());
        view.setPublicationDate(Integer.toString(selectedBook.getPublicationDate()));
        view.setPageCount(Integer.toString(selectedBook.getPageCount()));
    }

    private class CancelListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.dispose();
            GuestBookCatalogView catalogView = new GuestBookCatalogView();
            new GuestBookCatalogModel(catalogView, userEmail);
        }
    }

    private class OkayListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int rating = (Integer) view.getRatingSpinner().getValue();
            System.out.println("Rating is " + rating);

            try {
                setConn();

                //update record's rating attribute in books
                CallableStatement addRatingQuery = DataAccess.getConnection().prepareCall("{CALL add_rating(?, ?, ?)}");
                addRatingQuery.setString(1, userEmail);
                addRatingQuery.setLong(2, selectedBook.getISBN());
                addRatingQuery.setInt(3, rating);
                addRatingQuery.executeUpdate();

                //update record's rating attribute in books
                CallableStatement updateRatingQuery = DataAccess.getConnection().prepareCall("{CALL update_rating(?)}");
                updateRatingQuery.setString(1, Long.toString(selectedBook.getISBN()));
                updateRatingQuery.executeUpdate();

                closeCon();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            view.dispose();
            GuestBookCatalogView catalogView = new GuestBookCatalogView();
            new GuestBookCatalogModel(catalogView, userEmail);
        }
    }
}
