package org.dreamexposure.startapped.objects.blog;

import org.dreamexposure.startapped.enums.blog.BlogType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/17/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class GroupBlog extends Blog {
    private List<UUID> owners = new ArrayList<UUID>();


    public GroupBlog() {
        setType(BlogType.GROUP);
    }

    //Getters
    public List<UUID> getOwners() {
        return owners;
    }

    //Setters

    @Override
    public JSONObject toJson() {
        JSONObject base = super.toJson();

        JSONArray jOwners = new JSONArray();
        for (UUID id : owners) {
            jOwners.put(id.toString());
        }

        try {
            base.put("owners", jOwners);
        } catch (JSONException ignore) {
        }

        return base;
    }

    @Override
    public GroupBlog fromJson(JSONObject json) {
        super.fromJson(json);

        try {
            JSONArray jOwners = json.getJSONArray("owners");
            for (int i = 0; i < jOwners.length(); i++) {
                owners.add(UUID.fromString(jOwners.getString(i)));
            }

        } catch (JSONException ignore) {
        }
        return this;
    }
}
