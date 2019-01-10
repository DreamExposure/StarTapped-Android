package org.dreamexposure.startapped.network.post;

import android.os.AsyncTask;
import android.util.Log;

import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.conf.GlobalConst;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.dreamexposure.startapped.objects.post.IPost;
import org.dreamexposure.startapped.utils.SettingsManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author NovaFox161
 * Date Created: 1/1/2019
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class PostCreateTask extends AsyncTask<Object, Void, NetworkCallStatus> {
    private TaskCallback callback;
    private final IPost post;

    private List<JSONObject> files = new ArrayList<>();

    public PostCreateTask(TaskCallback _callback, IPost _post) {
        callback = _callback;
        post = _post;
    }

    public PostCreateTask(TaskCallback _callback, IPost _post, JSONObject... _files) {
        callback = _callback;
        post = _post;
        Collections.addAll(files, _files);
    }


    @Override
    protected NetworkCallStatus doInBackground(Object... objects) {
        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject requestJson = new JSONObject();
            //Add the required defaults...
            requestJson.put("blog_id", post.getOriginBlog().getBlogId().toString());
            requestJson.put("type", post.getPostType().name());
            requestJson.put("title", post.getTitle());
            requestJson.put("body", post.getBody());
            if (post.getParent() != null)
                requestJson.put("parent", post.getParent().toString());
            requestJson.put("nsfw", post.isNsfw());

            //Handle post types (File validation handled on backend)
            switch (post.getPostType()) {
                case TEXT:
                    //Nothing special for this type.
                    break;
                case IMAGE:
                    requestJson.put("image", files.get(0));
                    break;
                case AUDIO:
                    requestJson.put("audio", files.get(0));
                    break;
                case VIDEO:
                    requestJson.put("video", files.get(0));
                    break;
            }

            //TODO: Post tag handling.

            RequestBody requestBody = RequestBody.create(GlobalConst.JSON, requestJson.toString());

            Request request = new Request.Builder()
                    .url(GlobalConst.apiUrl + "/post/create")
                    .post(requestBody)
                    .header("Content-Type", "application/json")
                    .header("Authorization_Access", SettingsManager.getManager().getSettings().getAccessToken())
                    .header("Authorization_Refresh", SettingsManager.getManager().getSettings().getRefreshToken())
                    .build();

            Response response = client.newCall(request).execute();

            JSONObject responseBody = new JSONObject(response.body().string());

            if (response.code() == 200) {
                //Success
                return new NetworkCallStatus(true, TaskType.POST_CREATE)
                        .setCode(response.code())
                        .setMessage(responseBody.getString("message"))
                        .setBody(responseBody);
            } else {
                Log.d(StarTappedApp.TAG, response.code() + " " + responseBody.toString());

                return new NetworkCallStatus(false, TaskType.POST_CREATE)
                        .setCode(response.code())
                        .setMessage(responseBody.getString("message"))
                        .setBody(responseBody);
            }
        } catch (JSONException | IOException | IllegalStateException e) {
            e.printStackTrace();
            return new NetworkCallStatus(false, TaskType.POST_CREATE)
                    .setCode(1)
                    .setMessage("Error");
        }
    }

    @Override
    protected void onPostExecute(NetworkCallStatus response) {
        callback.taskCallback(response);
    }
}
