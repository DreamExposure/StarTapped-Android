package org.dreamexposure.startapped.network.download;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.VideoView;

import org.dreamexposure.startapped.StarTappedApp;

public class DownloadVideoTask extends AsyncTask<Object, Void, Boolean> {
    @SuppressLint("StaticFieldLeak")
    private VideoView video;

    private String url;

    private Uri uri;

    public DownloadVideoTask(VideoView video, String url) {
        this.video = video;
        this.url = url;
    }

    protected Boolean doInBackground(Object... objects) {
        try {
            uri = Uri.parse(url);
            return true;
        } catch (Exception e) {
            Log.e(StarTappedApp.TAG, "Failed to download video", e);
        }
        return false;
    }

    protected void onPostExecute(Boolean result) {
        video.stopPlayback();
        if (result) {
            video.setVideoURI(uri);
        }
    }
}
