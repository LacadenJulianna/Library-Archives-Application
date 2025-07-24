package src.personnel.view;

import src.personnel.model.NomineesManagerModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class NomineesManagerView extends JFrame {
    private JPanel topPanel;
    private JLabel addNominees, category, nomineesTableHeader, candidatesTableHeader;
    private JComboBox<String> categories;
    private JTable nominees, candidates;
    private JButton removeNominee, addCandidate, cancelButton, finishButton, addNomineesButton;

    public NomineesManagerView() {
        setContentPane(topPanel);
        setTitle("Nominees Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

    }

    // Getters
    public JPanel getTopPanel() {
        return topPanel;
    }

    public JLabel getAddNominees() {
        return addNominees;
    }

    public JLabel getCategory() {
        return category;
    }

    public JLabel getNomineesTableHeader() {
        return nomineesTableHeader;
    }

    public JLabel getCandidatesTableHeader() {
        return candidatesTableHeader;
    }

    public JComboBox<String> getCategories() {
        return categories;
    }

    public JTable getNominees() {
        return nominees;
    }

    public JTable getCandidates() {
        return candidates;
    }

    public JButton getRemoveNominee() {
        return removeNominee;
    }

    public JButton getAddCandidate() {
        return addCandidate;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getFinishButton() {
        return finishButton;
    }

    public JButton getAddNomineesButton() {
        return addNomineesButton;
    }

}
