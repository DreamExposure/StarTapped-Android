package org.dreamexposure.startapped.objects.post;

import org.dreamexposure.startapped.enums.post.PostType;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author NovaFox161
 * Date Created: 12/17/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class VideoPost extends Post {
    private String videoUrl;

    public VideoPost() {
        setPostType(PostType.VIDEO);
    }

    //Getters
    public String getVideoUrl() {
        return videoUrl;
    }

    //Setters
    public void setVideoUrl(String _videoUrl) {
        videoUrl = _videoUrl;
    }

    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();

        try {
            base.put("video_url", videoUrl);
        } catch (JSONException ignore) {
        }

        return base;
    }

    @Override
    public VideoPost fromJson(JSONObject json) {
        super.fromJson(json);

        try {
            videoUrl = json.getString("video_url");
        } catch (JSONException ignore) {
        }

        return this;
    }
}