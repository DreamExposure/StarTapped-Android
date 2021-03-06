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
 * Date Created: 12/17/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class TokenReauthTask extends AsyncTask<Object, Void, NetworkCallStatus> {
    private TaskCallback callback;

    public TokenReauthTask(TaskCallback _callback) {
        callback = _callback;
    }

    @Override
    protected NetworkCallStatus doInBackground(Object... objects) {
        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject requestJson = new JSONObject();

            RequestBody requestBody = RequestBody.create(GlobalConst.JSON, requestJson.toString());

            Request request = new Request.Builder()
                    .url(GlobalConst.apiUrl + "/auth/refresh")
                    .post(requestBody)
                    .header("Content-Type", "application/json")
                    .header("Authorization_Access", SettingsManager.getManager().getSettings().getAccessToken())
                    .header("Authorization_Refresh", SettingsManager.getManager().getSettings().getRefreshToken())
                    .build();

            Response response = client.newCall(request).execute();

            JSONObject responseBody = new JSONObject(response.body().string());

            if (response.code() == 201) {
                //Success, save credentials and return...
                JSONObject credentials = responseBody.getJSONObject("credentials");
                SettingsManager.getManager().getSettings().setAccessToken(credentials.getString("access_token"));
                SettingsManager.getManager().getSettings().setRefreshToken(credentials.getString("refresh_token"));
                SettingsManager.getManager().getSettings().setTokenExpire(credentials.getLong("expire"));
                SettingsManager.getManager().saveSettings();

                return new NetworkCallStatus(true, TaskType.AUTH_TOKEN_REAUTH).setCode(201).setMessage(responseBody.getString("message"));
            } else if (response.code() == 200) {
                //Success, credentials do not need to be refreshed..
                return new NetworkCallStatus(true, TaskType.AUTH_TOKEN_REAUTH).setCode(200).setMessage(responseBody.getString("message"));
            } else {
                Log.d(StarTappedApp.TAG, response.code() + " " + responseBody.toString());

                return new NetworkCallStatus(false, TaskType.AUTH_TOKEN_REAUTH).setCode(response.code()).setMessage(responseBody.getString("message"));
            }
        } catch (JSONException | IOException | IllegalStateException e) {
            Log.e(StarTappedApp.TAG, "Error with Token refresh", e);
            return new NetworkCallStatus(false, TaskType.AUTH_TOKEN_REAUTH).setCode(1).setMessage("Error");
        }
    }

    @Override
    protected void onPostExecute(NetworkCallStatus response) {
        callback.taskCallback(response);
    }
}
