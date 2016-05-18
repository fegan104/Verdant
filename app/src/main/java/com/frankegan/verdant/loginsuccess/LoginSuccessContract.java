package com.frankegan.verdant.loginsuccess;

/**
 * Created by frankegan on 5/15/16.
 */
public interface LoginSuccessContract {
    interface View {

        void setWelcomeName(String accountName);

        void close();
    }

    interface UserActionsListener {

        void explore();

        void saveUser();
    }
}
