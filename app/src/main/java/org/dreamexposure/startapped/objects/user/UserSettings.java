package org.dreamexposure.startapped.objects.user;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

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

    //General account settings
    private UUID accountId;
    private String username;
    private boolean safeSearch;
    private boolean emailConfirmed;
    private boolean verified;
    private String birthday;
    private String phoneNumber;


    public UserSettings(JSONObject rawSettings) {
        try {
            privacyAgree = rawSettings.getBoolean("privacy_agree");
            accessToken = rawSettings.getString("access_token");
            refreshToken = rawSettings.getString("refresh_token");
            tokenExpire = rawSettings.getLong("token_expire");

            accountId = UUID.fromString(rawSettings.getString("account_id"));
            username = rawSettings.getString("username");
            safeSearch = rawSettings.getBoolean("safe_search");
            emailConfirmed = rawSettings.getBoolean("email_confirmed");
            verified = rawSettings.getBoolean("verified");
            birthday = rawSettings.getString("birthday");
            phoneNumber = rawSettings.getString("phone_number");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public UserSettings() {
        privacyAgree = false;
        accessToken = "N/a";
        refreshToken = "N/a";
        tokenExpire = 0;

        accountId = UUID.randomUUID(); //Doesn't really matter, it will get overwritten with a real ID once logged in.
        username = "N/a";
        safeSearch = false;
        emailConfirmed = false;
        verified = false;
        birthday = "01-01-1970";
        phoneNumber = "000-000-0000";
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

    public UUID getAccountId() {
        return accountId;
    }

    public String getUsername() {
        return username;
    }

    public boolean isSafeSearch() {
        return safeSearch;
    }

    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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

    public void setAccountId(UUID _id) {
        accountId = _id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSafeSearch(boolean safeSearch) {
        this.safeSearch = safeSearch;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public JSONObject asJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("privacy_agree", privacyAgree);
        json.put("access_token", accessToken);
        json.put("refresh_token", refreshToken);
        json.put("token_expire", tokenExpire);

        json.put("account_id", accountId.toString());
        json.put("username", username);
        json.put("safe_search", safeSearch);
        json.put("email_confirmed", emailConfirmed);
        json.put("verified", verified);
        json.put("birthday", birthday);
        json.put("phone_number", phoneNumber);

        return json;
    }
}
