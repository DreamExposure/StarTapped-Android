package org.dreamexposure.startapped.objects.user;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author NovaFox161
 * Date Created: 12/16/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class UserSettings {
    private boolean privacyAgree;

    private String accessToken;
    private String refreshToken;
    private long tokenExpire;

    public UserSettings(JSONObject rawSettings) {
        try {
            privacyAgree = rawSettings.getBoolean("privacy_agree");
            accessToken = rawSettings.getString("access_token");
            refreshToken = rawSettings.getString("refresh_token");
            tokenExpire = rawSettings.getLong("token_expire");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public UserSettings() {
        privacyAgree = false;
        accessToken = "N/a";
        refreshToken = "N/a";
        tokenExpire = 0;
    }

    //Getters
    public boolean hasAgreedToPrivacy() {
        return privacyAgree;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getTokenExpire() {
        return tokenExpire;
    }

    //Setters
    public void setPrivacyAgree(boolean _agree) {
        privacyAgree = _agree;
    }

    public void setAccessToken(String _token) {
        accessToken = _token;
    }

    public void setRefreshToken(String _token) {
        refreshToken = _token;
    }

    public void setTokenExpire(long _expire) {
        tokenExpire = _expire;
    }

    public JSONObject asJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("privacy_agree", privacyAgree);
        json.put("access_token", accessToken);
        json.put("refresh_token", refreshToken);
        json.put("token_expire", tokenExpire);

        return json;
    }
}
