package src.guest.view;

import src.guest.model.GuestBookCatalogModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

public class GuestBookCatalogView extends JFrame implements Runnable {
    private JLabel viewBookLabel, includeLabel, sortByLabel, fromCompetitionLabel;
    private JTable bookTable;
    private JButton addRatingButton, mainMenuButton, searchButton, sortAndFilterButton, clearFilterButton;
    private JTextField searchField;
    private JComboBox<String> sortByJComboBox;
    private JRadioButton nomineeButton, awardeeButton, seeAllBooksButton;
    private JComboBox<Integer> competitionJComboBox;
    private List<Integer> years;
    private ButtonGroup filterGroup;

    // Default headers
    private final String[] defaultHeaders = {"Title", "Author", "Genre", "Publication Date", "Page Count", "Rating"};
    private final String[] nomineeHeaders = {"Competition Name", "Category Name", "Title", "Author", "Rating At Nomination", "Vote Count"};
    private final String[] awardeeHeaders = {"Competition Name", "Category Name", "Title", "Author", "Rating At Nomination", "Vote Count", "Ranking"};

    // Default sort options
    private final String[] defaultSorts = {"Title", "Author", "Genre", "Publication Date", "Page Count", "Rating"};
    private final String[] nomineeSorts = {"Competition Name", "Category Name", "Title", "Author", "Rating At Nomination", "Vote Count"};
    private final String[] awardeeSorts = {"Competition Name", "Category Name", "Title", "Author", "Rating At Nomination", "Vote Count", "Ranking"};

    private DefaultTableModel tableModel;

    public GuestBookCatalogView() {
        setTitle("Book Catalog");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        /** NORTH Panel - title and search (top) */
        JPanel topPanel = new JPanel(new BorderLayout());

        // title
        viewBookLabel = new JLabel("View Books", SwingConstants.CENTER);
        viewBookLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        topPanel.add(viewBookLabel, BorderLayout.CENTER);

        // Search Field and Button (right)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 25));

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        /** CENTER Panel - Book Table */
        tableModel = new DefaultTableModel(defaultHeaders, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // makes it so that the user can only select one row
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        /** EAST Panel - Sorting and Filtering */
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // sort & filter button
        sortAndFilterButton = new JButton("Sort & Filter");

        // the dropdown for sort by
        sortByLabel = new JLabel("Sort by:");
        sortByJComboBox = new JComboBox<>(defaultSorts);


        // button for clear filter
        clearFilterButton = new JButton("clear filters");

        // the radio buttons for filter
        includeLabel = new JLabel("Filter: ");
        nomineeButton = new JRadioButton("Nominee");
        awardeeButton = new JRadioButton("Awardee");
        seeAllBooksButton = new JRadioButton("All Books", true); // the default selected button

        // group the radio buttons
        filterGroup = new ButtonGroup();
        filterGroup.add(nomineeButton);
        filterGroup.add(awardeeButton);
        filterGroup.add(seeAllBooksButton);

        // add action listener to the radio buttons in the model na lang

        // the dropdown for from competition
        competitionJComboBox = new JComboBox<>();
        fromCompetitionLabel = new JLabel("From competition: ");

        // set the height of the combo box
        sortByJComboBox.setPreferredSize(new Dimension(150, 10));

        competitionJComboBox.setPreferredSize(new Dimension(150, 10));


        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(sortByLabel);
        rightPanel.add(sortByJComboBox);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(includeLabel);
        rightPanel.add(nomineeButton);
        rightPanel.add(awardeeButton);
        rightPanel.add(seeAllBooksButton);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(fromCompetitionLabel);
        rightPanel.add(competitionJComboBox);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(sortAndFilterButton);
        rightPanel.add(clearFilterButton);

        add(rightPanel, BorderLayout.EAST);

        /** SOUTH Panel - main menu and add rating */
        JPanel bottomPanel = new JPanel();
        addRatingButton = new JButton("add rating");
        mainMenuButton = new JButton("main menu");

        bottomPanel.add(addRatingButton);
        bottomPanel.add(mainMenuButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);



    }

    public void run() {

    }

    //SETTERS
    // setter for the radio buttons
    public void setNomineeButton(ItemListener listener) {
        nomineeButton.addItemListener(listener);
    }

    public void setAwardeeButton(ItemListener listener) {
        awardeeButton.addItemListener(listener);
    }

    public void setSeeAllBooksButton(ItemListener listener) {
        seeAllBooksButton.addItemListener(listener);
    }

    public void setMainMenuButton(ActionListener listener) {
        mainMenuButton.addActionListener(listener);
    }

    public void setAddRatingButton(ActionListener listener) {
        addRatingButton.addActionListener(listener);
    }

    public void setSearchButton(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void setSortAndFilterButton(ActionListener listener) {
        sortAndFilterButton.addActionListener(listener);
    }

    public void setClearFilterButton(ActionListener listener) {
        clearFilterButton.addActionListener(listener);
    }

    // the setter for the table header to trigger the header switch when the checkbox is selected
    public void setTableHeaders(String[] headers) {
        tableModel.setColumnIdentifiers(headers);
    }

    // same with the setTableHeader method but this will trigger the change for the list in dropdown
    public void setSortOptions(String[] sortOptions) {
        sortByJComboBox.setModel(new DefaultComboBoxModel<>(sortOptions));
    }

    public void setRadioButtonListener(ActionListener listener) {
        nomineeButton.addActionListener(listener);
        awardeeButton.addActionListener(listener);
        seeAllBooksButton.addActionListener(listener);
    }
    public void setCompetitionJComboBox(boolean enabled){
        competitionJComboBox.setEnabled(enabled);
    }

    // Update the competition years dropdown
    public void setCompetitionYears(List<Integer> years) {
        competitionJComboBox.removeAllItems();
        this.years = years;
        for (int y : years) {
            competitionJComboBox.addItem(y);
        }
    }

    //GETTERS
    // getters for the radio button
    public JRadioButton getNomineeButton() {
        return nomineeButton;
    }

    public JRadioButton getAwardeeButton() {
        return awardeeButton;
    }

    public JRadioButton getSeeAllBooksButton() {
        return seeAllBooksButton;
    }

    public JTable getBookTable() {
        return bookTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getAddRatingButton() {
        return addRatingButton;
    }

    public JButton getMainMenuButton() {
        return mainMenuButton;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JButton getSortAndFilterButton() {
        return sortAndFilterButton;
    }

    public JButton getClearFilterButton() {
        return clearFilterButton;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public String getSortByJComboBox() {
        return (String) sortByJComboBox.getSelectedItem();
    }

    public JComboBox<Integer> getCompetitionJComboBox() {
        return competitionJComboBox;
    }

    public Integer getCompetitionJComboBoxSelected() {
        return years.get(competitionJComboBox.getSelectedIndex());
    }

    public String[] getDefaultHeaders() {
        return defaultHeaders;
    }

    public String[] getDefaultSorts() {
        return defaultSorts;
    }

    // JOptionPane for showing an error message
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message,"Error", JOptionPane.ERROR_MESSAGE);
    }

    public void clearSearchField() {
        searchField.setText("");
    }

}
