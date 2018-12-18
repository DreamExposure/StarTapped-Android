package org.dreamexposure.startapped.objects.blog;

import org.dreamexposure.startapped.enums.blog.BlogType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/17/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class PersonalBlog extends Blog {
    private UUID ownerId;

    private boolean displayAge;


    public PersonalBlog() {
        setType(BlogType.PERSONAL);
    }

    //Getters
    public UUID getOwnerId() {
        return ownerId;
    }

    public boolean isDisplayAge() {
        return displayAge;
    }

    //Setters
    public void setOwnerId(UUID _ownerId) {
        ownerId = _ownerId;
    }

    public void setDisplayAge(boolean _displayAge) {
        displayAge = _displayAge;
    }

    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();

        try {
            base.put("owner_id", ownerId.toString());
            base.put("display_age", displayAge);
        } catch (JSONException ignore) {
        }

        return base;
    }

    @Override
    public PersonalBlog fromJson(JSONObject json) {
        super.fromJson(json);

        try {
            ownerId = UUID.fromString(json.getString("owner_id"));
            displayAge = json.getBoolean("display_age");
        } catch (JSONException ignore) {
        }

        return this;
    }
}
