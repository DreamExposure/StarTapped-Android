package org.dreamexposure.startapped.objects.post;

import org.dreamexposure.startapped.enums.post.PostType;
import org.json.JSONObject;

/**
 * @author NovaFox161
 * Date Created: 12/17/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class TextPost extends Post {

    public TextPost() {
        setPostType(PostType.TEXT);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();

        return base;
    }

    @Override
    public TextPost fromJson(JSONObject json) {
        super.fromJson(json);

        return this;
    }
}
