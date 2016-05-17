package com.frankegan.verdant.loginsuccess;

/**
 * Created by frankegan on 5/15/16.
 */
public interface LoginSuccessContract {
    interface View {

        void showWelcome();

        void close();
    }

    interface UserActionsListener {

        void explore();

        void saveUser(String response);
    }
}
