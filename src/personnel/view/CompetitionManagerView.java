package src.personnel.view;

import javax.swing.*;

public class CompetitionManagerView extends JFrame {
    private JPanel CompetitionManager;
    private JLabel CreateCompetition, compName, year, compLocation, hostName, endDate;
    private JTextField nameTF, locationTF, hostNameTF;
    private JComboBox yearCB, endDateCB;
    private JButton cancelButton, saveButton;

    public JPanel getCompetitionManager() {
        return CompetitionManager;
    }
    public JLabel getCreateCompetition() {
        return CreateCompetition;
    }
    public JLabel getCompName() {
        return compName;
    }
    public JLabel getYear() {
        return year;
    }
    public JLabel getCompLocation() {
        return compLocation;
    }
    public JLabel getHostName() {
        return hostName;
    }
    public JLabel getEndDate() {
        return endDate;
    }
    public JTextField getNameTF() {
        return nameTF;
    }
    public JTextField getLocationTF() {
        return locationTF;
    }
    public JTextField getHostNameTF() {
        return hostNameTF;
    }
    public JComboBox getYearCB() {
        return yearCB;
    }
    public void setYearCB(JComboBox yearCB) {
        this.yearCB = yearCB;
    }
    public JComboBox getEndDateCB() {
        return endDateCB;
    }
    public JButton getCancelButton() {
        return cancelButton;
    }
    public JButton getSaveButton() {
        return saveButton;
    }

    public CompetitionManagerView() {
        setContentPane(CompetitionManager);
        setTitle("Competition Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(550, 350);
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
