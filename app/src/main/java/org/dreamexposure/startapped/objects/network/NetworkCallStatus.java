package org.dreamexposure.startapped.objects.network;

import org.dreamexposure.startapped.enums.TaskType;
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
    private final TaskType type;
    private int code;
    private String message;

    private JSONObject body;

    public NetworkCallStatus(boolean _success, TaskType _type) {
        success = _success;
        type = _type;
    }

    //Getters

    public boolean isSuccess() {
        return success;
    }

    public TaskType getType() {
        return type;
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
