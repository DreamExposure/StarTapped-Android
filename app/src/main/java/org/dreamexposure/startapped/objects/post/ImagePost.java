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
public class ImagePost extends Post {
    private UploadedFile image;

    public ImagePost() {
        setPostType(PostType.IMAGE);
    }

    //Getters
    public UploadedFile getImage() {
        return image;
    }

    //Setters
    public void setImage(UploadedFile _image) {
        image = _image;
    }

    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();

        try {
            base.put("image", image.toJson());
        } catch (JSONException ignore) {
        }

        return base;
    }

    @Override
    public ImagePost fromJson(JSONObject json) {
        super.fromJson(json);

        try {
            image = new UploadedFile().fromJson(json.getJSONObject("image"));
        } catch (JSONException ignore) {
        }

        return this;
    }
}