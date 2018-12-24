package org.dreamexposure.startapped.auth;

import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.network.auth.LoginTask;
import org.dreamexposure.startapped.network.auth.LogoutTask;
import org.dreamexposure.startapped.network.auth.RegisterTask;

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

    public void register(String user, String email, String pass, String birthday, String gCap, TaskCallback callback) {
        new RegisterTask(callback).execute(user, email, pass, birthday, gCap);
    }

    public void login(String email, String password, String gCap, TaskCallback callback) {
        new LoginTask(callback).execute(email, password, gCap);
    }

    public void logout(TaskCallback callback) {
        new LogoutTask(callback).execute();
    }
}
