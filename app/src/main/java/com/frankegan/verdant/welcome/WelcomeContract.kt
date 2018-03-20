package com.frankegan.verdant.welcome

/**
 * Created by frankegan on 5/15/16.
 */
interface WelcomeContract {
    interface View {

        fun setWelcomeName(accountName: String)

    }

    interface UserActionsListener {

        fun saveUser()
    }
}
