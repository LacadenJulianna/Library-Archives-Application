package src.personnel.view;

import javax.swing.*;
import java.awt.*;

public class CategoryManagerView extends JFrame {

    private JLabel titleLabel, competitionNameLabel;
    private  JCheckBox BOYCheckbox, HORCheckbox, CLACheckbox, DRACheckbox, CHICheckbox, MYSCheckbox,
            COMCheckbox, FICCheckbox, FANCheckbox, HIFCheckbox, ROMCheckbox, SCICheckbox, NONCheckbox, HISCheckbox;
    private JButton cancelButton, nextButton;

    public CategoryManagerView() {
        setTitle("Create Category");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // topPanel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Adding Categories for");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        competitionNameLabel = new JLabel(String.valueOf(competitionNameLabel)); // get the competition name to the model. This is the placeholder for the competition name
        competitionNameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        competitionNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(5)); // spacing
        topPanel.add(competitionNameLabel);
        add(topPanel, BorderLayout.NORTH);

        // center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        BOYCheckbox = new JCheckBox("Book of the Year");
        HORCheckbox = new JCheckBox("Best in Horror");
        CLACheckbox = new JCheckBox("Best in Classics");
        DRACheckbox = new JCheckBox("Best in Drama");
        CHICheckbox = new JCheckBox("Best in Children's Fiction");
        MYSCheckbox = new JCheckBox("Best in Mystery");
        COMCheckbox = new JCheckBox("Best in Comedy");
        FICCheckbox = new JCheckBox("Best in Fiction");
        FANCheckbox = new JCheckBox("Best in Fantasy");
        HISCheckbox = new JCheckBox("Best in History");
        HIFCheckbox = new JCheckBox("Best in Historical Fiction");
        ROMCheckbox = new JCheckBox("Best in Romance");
        SCICheckbox = new JCheckBox("Best in Science Fiction");
        NONCheckbox = new JCheckBox("Best in Non-Fiction");

        centerPanel.add(BOYCheckbox);
        centerPanel.add(HORCheckbox);
        centerPanel.add(CLACheckbox);
        centerPanel.add(DRACheckbox);
        centerPanel.add(CHICheckbox);
        centerPanel.add(MYSCheckbox);
        centerPanel.add(COMCheckbox);
        centerPanel.add(FICCheckbox);
        centerPanel.add(FANCheckbox);
        centerPanel.add(HISCheckbox);
        centerPanel.add(HIFCheckbox);
        centerPanel.add(ROMCheckbox);
        centerPanel.add(SCICheckbox);
        centerPanel.add(NONCheckbox);
        add(centerPanel, BorderLayout.CENTER);

        // bottomPanel
        JPanel bottomPanel = new JPanel();

        cancelButton = new JButton("cancel");
        nextButton = new JButton("next");

        bottomPanel.add(cancelButton);
        bottomPanel.add(nextButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Setter to update the competition name label
    public void setCompetitionName(String name) {
        competitionNameLabel.setText(name);
    }

    // Getters for buttons
    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getNextButton() {
        return nextButton;
    }

public JCheckBox getBOYCheckbox() { return BOYCheckbox; }
    public JCheckBox getHORCheckbox() { return HORCheckbox; }
    public JCheckBox getCLACheckbox() { return CLACheckbox; }
    public JCheckBox getDRACheckbox() { return DRACheckbox; }
    public JCheckBox getCHICheckbox() { return CHICheckbox; }
    public JCheckBox getMYSCheckbox() { return MYSCheckbox; }
    public JCheckBox getCOMCheckbox() { return COMCheckbox; }
    public JCheckBox getFICCheckbox() { return FICCheckbox; }
    public JCheckBox getFANCheckbox() { return FANCheckbox; }
    public JCheckBox getHISCheckbox() { return HISCheckbox; }
    public JCheckBox getHIFCheckbox() { return HIFCheckbox; }
    public JCheckBox getROMCheckbox() { return ROMCheckbox; }
    public JCheckBox getSCICheckbox() { return SCICheckbox; }
    public JCheckBox getNONCheckbox() { return NONCheckbox; }

    public java.util.List<String> getSelectedCategories() {
        java.util.List<String> selected = new java.util.ArrayList<>();
        if (BOYCheckbox.isSelected()) selected.add("BOY");
        if (HORCheckbox.isSelected()) selected.add("HOR");
        if (CLACheckbox.isSelected()) selected.add("CLA");
        if (DRACheckbox.isSelected()) selected.add("DRA");
        if (CHICheckbox.isSelected()) selected.add("CHI");
        if (MYSCheckbox.isSelected()) selected.add("MYS");
        if (COMCheckbox.isSelected()) selected.add("COM");
        if (FICCheckbox.isSelected()) selected.add("FIC");
        if (FANCheckbox.isSelected()) selected.add("FAN");
        if (HISCheckbox.isSelected()) selected.add("HIS");
        if (HIFCheckbox.isSelected()) selected.add("HIF");
        if (ROMCheckbox.isSelected()) selected.add("ROM");
        if (SCICheckbox.isSelected()) selected.add("SCI");
        if (NONCheckbox.isSelected()) selected.add("NON");
        return selected;
    }

}
