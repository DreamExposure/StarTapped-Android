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
public class AudioPost extends Post {
    private String audioUrl;

    public AudioPost() {
        setPostType(PostType.AUDIO);
    }

    //Getters
    public String getAudioUrl() {
        return audioUrl;
    }

    //Setters
    public void setAudioUrl(String _audioUrl) {
        audioUrl = _audioUrl;
    }

    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();

        try {
            base.put("audio_url", audioUrl);
        } catch (JSONException ignore) {
        }

        return base;
    }

    @Override
    public AudioPost fromJson(JSONObject json) {
        super.fromJson(json);

        try {
            audioUrl = json.getString("audio_url");
        } catch (JSONException ignore) {
        }

        return this;
    }
}