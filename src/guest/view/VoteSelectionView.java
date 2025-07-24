package src.guest.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VoteSelectionView extends JFrame {
    private JPanel MainPanel;
    private JTable JTableBooks;
    private JLabel jLabelComp_Name;
    private JButton cancelButton;
    private JButton finishButton;
    private JPanel labelCompetition;
    private JPanel Books;
    private JPanel Category;
    private JPanel Table;
    private JComboBox<String> comboBoxCategory;
    private JButton voteButton;
    private JLabel jLabelComp_name;

    public VoteSelectionView() {
        setContentPane(MainPanel);
        setTitle("Vote book");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
// icoconnect ko pa ung scroll - jewel

        // Set Competition Name label
        //jLabelComp_Name.setText("Competition Name");

        // Sample data for the table (Books and Authors)
        String[] columnNames = {"Book", "Author"};
        Object[][] data = {
                {"Book Title 1", "Author 1"},
                {"Book Title 2", "Author 2"},
                {"Book Title 3", "Author 3"},

        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTableBooks.setModel(model);
        // Set the selection mode to allow only a single selection
        JTableBooks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Sample data for category JComboBox
        String[] categories = {"Best in History", "Best in Comedy", "Best in Romance", "Best in Science Fiction"};
        comboBoxCategory.setModel(new DefaultComboBoxModel<>(categories));

        // Set visibility
        setVisible(true);

    }

    // SETTERS
    public void setMainPanel(JPanel mainPanel) {
        MainPanel = mainPanel;
    }

    public void setJTableBooks(JTable jTableBooks) {
        JTableBooks = jTableBooks;
    }

    public void setJLabelComp_Name(JLabel jLabelComp_Name) {
        this.jLabelComp_Name = jLabelComp_Name;
    }

    public void setCancelButton(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    public void setVoteButton(ActionListener listener) {
        voteButton.addActionListener(listener);
    }

    public void setFinishButton(ActionListener listener) {
        finishButton.addActionListener(listener);
    }

    public void setLabelCompetition(JPanel labelCompetition) {
        this.labelCompetition = labelCompetition;
    }

    public void setBooks(JPanel books) {
        Books = books;
    }

    public void setCategory(JPanel category) {
        Category = category;
    }

    public void setTable(JPanel table) {
        Table = table;
    }

    public void setComboBoxCategory(JComboBox<String> comboBoxCategory) {
        this.comboBoxCategory = comboBoxCategory;
    }

    public void setVoteButton(JButton voteButton) {
        this.voteButton = voteButton;
    }

    // GETTERS
    public JPanel getMainPanel() {
        return MainPanel;
    }

    public JTable getJTableBooks() {
        return JTableBooks;
    }

    public JLabel getjLabelComp_Name() {
        return jLabelComp_Name;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getFinishButton() {
        return finishButton;
    }

    public JPanel getLabelCompetition() {
        return labelCompetition;
    }

    public JPanel getBooks() {
        return Books;
    }

    public JPanel getCategory() {
        return Category;
    }

    public JPanel getTable() {
        return Table;
    }

    public JComboBox<String> getComboBoxCategory() {
        return comboBoxCategory;
    }

    public JButton getVoteButton() {
        return voteButton;
    }
}
