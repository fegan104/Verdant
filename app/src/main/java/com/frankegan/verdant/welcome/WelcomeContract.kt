package com.frankegan.verdant.welcome

/**
 * Created by frankegan on 5/15/16.
 */
interface WelcomeContract {
    interface View {

        fun setWelcomeName(accountName: String)

        fun close()
    }

    interface UserActionsListener {

        fun explore()

        fun saveUser()
    }
}
