package org.dreamexposure.startapped.auth;

import android.app.Activity;

import org.dreamexposure.startapped.network.auth.LoginTask;
import org.dreamexposure.startapped.network.auth.LogoutTask;

/**
 * @author NovaFox161
 * Date Created: 12/16/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings("ConstantConditions")
public class AuthenticationHandler {
    private static AuthenticationHandler instance;

    private AuthenticationHandler() {
    }

    public static AuthenticationHandler get() {
        if (instance == null)
            instance = new AuthenticationHandler();
        return instance;
    }

    public void login(String email, String password, String gCap, Activity source) {
        new LoginTask().execute(email, password, gCap, source);
    }

    public void logout(Activity source) {
        new LogoutTask().execute(source);
    }
}
