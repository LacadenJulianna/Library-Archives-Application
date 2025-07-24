package src.login.model;

import src.login.view.LoginView;
import src.login.view.PersonnelLoginView;
import src.login.view.UserIdentification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserIdentificationModel {
    static UserIdentification view;

    public UserIdentificationModel(UserIdentification view){
        this.view = view;

        this.view.setGuestButton(new GuestButtonListener());
        this.view.setPersonnelButton(new PersonnelButtonListener());

    }

    private class GuestButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            view.dispose(); //dispose the first window
            //Directs to the Guest User login window
            LoginView loginView = new LoginView();
            new LoginModel(loginView);
            loginView.setVisible(true);

        }
    }

    private class PersonnelButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            view.dispose(); //dispose the first window
            //Directs to the Personnel login window
            PersonnelLoginView personnelLoginView = new PersonnelLoginView();
            new PersonnelLoginModel(personnelLoginView);
            personnelLoginView.setVisible(true);
        }
    }
}
