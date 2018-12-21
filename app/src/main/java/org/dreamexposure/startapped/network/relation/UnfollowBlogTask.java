package org.dreamexposure.startapped.network.relation;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.conf.GlobalConst;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.dreamexposure.startapped.utils.SettingsManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author NovaFox161
 * Date Created: 12/21/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings("ConstantConditions")
public class UnfollowBlogTask extends AsyncTask<Object, Void, String> {
    private NetworkCallStatus status;
    private final UUID blogId;

    public UnfollowBlogTask(UUID _blogId) {
        blogId = _blogId;
    }

    @Override
    protected String doInBackground(Object... objects) {
        Activity source = (Activity) objects[0];

        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put("blog_id", blogId.toString());


            RequestBody requestBody = RequestBody.create(GlobalConst.JSON, requestJson.toString());

            Request request = new Request.Builder()
                    .url(GlobalConst.apiUrl + "/relation/unfollow")
                    .post(requestBody)
                    .header("Content-Type", "application/json")
                    .header("Authorization_Access", SettingsManager.getManager().getSettings().getAccessToken())
                    .header("Authorization_Refresh", SettingsManager.getManager().getSettings().getRefreshToken())
                    .build();

            Response response = client.newCall(request).execute();

            JSONObject responseBody = new JSONObject(response.body().string());

            if (response.code() == 200) {
                //Success
                status = new NetworkCallStatus(true, source)
                        .setCode(response.code())
                        .setMessage(responseBody.getString("message"))
                        .setBody(responseBody);
                return status.getMessage();
            } else {
                Log.d(StarTappedApp.TAG, response.code() + " " + responseBody.toString());

                status = new NetworkCallStatus(false, source)
                        .setCode(response.code())
                        .setMessage(responseBody.getString("message"))
                        .setBody(responseBody);
                return status.getMessage();
            }
        } catch (JSONException | IOException | IllegalStateException e) {
            e.printStackTrace();
            status = new NetworkCallStatus(false, source)
                    .setCode(1)
                    .setMessage("Error");
            return status.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (status.isSuccess()) {
            Toast.makeText(status.getSource().getApplicationContext(), status.getMessage(), Toast.LENGTH_SHORT).show();
            status.getSource().finish();
        } else {
            Toast.makeText(status.getSource().getApplicationContext(), status.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
