package org.dreamexposure.startapped.objects.container;

import android.media.MediaPlayer;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.async.load.LoadVideoFromFileTask;
import org.dreamexposure.startapped.network.download.DownloadVideoTask;

@SuppressWarnings("unused")
public class VideoContainer {
    private ConstraintLayout videoContainer;
    private VideoView postVideo;
    private View videoBackground;
    private MediaPlayer videoPlayer;

    private int initialHeight = -1;
    private int initialWidth = -1;

    private boolean autoPlay;

    public VideoContainer(View view) {
        videoContainer = view.findViewById(R.id.video_container);
        postVideo = view.findViewById(R.id.post_video);
        videoBackground = view.findViewById(R.id.post_video_background);

        postVideo.setOnPreparedListener(this::onVideoPrepared);

        videoContainer.setOnClickListener(v -> {
            if (videoPlayer.isPlaying())
                videoPlayer.pause();
            else
                videoPlayer.start();
        });
    }

    public void fromURL(String url) {
        new DownloadVideoTask(postVideo, url).execute();
    }

    public void fromPath(String path) {
        new LoadVideoFromFileTask(postVideo).execute(path);
    }

    //Getters
    public ConstraintLayout getVideoContainer() {
        return videoContainer;
    }

    public VideoView getPostVideo() {
        return postVideo;
    }

    public View getVideoBackground() {
        return videoBackground;
    }

    public MediaPlayer getVideoPlayer() {
        return videoPlayer;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    //Setters
    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    //Functions
    private void onVideoPrepared(MediaPlayer mp) {
        videoPlayer = mp;
        mp.setScreenOnWhilePlaying(true);

        // Adjust the size of the video
        // so it fits on the screen
        int videoWidth = videoPlayer.getVideoWidth();
        int videoHeight = videoPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = videoContainer.getWidth();
        int screenHeight = videoContainer.getHeight();
        if (initialWidth > -1)
            screenWidth = initialWidth;
        else
            initialWidth = screenWidth;
        if (initialHeight > -1)
            screenHeight = initialHeight;
        else
            initialHeight = screenHeight;

        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams lp = postVideo.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        postVideo.setLayoutParams(lp);

        ViewGroup.LayoutParams containerLp = videoContainer.getLayoutParams();
        //containerLp.height = postVideo.getLayoutParams().height;
        videoContainer.setLayoutParams(containerLp);

        videoPlayer.setLooping(true);

        if (autoPlay) {
            if (!videoPlayer.isPlaying())
                videoPlayer.start();
        } else {
            if (videoPlayer.isPlaying())
                videoPlayer.stop();
        }
    }

    public void makeVisible() {
        videoContainer.setVisibility(View.VISIBLE);
    }

    public void makeGone() {
        videoContainer.setVisibility(View.GONE);
    }
}
