package de.backxtar.clevercharge.managers;

import androidx.fragment.app.Fragment;

import de.backxtar.clevercharge.fragmentsLogin.LoginSignInFragment;
import de.backxtar.clevercharge.fragmentsLogin.LoginSignUpFragment;

/**
 * ChargingStation container.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class LoginManager {

    /* Global variables */

    /**
     * Static SignIn fragment.
     */
    private static Fragment signIn = new LoginSignInFragment();

    /**
     * Static SignUp fragment.
     */
    private static Fragment signUp = new LoginSignUpFragment();
    //==========================================================

    /**
     * Getter for sign in.
     * @return sign in object
     */
    public static Fragment getSignIn() {
        return signIn;
    }

    /**
     * Getter for sign up.
     * @return sign up object
     */
    public static Fragment getSignUp() {
        return signUp;
    }
}
