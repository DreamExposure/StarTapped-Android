package org.dreamexposure.startapped.objects.container;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.async.load.LoadAudioFromFileTask;
import org.dreamexposure.startapped.network.download.DownloadAudioTask;
import org.dreamexposure.startapped.objects.file.UploadedFile;

import java.io.File;

@SuppressWarnings("unused")
public class AudioContainer {
    private ConstraintLayout audioContainer;
    private Button playPauseAudioButton;
    private SeekBar audioProgressBar;
    private Button muteUnmuteAudioButton;
    private TextView audioNameDisplay;
    private MediaPlayer audioPlayer;

    private boolean muted = false;
    private boolean autoPlay = false;
    private boolean prepared = false;

    private Handler handler = new Handler();
    private AppCompatActivity activity;
    private FragmentActivity fragmentActivity;

    public AudioContainer(View view, AppCompatActivity _activity) {
        activity = _activity;
        fragmentActivity = null;

        audioContainer = view.findViewById(R.id.audio_container);
        playPauseAudioButton = view.findViewById(R.id.play_pause_audio);
        audioProgressBar = view.findViewById(R.id.audio_progress_bar);
        muteUnmuteAudioButton = view.findViewById(R.id.mute_unmute_audio);
        audioNameDisplay = view.findViewById(R.id.audio_file_name_display);

        audioPlayer = new MediaPlayer();
        audioPlayer.setOnPreparedListener(mediaPlayer -> onAudioPrepared());
        audioPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {
            onAudioError();
            return false;
        });

        audioProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    if (audioPlayer != null && fromUser) {
                        audioPlayer.seekTo(progress * 1000);
                    }
                } catch (Exception e) {
                    Log.e(StarTappedApp.TAG, "Failed to handle seek", e);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        muteUnmuteAudioButton.setOnClickListener(v -> onAudioMuteButtonClick());
        playPauseAudioButton.setOnClickListener(v -> onAudioPlayButtonClick());
    }

    public AudioContainer(View view, FragmentActivity fragment) {
        fragmentActivity = fragment;
        activity = null;

        audioContainer = view.findViewById(R.id.audio_container);
        playPauseAudioButton = view.findViewById(R.id.play_pause_audio);
        audioProgressBar = view.findViewById(R.id.audio_progress_bar);
        muteUnmuteAudioButton = view.findViewById(R.id.mute_unmute_audio);
        audioNameDisplay = view.findViewById(R.id.audio_file_name_display);

        audioPlayer = new MediaPlayer();
        audioPlayer.setOnPreparedListener(mediaPlayer -> onAudioPrepared());
        audioPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {
            onAudioError();
            return false;
        });

        audioProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    if (audioPlayer != null && fromUser) {
                        audioPlayer.seekTo(progress * 1000);
                    }
                } catch (Exception e) {
                    Log.e(StarTappedApp.TAG, "Failed to handle seek", e);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        muteUnmuteAudioButton.setOnClickListener(v -> onAudioMuteButtonClick());
        playPauseAudioButton.setOnClickListener(v -> onAudioPlayButtonClick());
    }

    public void fromURL(UploadedFile file) {
        audioPlayer.release();
        audioPlayer = new MediaPlayer();
        audioPlayer.setOnPreparedListener(mediaPlayer -> onAudioPrepared());
        audioPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {
            onAudioError();
            return false;
        });

        //TODO: Show loading until done.


        audioNameDisplay.setText(file.getName());
        new DownloadAudioTask(audioPlayer, file.getUrl()).execute();
    }

    public void fromPath(String path) {
        audioPlayer.release();
        audioPlayer = new MediaPlayer();
        audioPlayer.setOnPreparedListener(mediaPlayer -> onAudioPrepared());
        audioPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {
            onAudioError();
            return false;
        });

        //TODO: Show loading until done.

        audioNameDisplay.setText(new File(path).getName());
        new LoadAudioFromFileTask(audioPlayer).execute(path);
    }

    //Getters
    public ConstraintLayout getAudioContainer() {
        return audioContainer;
    }

    public Button getPlayPauseAudioButton() {
        return playPauseAudioButton;
    }

    public Button getMuteUnmuteAudioButton() {
        return muteUnmuteAudioButton;
    }

    public SeekBar getAudioProgressBar() {
        return audioProgressBar;
    }

    public TextView getAudioNameDisplay() {
        return audioNameDisplay;
    }

    public MediaPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public boolean isMuted() {
        return muted;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    //Setters
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    //Functions
    private void onAudioPlayButtonClick() {
        if (audioPlayer != null && prepared) {
            if (audioPlayer.isPlaying()) {
                playPauseAudioButton.setBackground(StarTappedApp.getContext().getResources().getDrawable(R.drawable.baseline_pause_24));

                audioPlayer.pause();
                handler.removeCallbacksAndMessages(null);
            } else {
                playPauseAudioButton.setBackground(StarTappedApp.getContext().getResources().getDrawable(R.drawable.baseline_play_arrow_24));

                audioPlayer.start();
                setupSeekbar();
            }
        }
    }

    private void onAudioMuteButtonClick() {
        if (prepared) {
            if (muted) {
                muted = false;
                muteUnmuteAudioButton.setBackground(StarTappedApp.getContext().getResources().getDrawable(R.drawable.baseline_volume_up_24));

                audioPlayer.setVolume(1, 1);
            } else {
                muted = true;
                muteUnmuteAudioButton.setBackground(StarTappedApp.getContext().getResources().getDrawable(R.drawable.baseline_volume_off_24));

                audioPlayer.setVolume(0, 0);
            }
        }
    }

    private void onAudioPrepared() {
        prepared = true;
        //TODO: Hide loading icon...

        audioProgressBar.setMax(audioPlayer.getDuration() / 1000);

        audioPlayer.setLooping(true);

        if (autoPlay) {
            if (!audioPlayer.isPlaying()) {
                audioPlayer.start();
                setupSeekbar();
            }
        } else {
            if (audioPlayer.isPlaying()) {
                audioPlayer.stop();
                handler.removeCallbacksAndMessages(null);
            }
        }

        fixButtons();
    }

    private void setupSeekbar() {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (audioPlayer != null) {
                        try {
                            int mCurrentPosition = audioPlayer.getCurrentPosition() / 1000;
                            audioProgressBar.setProgress(mCurrentPosition);
                        } catch (IllegalStateException ignore) {
                        }
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else if (fragmentActivity != null) {
            fragmentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (audioPlayer != null) {
                        try {
                            int mCurrentPosition = audioPlayer.getCurrentPosition() / 1000;
                            audioProgressBar.setProgress(mCurrentPosition);
                        } catch (IllegalStateException ignore) {
                        }
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private void fixButtons() {
        if (audioPlayer.isPlaying()) {
            playPauseAudioButton.setBackground(StarTappedApp.getContext().getResources().getDrawable(R.drawable.baseline_play_arrow_24));
        } else {
            playPauseAudioButton.setBackground(StarTappedApp.getContext().getResources().getDrawable(R.drawable.baseline_pause_24));
        }
    }

    private void onAudioError() {
        //TODO: Hide loading icon...

        audioNameDisplay.setText(R.string.error_audio_load_fail);
    }

    public void makeVisible() {
        audioContainer.setVisibility(View.VISIBLE);
    }

    public void makeGone() {
        audioContainer.setVisibility(View.GONE);
    }

    public void stop() {
        if (audioPlayer != null) {
            audioPlayer.stop();
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void play() {
        if (audioPlayer != null) {
            audioPlayer.start();
            setupSeekbar();
        }
    }
}
