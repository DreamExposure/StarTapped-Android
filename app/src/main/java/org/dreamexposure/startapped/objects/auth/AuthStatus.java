package org.dreamexposure.startapped.objects.auth;

import android.app.Activity;

/**
 * @author NovaFox161
 * Date Created: 12/16/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class AuthStatus {
    private final boolean success;
    private final Activity source;
    private int code;
    private String message;

    public AuthStatus(boolean _success, Activity _source) {
        success = _success;
        source = _source;
    }

    //Getters

    public boolean isSuccess() {
        return success;
    }

    public Activity getSource() {
        return source;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    //Setters

    public AuthStatus setCode(int code) {
        this.code = code;
        return this;
    }

    public AuthStatus setMessage(String message) {
        this.message = message;
        return this;
    }
}
