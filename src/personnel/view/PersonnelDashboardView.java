package src.personnel.view;

import javax.swing.*;
import java.awt.event.ActionListener;

public class PersonnelDashboardView extends JFrame {
    public JPanel dashboardPanel, topPanel, midPanel;
    public JLabel topLabel;
    public JButton createCompetitionButton, addBookButton, viewBooksButton, closeCompetitionButton;

    public JPanel getDashboardPanel() {
        return dashboardPanel;
    }

    public void setDashboardPanel(JPanel dashboardPanel) {
        this.dashboardPanel = dashboardPanel;
    }

    public JPanel getTopPanel() {
        return topPanel;
    }

    public void setTopPanel(JPanel topPanel) {
        this.topPanel = topPanel;
    }

    public JPanel getMidPanel() {
        return midPanel;
    }

    public void setMidPanel(JPanel midPanel) {
        this.midPanel = midPanel;
    }

    public JLabel getTopLabel() {
        return topLabel;
    }

    public void setTopLabel(JLabel topLabel) {
        this.topLabel = topLabel;
    }

    public void setCreateCompetitionButton(ActionListener listener){
        createCompetitionButton.addActionListener(listener);
    }

    public void setAddBookButton(ActionListener listener){
        addBookButton.addActionListener(listener);
    }

    public void setViewBooksButton(ActionListener listener){
        viewBooksButton.addActionListener(listener);
    }

    public void setCloseCompetitionButton(ActionListener listener){
        closeCompetitionButton.addActionListener(listener);
    }

    public JButton getCreateCompetitionButton() {
        return createCompetitionButton;
    }

    public JButton getAddBookButton() {
        return addBookButton;
    }

    public JButton getViewBooksButton() {
        return viewBooksButton;
    }

    public JButton getCloseCompetitionButton() {
        return closeCompetitionButton;
    }

    public PersonnelDashboardView() {
        setContentPane(dashboardPanel);
        setTitle("Personnel Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
