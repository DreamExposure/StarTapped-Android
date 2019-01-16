package org.dreamexposure.startapped.async.load;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.dreamexposure.startapped.StarTappedApp;

import java.io.File;

public class LoadAudioFromFileTask extends AsyncTask<String, Void, Boolean>

{
    @SuppressLint("StaticFieldLeak")
    private MediaPlayer audioPlayer;

    private String audioPath;

    public LoadAudioFromFileTask(MediaPlayer player) {
        audioPlayer = player;
    }

    protected Boolean doInBackground(String... filePaths) {
        if (filePaths.length >= 1) {
            audioPath = filePaths[0];
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(StarTappedApp.getContext(), Uri.fromFile(new File(audioPath)));

                String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
                if ("yes".equals(hasVideo)) {
                    try {
                        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        Uri uri = Uri.parse(audioPath);
                        audioPlayer.setDataSource(StarTappedApp.getInstance().getApplicationContext(), uri);
                        audioPlayer.prepare();
                        return true;
                    } catch (Exception e) {
                        Log.e(StarTappedApp.TAG, "Failed to load audio", e);
                    }
                }
            } catch (Exception e) {
                Log.e(StarTappedApp.TAG, "Failed to check audio validity", e);
            }
        }
        return false;
    }

    protected void onPostExecute(Boolean result) {
    }
}
