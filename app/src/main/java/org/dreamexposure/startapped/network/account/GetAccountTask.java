package org.dreamexposure.startapped.network.account;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.dreamexposure.startapped.StarTappedApp;
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
public class GetAccountTask extends AsyncTask<Object, Void, NetworkCallStatus> {

    @Override
    protected NetworkCallStatus doInBackground(Object... objects) {
        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject requestJson = new JSONObject();

            RequestBody requestBody = RequestBody.create(GlobalConst.JSON, requestJson.toString());

            Request request = new Request.Builder()
                    .url(GlobalConst.apiUrl + "/account/get")
                    .post(requestBody)
                    .header("Content-Type", "application/json")
                    .header("Authorization_Access", SettingsManager.getManager().getSettings().getAccessToken())
                    .header("Authorization_Refresh", SettingsManager.getManager().getSettings().getRefreshToken())
                    .build();

            Response response = client.newCall(request).execute();

            JSONObject responseBody = new JSONObject(response.body().string());

            if (response.code() == 200) {
                //Success
                return new NetworkCallStatus(true, TaskType.ACCOUNT_GET_SELF)
                        .setCode(response.code())
                        .setMessage(responseBody.getString("message"))
                        .setBody(responseBody);
            } else {
                Log.d(StarTappedApp.TAG, response.code() + " " + responseBody.toString());

                return new NetworkCallStatus(false, TaskType.ACCOUNT_GET_SELF)
                        .setCode(response.code())
                        .setMessage(responseBody.getString("message"))
                        .setBody(responseBody);
            }
        } catch (JSONException | IOException | IllegalStateException e) {
            e.printStackTrace();
            return new NetworkCallStatus(false, TaskType.ACCOUNT_GET_SELF)
                    .setCode(1)
                    .setMessage("Error");
        }
    }

    @Override
    protected void onPostExecute(NetworkCallStatus response) {
        if (response.isSuccess()) {
            //Save settings
            try {
                SettingsManager.getManager().updateSettings(response.getBody().getJSONObject("account"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Don't show toast, its a background process.
        } else {
            Toast.makeText(StarTappedApp.getContext(), response.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}