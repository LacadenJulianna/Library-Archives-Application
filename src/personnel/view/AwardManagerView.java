package src.personnel.view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AwardManagerView extends JFrame {
    private JPanel MainPanel;
    private JLabel jLabelCompetitionName;
    private JComboBox comboBoxCategory;
    private JButton okayButton;
    private JLabel jLabel1stAuthor;
    private JLabel jLabel1stTitle;
    private JLabel jLabel1stVotes;
    private JLabel jLabel1stRating;
    private JLabel jLabel2ndTitle;
    private JLabel jLabel3rdTitle;
    private JLabel jLabel2ndAuthor;
    private JLabel jLabel2ndVotes;
    private JLabel jLabel2ndRating;
    private JLabel jLabel3rdRating;
    private JLabel jLabel3rdVotes;
    private JLabel jLabel3rdAuthor;


    public AwardManagerView(){
        setContentPane(MainPanel);
        setTitle("Awardees");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700,400);
        setLocationRelativeTo(null);
        setVisible(true);

        // Set competition name
        jLabelCompetitionName.setText("Competition Name");

        // action listener for okay button
        okayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { dispose();}
        });


    }
    // Set competition name label
    public void setCompetitionName(String name) {
        jLabelCompetitionName.setText(name);
    }

    // Set selected category in combo box
    public void setSelectedCategory(String category) {
        comboBoxCategory.setSelectedItem(category);
    }

    // Combo box for category selection
    public JComboBox getComboBoxCategory() {
        return comboBoxCategory;
    }

    // Label showing competition name
    public JLabel getCompetitionNameLabel() {
        return jLabelCompetitionName;
    }


    // Button to confirm selection
    public JButton getOkayButton() {
        return okayButton;
    }

    public JLabel getjLabel1stAuthor() {
        return jLabel1stAuthor;
    }

    public void setjLabel1stAuthor(JLabel jLabel1stAuthor) {
        this.jLabel1stAuthor = jLabel1stAuthor;
    }

    public JLabel getjLabel1stTitle() {
        return jLabel1stTitle;
    }

    public void setjLabel1stTitle(JLabel jLabel1stTitle) {
        this.jLabel1stTitle = jLabel1stTitle;
    }

    public JLabel getjLabel1stVotes() {
        return jLabel1stVotes;
    }

    public void setjLabel1stVotes(JLabel jLabel1stVotes) {
        this.jLabel1stVotes = jLabel1stVotes;
    }

    public JLabel getjLabel1stRating() {
        return jLabel1stRating;
    }

    public void setjLabel1stRating(JLabel jLabel1stRating) {
        this.jLabel1stRating = jLabel1stRating;
    }

    public JLabel getjLabel2ndTitle() {
        return jLabel2ndTitle;
    }

    public void setjLabel2ndTitle(JLabel jLabel2ndTitle) {
        this.jLabel2ndTitle = jLabel2ndTitle;
    }

    public JLabel getjLabel3rdTitle() {
        return jLabel3rdTitle;
    }

    public void setjLabel3rdTitle(JLabel jLabel3rdTitle) {
        this.jLabel3rdTitle = jLabel3rdTitle;
    }

    public JLabel getjLabel2ndAuthor() {
        return jLabel2ndAuthor;
    }

    public void setjLabel2ndAuthor(JLabel jLabel2ndAuthor) {
        this.jLabel2ndAuthor = jLabel2ndAuthor;
    }

    public JLabel getjLabel2ndVotes() {
        return jLabel2ndVotes;
    }

    public void setjLabel2ndVotes(JLabel jLabel2ndVotes) {
        this.jLabel2ndVotes = jLabel2ndVotes;
    }

    public JLabel getjLabel2ndRating() {
        return jLabel2ndRating;
    }

    public void setjLabel2ndRating(JLabel jLabel2ndRating) {
        this.jLabel2ndRating = jLabel2ndRating;
    }

    public JLabel getjLabel3rdRating() {
        return jLabel3rdRating;
    }

    public void setjLabel3rdRating(JLabel jLabel3rdRating) {
        this.jLabel3rdRating = jLabel3rdRating;
    }

    public JLabel getjLabel3rdVotes() {
        return jLabel3rdVotes;
    }

    public void setjLabel3rdVotes(JLabel jLabel3rdVotes) {
        this.jLabel3rdVotes = jLabel3rdVotes;
    }

    public JLabel getjLabel3rdAuthor() {
        return jLabel3rdAuthor;
    }

    public void setjLabel3rdAuthor(JLabel jLabel3rdAuthor) {
        this.jLabel3rdAuthor = jLabel3rdAuthor;
    }

}
