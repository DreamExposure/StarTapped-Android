package org.dreamexposure.startapped.network.download;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;

public class DownloadAudioTask extends AsyncTask<Object, Void, Boolean> {
    @SuppressLint("StaticFieldLeak")
    private MediaPlayer audioPlayer;

    private String url;

    public DownloadAudioTask(MediaPlayer _player, String url) {
        audioPlayer = _player;
        this.url = url;
    }

    protected Boolean doInBackground(Object... objects) {
        try {
            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            audioPlayer.setDataSource(url);
            audioPlayer.prepare();
            return true;
        } catch (Exception ignore) {
        }
        return false;
    }

    protected void onPostExecute(Boolean result) {
    }
}
