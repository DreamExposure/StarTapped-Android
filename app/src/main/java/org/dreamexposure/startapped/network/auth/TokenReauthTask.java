package org.dreamexposure.startapped.network.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.activities.MainActivity;
import org.dreamexposure.startapped.activities.auth.LoginActivity;
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
 * Date Created: 12/17/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class TokenReauthTask extends AsyncTask<Object, Void, String> {
    private AuthStatus status;


    @Override
    protected String doInBackground(Object... objects) {
        Activity source = (Activity) objects[0];

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

            if (response.code() == 200) {
                //Success, save credentials and return...
                JSONObject credentials = responseBody.getJSONObject("credentials");
                SettingsManager.getManager().getSettings().setAccessToken(credentials.getString("access_token"));
                SettingsManager.getManager().getSettings().setRefreshToken(credentials.getString("refresh_token"));
                SettingsManager.getManager().getSettings().setTokenExpire(credentials.getLong("expire"));
                SettingsManager.getManager().saveSettings();

                status = new AuthStatus(true, source).setCode(200).setMessage(responseBody.getString("message"));
                return status.getMessage();
            } else if (response.code() == 201) {
                //Success, credentials do not need to be refreshed..
                status = new AuthStatus(true, source).setCode(201).setMessage(responseBody.getString("message"));
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
            Intent intent = new Intent(status.getSource(), MainActivity.class);
            status.getSource().startActivity(intent);
            status.getSource().finish();

            //Get the account settings
            new GetAccountTask().execute(status.getSource());
        } else {
            Toast.makeText(status.getSource().getApplicationContext(), status.getMessage(), Toast.LENGTH_LONG).show();
            //Go back to login page, and invalidate saved credentials...
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

            Intent intent = new Intent(status.getSource(), LoginActivity.class);
            status.getSource().startActivity(intent);
            status.getSource().finish();
        }
    }
}
