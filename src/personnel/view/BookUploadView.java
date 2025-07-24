package src.personnel.view;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class BookUploadView extends JFrame {
    private JPanel topPanel, centerPanel, bottomPanel;
    private JLabel titleLabel, selectedFileLabel;
    private JButton browseButton, returnButton, uploadButton;
    private File selectedFile;

    public BookUploadView() {
        setTitle("Upload Book");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel
        topPanel = new JPanel();
        titleLabel = new JLabel("Upload Books to Database");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        selectedFileLabel = new JLabel("Select a .csv or .txt file to upload");
        selectedFileLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        selectedFileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        browseButton = new JButton("Browse");
        browseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        browseButton.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            // *** Accept both .csv and .txt ***
            FileNameExtensionFilter filter =
                    new FileNameExtensionFilter("CSV / TXT Files", "csv", "txt");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(BookUploadView.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String name = file.getName().toLowerCase();
                if (name.endsWith(".csv") || name.endsWith(".txt")) {
                    selectedFile = file;
                    selectedFileLabel.setText("Selected: " + file.getName());
                } else {
                    selectedFileLabel.setText("Only .csv or .txt files are accepted.");
                }
            }
        });

        centerPanel.add(Box.createVerticalStrut(50));
        centerPanel.add(selectedFileLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(browseButton);
        centerPanel.add(Box.createVerticalGlue());
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        bottomPanel = new JPanel();
        returnButton = new JButton("Return");
        uploadButton = new JButton("Upload");
        bottomPanel.add(returnButton);
        bottomPanel.add(uploadButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public JButton getBrowseButton() { return browseButton; }
    public JButton getReturnButton() { return returnButton; }
    public JButton getUploadButton() { return uploadButton; }
    public JLabel getSelectedFileLabel() { return selectedFileLabel; }
    public File getSelectedFile() { return selectedFile; }
    public void setSelectedFile(File f) { selectedFile = f; }

}
