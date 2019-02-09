package org.dreamexposure.startapped.objects.post;

import org.dreamexposure.startapped.enums.post.PostType;
import org.dreamexposure.startapped.objects.file.UploadedFile;
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
    private UploadedFile video;

    public VideoPost() {
        setPostType(PostType.VIDEO);
    }

    //Getters
    public UploadedFile getVideo() {
        return video;
    }

    //Setters
    public void setVideo(UploadedFile _video) {
        video = _video;
    }

    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();

        try {
            base.put("video", video.toJson());
        } catch (JSONException ignore) {
        }

        return base;
    }

    @Override
    public VideoPost fromJson(JSONObject json) {
        super.fromJson(json);

        try {
            video = new UploadedFile().fromJson(json.getJSONObject("video"));
        } catch (JSONException ignore) {
        }

        return this;
    }
}