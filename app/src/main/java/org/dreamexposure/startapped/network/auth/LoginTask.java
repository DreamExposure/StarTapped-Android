package org.dreamexposure.startapped.network.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.activities.HubActivity;
import org.dreamexposure.startapped.conf.GlobalConst;
import org.dreamexposure.startapped.network.account.GetAccountTask;
import org.dreamexposure.startapped.objects.auth.AuthStatus;
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
public class LoginTask extends AsyncTask<Object, Void, String> {
    private AuthStatus status;


    @Override
    protected String doInBackground(Object... objects) {
        String email = (String) objects[0];
        String pass = (String) objects[1];
        String gcap = (String) objects[2];
        Activity source = (Activity) objects[3];

        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put("email", email);
            requestJson.put("password", pass);
            requestJson.put("gcap", gcap);

            RequestBody requestBody = RequestBody.create(GlobalConst.JSON, requestJson.toString());

            Request request = new Request.Builder()
                    .url(GlobalConst.apiUrl + "/account/login")
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

                status = new AuthStatus(true, source).setCode(200).setMessage(responseBody.getString("message"));
                return status.getMessage();
            } else {
                Log.d(StarTappedApp.TAG, response.code() + " " + responseBody.toString());

                status = new AuthStatus(false, source).setCode(response.code()).setMessage(responseBody.getString("message"));
                return status.getMessage();
            }
        } catch (JSONException | IOException | IllegalStateException e) {
            e.printStackTrace();
            status = new AuthStatus(false, source).setCode(1).setMessage("Error");
            return status.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (status.isSuccess()) {
            Intent intent = new Intent(status.getSource(), HubActivity.class);
            status.getSource().startActivity(intent);
            status.getSource().finish();

            //Get the account settings
            new GetAccountTask().execute(status.getSource());
        } else {
            Toast.makeText(status.getSource().getApplicationContext(), status.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
