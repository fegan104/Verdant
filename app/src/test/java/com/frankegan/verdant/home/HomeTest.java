package com.frankegan.verdant.home;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class HomeTest {
    HomeContract.View view = new HomeActivity();
    HomeContract.UserActionsListener presenter = new HomePresenter("pics", view);

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
}