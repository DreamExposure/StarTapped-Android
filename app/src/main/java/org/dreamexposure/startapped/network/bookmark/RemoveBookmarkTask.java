package org.dreamexposure.startapped.network.bookmark;

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
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemoveBookmarkTask extends AsyncTask<Object, Void, NetworkCallStatus> {
    private TaskCallback callback;
    private UUID postId;

    public RemoveBookmarkTask(TaskCallback _callback, UUID _postId) {
        callback = _callback;
        postId = _postId;
    }


    @Override
    protected NetworkCallStatus doInBackground(Object... objects) {
        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put("post_id", postId.toString());

            RequestBody requestBody = RequestBody.create(GlobalConst.JSON, requestJson.toString());

            Request request = new Request.Builder()
                    .url(GlobalConst.apiUrl + "/post/bookmark/remove")
                    .post(requestBody)
                    .header("Content-Type", "application/json")
                    .header("Authorization_Access", SettingsManager.getManager().getSettings().getAccessToken())
                    .header("Authorization_Refresh", SettingsManager.getManager().getSettings().getRefreshToken())
                    .build();

            Response response = client.newCall(request).execute();

            JSONObject responseBody = new JSONObject(response.body().string());

            if (response.code() == 200) {
                //Success
                return new NetworkCallStatus(true, TaskType.BOOKMARK_REMOVE)
                        .setCode(response.code())
                        .setMessage(responseBody.getString("message"))
                        .setBody(responseBody);
            } else {
                Log.d(StarTappedApp.TAG, response.code() + " " + responseBody.toString());

                return new NetworkCallStatus(false, TaskType.BOOKMARK_REMOVE)
                        .setCode(response.code())
                        .setMessage(responseBody.getString("message"))
                        .setBody(responseBody);
            }
        } catch (JSONException | IOException | IllegalStateException e) {
            e.printStackTrace();
            return new NetworkCallStatus(false, TaskType.BOOKMARK_REMOVE)
                    .setCode(1)
                    .setMessage("Error");
        }
    }

    @Override
    protected void onPostExecute(NetworkCallStatus response) {
        callback.taskCallback(response);
    }
}