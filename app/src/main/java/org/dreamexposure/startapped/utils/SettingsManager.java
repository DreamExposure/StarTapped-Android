package org.dreamexposure.startapped.utils;

import android.content.Context;

import org.apache.commons.io.IOUtils;
import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.objects.user.UserSettings;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/16/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class SettingsManager {
    private static SettingsManager instance;

    private UserSettings settings;

    private SettingsManager() {
    }

    public static SettingsManager getManager() {
        if (instance == null)
            instance = new SettingsManager();
        return instance;
    }

    public void init() {
        readSettings();
    }

    //Getters
    public UserSettings getSettings() {
        if (settings == null)
            readSettings();
        return settings;
    }

    //Functions
    private void readSettings() {
        String FILENAME = "user.prefs";

        File file = StarTappedApp.getContext().getFileStreamPath(FILENAME);
        if (file.exists()) {
            try {
                InputStream is = StarTappedApp.getContext().openFileInput(FILENAME);

                JSONObject rawSettings = new JSONObject(IOUtils.toString(is, "UTF-8"));

                settings = new UserSettings(rawSettings);

                is.close();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            settings = new UserSettings();
        }
    }

    public void updateSettings(JSONObject json) {
        try {
            getSettings().setAccountId(UUID.fromString(json.getString("id")));
            getSettings().setUsername(json.getString("username"));
            getSettings().setSafeSearch(json.getBoolean("safe_search"));
            getSettings().setEmailConfirmed(json.getBoolean("email_confirmed"));
            getSettings().setVerified(json.getBoolean("verified"));
            getSettings().setPhoneNumber(json.getString("phone_number"));
            getSettings().setBirthday(json.getString("birthday"));

            saveSettings();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveSettings() {
        String FILENAME = "user.prefs";
        try {
            FileOutputStream fos = StarTappedApp.getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(settings.asJson().toString().getBytes());
            fos.close();

            readSettings();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
