package src.guest.view;

import src.guest.model.GuestDashboardModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GuestDashboardView extends JFrame implements Runnable {
    private JButton viewBookButton, castVoteButton;
    private JLabel titleLabel;

    public GuestDashboardView() {
        setTitle("Guest Dashboard");
        setSize(380, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Padding

        titleLabel = new JLabel("Bach Archive Awards");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // buttons
        viewBookButton = new JButton("View Books");
        viewBookButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        castVoteButton = new JButton("Cast a Vote");
        castVoteButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add spacing and components
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(20)); // spacing
        centerPanel.add(viewBookButton);
        centerPanel.add(Box.createVerticalStrut(10)); // spacing
        centerPanel.add(castVoteButton);

        add(centerPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public void setViewBookButton(ActionListener listener) {
        viewBookButton.addActionListener(listener);
    }

    public void setCastVoteButton(ActionListener listener) {
        castVoteButton.addActionListener(listener);
    }

    public JButton getViewBookButton() {
        return viewBookButton;
    }

    public JButton getCastVoteButton() {
        return castVoteButton;
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }

    public void run() {

    }

}
