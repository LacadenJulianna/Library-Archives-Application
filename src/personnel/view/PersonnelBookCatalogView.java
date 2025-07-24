package src.personnel.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class PersonnelBookCatalogView extends JFrame {
    private JLabel viewBookLabel, includeLabel, sortByLabel, fromCompetitionLabel;
    private JTable bookTable;
    private JButton mainMenuButton, searchButton, sortAndFilterButton, clearFilterButton;
    private JTextField searchField;
    private JComboBox<String> sortByJComboBox;
    private JRadioButton nomineeButton, awardeeButton, seeAllBooksButton;
    private JComboBox<Integer> competitionJComboBox;

    // Default headers
    private final String[] defaultHeaders = {"Title", "Author", "Genre", "Publication Date", "Page Count", "Ratings"};
    private final String[] competitionHeaders = {"Competition Name", "Category", "Title", "Author", "Ratings", "Vote Counts"};

    // Default sort options
    private final String[] defaultSorts = {"book title", "author", "genre", "publication date", "page count", "rating"};
    private final String[] competitionSorts = {"competition", "category", "title", "author", "rating", "vote counts"};

    private DefaultTableModel tableModel;

    public PersonnelBookCatalogView() {
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
        seeAllBooksButton = new JRadioButton("See All Books", true); // the default selected button

        // group the radio buttons
        ButtonGroup filterGroup = new ButtonGroup();
        filterGroup.add(nomineeButton);
        filterGroup.add(awardeeButton);
        filterGroup.add(seeAllBooksButton);

        // add action listener to the radio buttons in the model na lang

        // the dropdown for from competition
        fromCompetitionLabel = new JLabel("From competition: ");
        competitionJComboBox = new JComboBox<>();

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
        mainMenuButton = new JButton("main menu");

        bottomPanel.add(mainMenuButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public JTable getBookTable() {
        return bookTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getMainMenuButton() {
        return mainMenuButton;
    }

    public void setMainMenuButton(ActionListener listener){
        mainMenuButton.addActionListener(listener);
    }

    public JButton getSearchButton() {
        return searchButton;
    }
    public void setSearchButton(ActionListener listener){
        searchButton.addActionListener(listener);
    }

    public JButton getSortAndFilterButton() {
        return sortAndFilterButton;
    }
    public void setSortAndFilterButton(ActionListener listener){
        sortAndFilterButton.addActionListener(listener);
    }

    public JButton getClearFilterButton() {
        return clearFilterButton;
    }
    public void setClearFilterButton(ActionListener listener){
        clearFilterButton.addActionListener(listener);
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public String getSortByFilter() {
        return (String) sortByJComboBox.getSelectedItem();
    }

    public void setRadioButtonListener(ActionListener listener) {
        nomineeButton.addActionListener(listener);
        awardeeButton.addActionListener(listener);
        seeAllBooksButton.addActionListener(listener);
    }
    public void setCompetitionJComboBox(boolean enabled){
        competitionJComboBox.setEnabled(enabled);
    }

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

    public Integer getCompetitionJComboBox() {
        return (Integer) competitionJComboBox.getSelectedItem();
    }

    public String[] getDefaultHeaders() {
        return defaultHeaders;
    }

    public String[] getCompetitionHeaders() {
        return competitionHeaders;
    }

    public String[] getDefaultSorts() {
        return defaultSorts;
    }

    public String[] getCompetitionSorts() {
        return competitionSorts;
    }

    // the setter for the table header to trigger the header switch when the checkbox is selected
    public void setTableHeaders(String[] headers) {
        tableModel.setColumnIdentifiers(headers);
    }

    // same with the setTableHeader method but this will trigger the change for the list in dropdown
    public void setSortOptions(String[] sortOptions) {
        sortByJComboBox.setModel(new DefaultComboBoxModel<>(sortOptions));
    }

    // Update the competition years dropdown
    public void setCompetitionYears(List<Integer> years) {
        competitionJComboBox.removeAllItems();
        for (Integer year : years) {
            competitionJComboBox.addItem(year);
        }
    }

    // JOptionPane for showing an error message
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message,"Error", JOptionPane.ERROR_MESSAGE);
    }

    public void clearSearchField() {
        searchField.setText("");
    }

}
