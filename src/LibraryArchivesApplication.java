package src;

import src.login.model.UserIdentificationModel;
import src.login.view.UserIdentification;

import javax.swing.*;

import static src.databaseConnection.DataAccess.conn;
import static src.databaseConnection.DataAccess.setConn;

public class LibraryArchivesApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            try {
                setConn();
                if (conn != null) {
                    System.out.println("Connection successful!");
                } else {
                    System.out.println("Connection failed.");
                }
                UserIdentification view = new UserIdentification();
                new UserIdentificationModel(view);
                view.setVisible(true);

            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}
