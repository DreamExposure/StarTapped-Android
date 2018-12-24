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
public class RegisterTask extends AsyncTask<String, Void, NetworkCallStatus> {
    private TaskCallback callback;

    public RegisterTask(TaskCallback _callback) {
        callback = _callback;
    }


    @Override
    protected NetworkCallStatus doInBackground(String... params) {
        String user = params[0];
        String email = params[1];
        String pass = params[2];
        String birthday = params[3];
        String gcap = params[4];

        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put("username", user);
            requestJson.put("email", email);
            requestJson.put("password", pass);
            requestJson.put("birthday", birthday);
            requestJson.put("gcap", gcap);

            RequestBody requestBody = RequestBody.create(GlobalConst.JSON, requestJson.toString());

            Request request = new Request.Builder()
                    .url(GlobalConst.apiUrl + "/account/register")
                    .post(requestBody)
                    .header("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            JSONObject responseBody = new JSONObject(response.body().string());

            if (response.code() == 200) {
                //Success, save credentials and return...
                JSONObject credentials = responseBody.getJSONObject("credentials");
                SettingsManager.getManager().getSettings().setAccessToken(credentials.getString("access_token"));
                SettingsManager.getManager().getSettings().setRefreshToken(credentials.getString("refresh_token"));
                SettingsManager.getManager().getSettings().setTokenExpire(credentials.getLong("expire"));
                SettingsManager.getManager().saveSettings();

                return new NetworkCallStatus(true, TaskType.AUTH_REGISTER).setCode(200).setMessage(responseBody.getString("message"));
            } else {
                Log.d(StarTappedApp.TAG, response.code() + " " + responseBody.toString());

                return new NetworkCallStatus(false, TaskType.AUTH_REGISTER).setCode(response.code()).setMessage(responseBody.getString("message"));
            }
        } catch (JSONException | IOException | IllegalStateException e) {
            e.printStackTrace();
            return new NetworkCallStatus(false, TaskType.AUTH_REGISTER).setCode(1).setMessage("Error");
        }
    }

    @Override
    protected void onPostExecute(NetworkCallStatus response) {
        callback.taskCallback(response);
    }
}