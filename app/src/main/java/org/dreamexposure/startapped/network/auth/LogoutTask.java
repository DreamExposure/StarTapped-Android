package org.dreamexposure.startapped.network.auth;

import android.os.AsyncTask;
import android.util.Log;

import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.conf.GlobalConst;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.dreamexposure.startapped.utils.SettingsManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author NovaFox161
 * Date Created: 12/16/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class LogoutTask extends AsyncTask<Object, Void, NetworkCallStatus> {
    private TaskCallback callback;

    public LogoutTask(TaskCallback _callback) {
        callback = _callback;
    }

    @Override
    protected NetworkCallStatus doInBackground(Object... objects) {
        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject requestJson = new JSONObject();

            RequestBody requestBody = RequestBody.create(GlobalConst.JSON, requestJson.toString());

            Request request = new Request.Builder()
                    .url(GlobalConst.apiUrl + "/account/logout")
                    .post(requestBody)
                    .header("Content-Type", "application/json")
                    .header("Authorization_Access", SettingsManager.getManager().getSettings().getAccessToken())
                    .header("Authorization_Refresh", SettingsManager.getManager().getSettings().getRefreshToken())
                    .build();

            Response response = client.newCall(request).execute();

            JSONObject responseBody = new JSONObject(response.body().string());

            if (response.code() == 200) {
                //Success, delete credentials
                SettingsManager.getManager().getSettings().setRefreshToken("N/a");
                SettingsManager.getManager().getSettings().setAccessToken("N/a");
                SettingsManager.getManager().getSettings().setTokenExpire(0);

                SettingsManager.getManager().getSettings().setUsername("N/a");
                SettingsManager.getManager().getSettings().setSafeSearch(false);
                SettingsManager.getManager().getSettings().setEmailConfirmed(false);
                SettingsManager.getManager().getSettings().setVerified(false);
                SettingsManager.getManager().getSettings().setBirthday("01-01-1970");
                SettingsManager.getManager().getSettings().setPhoneNumber("000.000.0000");
                SettingsManager.getManager().saveSettings();

                return new NetworkCallStatus(true, TaskType.AUTH_LOGOUT).setCode(200).setMessage(responseBody.getString("message"));
            } else {
                Log.d(StarTappedApp.TAG, response.code() + " " + responseBody.toString());

                return new NetworkCallStatus(false, TaskType.AUTH_LOGOUT).setCode(response.code()).setMessage(responseBody.getString("message"));
            }
        } catch (JSONException | IOException | IllegalStateException e) {
            e.printStackTrace();
            return new NetworkCallStatus(false, TaskType.AUTH_LOGOUT).setCode(1).setMessage("Error");
        }
    }

    @Override
    protected void onPostExecute(NetworkCallStatus response) {
        callback.taskCallback(response);
    }
}
