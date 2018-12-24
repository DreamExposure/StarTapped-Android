package org.dreamexposure.startapped.network.blog.self;

import android.os.AsyncTask;
import android.util.Log;

import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.conf.GlobalConst;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.objects.blog.IBlog;
import org.dreamexposure.startapped.objects.blog.PersonalBlog;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.dreamexposure.startapped.utils.ImageUtils;
import org.dreamexposure.startapped.utils.SettingsManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author NovaFox161
 * Date Created: 12/18/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings("ConstantConditions")
public class UpdateBlogTask extends AsyncTask<IBlog, Void, NetworkCallStatus> {
    private TaskCallback callback;
    private String backgroundPath;
    private String iconPath;

    public UpdateBlogTask(TaskCallback _callback, String _iconPath, String _backgroundPath) {
        callback = _callback;
        iconPath = _iconPath;
        backgroundPath = _backgroundPath;
    }

    @Override
    protected NetworkCallStatus doInBackground(IBlog... params) {
        IBlog blog = params[0];

        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put("id", blog.getBlogId().toString());
            requestJson.put("name", blog.getName());
            requestJson.put("description", blog.getDescription());
            requestJson.put("nsfw", blog.isNsfw());
            requestJson.put("allow_under_18", blog.isAllowUnder18());
            requestJson.put("background_color", blog.getBackgroundColor());
            if (blog instanceof PersonalBlog) {
                requestJson.put("display_age", ((PersonalBlog) blog).isDisplayAge());
            }

            //background and icon image
            if (backgroundPath.length() > 0 && !backgroundPath.isEmpty()) {
                requestJson.put("background_image", ImageUtils.fileToBase64(new File(backgroundPath)));
            }
            if (iconPath.length() > 0 && !iconPath.isEmpty()) {
                requestJson.put("icon_image", ImageUtils.fileToBase64(new File(iconPath)));
            }


            RequestBody requestBody = RequestBody.create(GlobalConst.JSON, requestJson.toString());

            Request request = new Request.Builder()
                    .url(GlobalConst.apiUrl + "/blog/update")
                    .post(requestBody)
                    .header("Content-Type", "application/json")
                    .header("Authorization_Access", SettingsManager.getManager().getSettings().getAccessToken())
                    .header("Authorization_Refresh", SettingsManager.getManager().getSettings().getRefreshToken())
                    .build();

            Response response = client.newCall(request).execute();

            JSONObject responseBody = new JSONObject(response.body().string());

            if (response.code() == 200) {
                //Success
                return new NetworkCallStatus(true, TaskType.BLOG_UPDATE_SELF)
                        .setCode(response.code())
                        .setMessage(responseBody.getString("message"))
                        .setBody(responseBody);
            } else {
                Log.d(StarTappedApp.TAG, response.code() + " " + responseBody.toString());

                return new NetworkCallStatus(false, TaskType.BLOG_UPDATE_SELF)
                        .setCode(response.code())
                        .setMessage(responseBody.getString("message"))
                        .setBody(responseBody);
            }
        } catch (JSONException | IOException | IllegalStateException e) {
            e.printStackTrace();
            return new NetworkCallStatus(false, TaskType.BLOG_UPDATE_SELF)
                    .setCode(1)
                    .setMessage("Error");
        }
    }

    @Override
    protected void onPostExecute(NetworkCallStatus response) {
        callback.taskCallback(response);
    }
}
