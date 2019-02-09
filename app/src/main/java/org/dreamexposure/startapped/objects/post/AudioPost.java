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
public class AudioPost extends Post {
    private UploadedFile audio;

    public AudioPost() {
        setPostType(PostType.AUDIO);
    }

    //Getters
    public UploadedFile getAudio() {
        return audio;
    }

    //Setters
    public void setAudio(UploadedFile _audio) {
        audio = _audio;
    }

    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();

        try {
            base.put("audio", audio.toJson());
        } catch (JSONException ignore) {
        }

        return base;
    }

    @Override
    public AudioPost fromJson(JSONObject json) {
        super.fromJson(json);

        try {
            audio = new UploadedFile().fromJson(json.getJSONObject("audio"));
        } catch (JSONException ignore) {
        }

        return this;
    }
}