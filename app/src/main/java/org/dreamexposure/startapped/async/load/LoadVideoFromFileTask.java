package org.dreamexposure.startapped.async.load;

import android.annotation.SuppressLint;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.VideoView;

import org.dreamexposure.startapped.StarTappedApp;

import java.io.File;

/**
 * @author NovaFox161
 * Date Created: 1/2/2019
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class LoadVideoFromFileTask extends AsyncTask<String, Void, Boolean> {
    @SuppressLint("StaticFieldLeak")
    private VideoView video;

    private String videoPath;

    public LoadVideoFromFileTask(VideoView video) {
        this.video = video;
    }

    protected Boolean doInBackground(String... filePaths) {
        if (filePaths.length >= 1) {
            videoPath = filePaths[0];
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(StarTappedApp.getContext(), Uri.fromFile(new File(videoPath)));

                String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                return "yes".equals(hasVideo);
            } catch (Exception e) {
                Log.e(StarTappedApp.TAG, "Failed to check video validity", e);
            }
        }
        return false;
    }

    protected void onPostExecute(Boolean result) {
        video.stopPlayback();
        if (result) {
            video.setVideoPath(videoPath);
        }
    }
}
