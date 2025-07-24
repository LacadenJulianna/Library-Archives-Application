package src.personnel.model;

import src.databaseConnection.DataAccess;
import src.personnel.view.BookUploadView;
import src.personnel.view.PersonnelDashboardView;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.time.Year;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles the uploading of book data files and persists entries
 * into the database with insert-or-update semantics.
 * Reads CSV or txt files containing book details,
 * validates records, and batch-inserts or updates them in the 'book' table.
 */
public class BookUploadModel {
    private final BookUploadView view;

    /**
     * Initializes the model by setting up the database connection and
     */
    public BookUploadModel(BookUploadView view) {
        this.view = view;
        DataAccess.setConn();

        view.getReturnButton().addActionListener(e -> {
            view.dispose();
            PersonnelDashboardView dashView = new PersonnelDashboardView();
            new PersonnelDashboardModel(dashView);
            dashView.setVisible(true);
        });

        view.getUploadButton().addActionListener(e -> uploadFile());
    }

    /**
     * Processes the selected file by reading each line, validating fields,
     * Displays dialog messages on success or failure,
     * and resets the view state after completion.
     */
    private void uploadFile() {
        File file = view.getSelectedFile();
        if (file == null) {
            JOptionPane.showMessageDialog(view,
                    "No file selected.",
                    "Upload Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String delim = "[,\\t]";
        String sql = """
            INSERT INTO book
              (ISBN, Title, Author, Genre, Publication_Date, Page_Count, Rating)
            VALUES
              (?, ?, ?, ?, ?, ?, 0)
            """;

        try (Connection conn = DataAccess.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(sql);
                 BufferedReader br = new BufferedReader(new FileReader(file))) {

                String line;
                Set<Long> seenIsbns = new HashSet<>();

                while ((line = br.readLine()) != null) {
                    String[] cols = line.split(delim);
                    if (cols.length < 6) continue;

                    long isbn;
                    try {
                        isbn = Long.parseLong(cols[0].trim());
                        if (isbn <= 0) throw new NumberFormatException();
                    } catch (NumberFormatException nfe) {
                        continue;
                    }
                    if (!seenIsbns.add(isbn)) continue;

                    String title  = cols[1].trim();
                    String author = cols[2].trim();
                    String genre  = cols[3].trim();
                    if (title.isEmpty() || author.isEmpty() || genre.isEmpty()) continue;

                    int pubYear;
                    try {
                        pubYear = Integer.parseInt(cols[4].trim());
                        int current = Year.now().getValue();
                        if (pubYear < 1500 || pubYear > current) throw new NumberFormatException();
                    } catch (NumberFormatException nfe) {
                        continue;
                    }

                    int pages;
                    try {
                        pages = Integer.parseInt(cols[5].trim());
                        if (pages <= 0) throw new NumberFormatException();
                    } catch (NumberFormatException nfe) {
                        continue;
                    }

                    pst.setLong(1, isbn);
                    pst.setString(2, title);
                    pst.setString(3, author);
                    pst.setString(4, genre);
                    pst.setInt(5, pubYear);
                    pst.setInt(6, pages);

                    pst.addBatch();
                }

                int[] counts = pst.executeBatch();
                conn.commit();

                JOptionPane.showMessageDialog(view,
                        counts.length + " records processed successfully.",
                        "Upload Complete",
                        JOptionPane.INFORMATION_MESSAGE);

                view.getSelectedFileLabel().setText("Select a .csv or .txt file to upload");
                view.setSelectedFile(null);

            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view,
                    "Upload failed: " + ex.getMessage(),
                    "Upload Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
