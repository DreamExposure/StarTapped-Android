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
public class ImagePost extends Post {
    private String imageUrl;

    public ImagePost() {
        setPostType(PostType.IMAGE);
    }

    //Getters
    public String getImageUrl() {
        return imageUrl;
    }

    //Setters
    public void setImageUrl(String _imageUrl) {
        imageUrl = _imageUrl;
    }

    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();

        try {
            base.put("image_url", imageUrl);
        } catch (JSONException ignore) {
        }

        return base;
    }

    @Override
    public ImagePost fromJson(JSONObject json) {
        super.fromJson(json);

        try {
            imageUrl = json.getString("image_url");
        } catch (JSONException ignore) {
        }

        return this;
    }
}