package com.frankegan.verdant.welcome;

/**
 * Created by frankegan on 5/15/16.
 */
public interface WelcomeContract {
    interface View {

        void setWelcomeName(String accountName);

        void close();
    }

    interface UserActionsListener {

        void explore();

        void saveUser();
    }
}
