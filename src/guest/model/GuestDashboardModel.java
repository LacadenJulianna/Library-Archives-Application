package src.guest.model;

import src.databaseConnection.DataAccess;
import src.guest.view.GuestBookCatalogView;
import src.guest.view.GuestDashboardView;
import src.guest.view.VoteSelectionView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static src.databaseConnection.DataAccess.closeCon;
import static src.databaseConnection.DataAccess.setConn;

public class GuestDashboardModel {
    static GuestDashboardView view;
    private static String userEmail;

    public GuestDashboardModel(GuestDashboardView view, String userEmail) {
        this.userEmail = userEmail;
        this.view = view;

        view.setViewBookButton(new ViewBookListener());
        view.setCastVoteButton(new CastVoteListener());
    }

    private class ViewBookListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.dispose();
            GuestBookCatalogView catalogView = new GuestBookCatalogView();
            new GuestBookCatalogModel(catalogView, userEmail);
        }
    }

    private class CastVoteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean continueToVoting;

            try {
                setConn();

                String query = "SELECT * FROM competition WHERE Ending_Date IS NULL";
                Statement stmt = DataAccess.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query);
                continueToVoting = (rs.next()) ? true : false;

                closeCon();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            if (continueToVoting) {
                view.dispose();
                VoteSelectionView voteSelectionView = new VoteSelectionView();
                new VoteSelectionModel(voteSelectionView, userEmail);
            } else {
                JOptionPane.showMessageDialog(view,
                        "No Ongoing Competition.\nCheck back later!",
                        "Voting Closed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
