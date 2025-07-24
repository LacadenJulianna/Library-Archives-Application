package src.guest.view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RateManagerView extends JFrame {
    private JLabel jLabelBookTitle;
    private JSpinner RatingSpinner;
    private JButton cancelButton;
    private JButton okayButton;
    private JPanel MainPanel;
    private JTextField textFieldAuthor;
    private JTextField textFieldPubDate;
    private JTextField textFieldPgCount;
    private JTextField textFieldGenre;
    private JTextField textFieldISBN;
    private JLabel labelISBN;
    private JLabel labelAuthor;
    private JLabel labelGenre;
    private JLabel labelPubDate;
    private JLabel labelPageCount;

    public RateManagerView () {
        setContentPane(MainPanel);
        setTitle("Rate Book");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500,400);
        setLocationRelativeTo(null);
        setVisible(true);

        // Set default book title label
        jLabelBookTitle.setText("Book Title");

        // Disable editing of book information fields
        textFieldISBN.setEditable(false);
        textFieldAuthor.setEditable(false);
        textFieldGenre.setEditable(false);
        textFieldPubDate.setEditable(false);
        textFieldPgCount.setEditable(false);

    }

    // Setters
    public void setBookTitle(String title) {
        jLabelBookTitle.setText(title);
    }

    public void setISBN(String isbn) {
        textFieldISBN.setText(isbn);
    }

    public void setAuthor(String author) {
        textFieldAuthor.setText(author);
    }

    public void setGenre(String genre) {
        textFieldGenre.setText(genre);
    }

    public void setPublicationDate(String pubDate) {
        textFieldPubDate.setText(pubDate);
    }

    public void setPageCount(String pgCount) {
        textFieldPgCount.setText(pgCount);
    }

    public void setRatingValue(SpinnerNumberModel value) {
        RatingSpinner.setValue(value);
    }

    public void setCancelButton(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    public void setOkayButton(ActionListener listener) {
        okayButton.addActionListener(listener);
    }

    // Getters
    public JLabel getBookTitleLabel() {
        return jLabelBookTitle;
    }

    public JTextField getTextFieldISBN() {
        return textFieldISBN;
    }

    public JTextField getTextFieldAuthor() {
        return textFieldAuthor;
    }

    public JTextField getTextFieldGenre() {
        return textFieldGenre;
    }

    public JTextField getTextFieldPubDate() {
        return textFieldPubDate;
    }

    public JTextField getTextFieldPgCount() {
        return textFieldPgCount;
    }

    public JSpinner getRatingSpinner() {
        return RatingSpinner;
    }

    public JButton getOkayButton() {
        return okayButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

}