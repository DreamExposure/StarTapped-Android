package org.dreamexposure.startapped.objects.network;

import android.app.Activity;

import org.json.JSONObject;

/**
 * @author NovaFox161
 * Date Created: 12/17/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class NetworkCallStatus {
    private final boolean success;
    private final Activity source;
    private int code;
    private String message;

    private JSONObject body;

    public NetworkCallStatus(boolean _success, Activity _source) {
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

    public JSONObject getBody() {
        return body;
    }


    //Setters

    public NetworkCallStatus setCode(int code) {
        this.code = code;
        return this;
    }

    public NetworkCallStatus setMessage(String message) {
        this.message = message;
        return this;
    }

    public NetworkCallStatus setBody(JSONObject _body) {
        body = _body;
        return this;
    }
}
