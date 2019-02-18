package org.dreamexposure.startapped.objects.post;

import android.support.annotation.NonNull;

import org.dreamexposure.startapped.enums.post.PostType;
import org.dreamexposure.startapped.objects.blog.Blog;
import org.dreamexposure.startapped.objects.blog.IBlog;
import org.dreamexposure.startapped.objects.user.Account;
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
public class Post implements IPost, Comparable<IPost> {
    private UUID id;
    private Account creator;
    private IBlog originBlog;
    private String permaLink;
    private String fullUrl;
    private PostType type;
    private long timestamp;

    private String title;
    private String body;

    private boolean nsfw;

    private UUID parent;

    private List<String> tags = new ArrayList<>();

    //Getters
    public UUID getId() {
        return id;
    }

    public Account getCreator() {
        return creator;
    }

    public IBlog getOriginBlog() {
        return originBlog;
    }

    public String getPermaLink() {
        return permaLink;
    }

    public String getFullUrl() {
        return fullUrl;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public PostType getPostType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    @Override
    public UUID getParent() {
        return parent;
    }

    public List<String> getTags() {
        return tags;
    }

    public String tagsToString() {
        if (!tags.isEmpty()) {
            String t = tags.toString();
            return t.substring(1, t.length() - 1); //Should cut off the []
        } else {
            return "";
        }
    }

    //Setters
    public void setId(UUID _id) {
        id = _id;
    }

    public void setCreator(Account _creator) {
        creator = _creator;
    }

    @Override
    public void setOriginBlog(IBlog _blog) {
        originBlog = _blog;
    }

    public void setOriginBlog(Blog _blog) {
        originBlog = _blog;
    }

    public void setPermaLink(String _permaLink) {
        permaLink = _permaLink;
    }

    public void setFullUrl(String _fullUrl) {
        fullUrl = _fullUrl;
    }

    public void setTimestamp(long _timestamp) {
        timestamp = _timestamp;
    }

    public void setPostType(PostType _type) {
        type = _type;
    }

    public void setTitle(String _title) {
        title = _title;
    }

    public void setBody(String _body) {
        body = _body;
    }

    public void setNsfw(boolean _nsfw) {
        nsfw = _nsfw;
    }

    @Override
    public void setParent(UUID _parent) {
        parent = _parent;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("id", id.toString());
            json.put("creator", creator.toJson());
            json.put("origin_blog", originBlog.toJson());
            json.put("permalink", permaLink);
            json.put("full_url", fullUrl);
            json.put("timestamp", timestamp);
            json.put("type", type.name());
            json.put("title", title);
            json.put("body", body);
            json.put("nsfw", nsfw);
            if (parent != null)
                json.put("parent", parent);

            JSONArray jTags = new JSONArray();
            for (String t : tags) {
                jTags.put(t);
            }
            json.put("tags", jTags);

        } catch (JSONException ignore) {
        }

        return json;
    }

    public IPost fromJson(JSONObject json) {
        try {
            id = UUID.fromString(json.getString("id"));
            creator = new Account().fromJson(json.getJSONObject("creator"));
            originBlog = new Blog().fromJson(json.getJSONObject("origin_blog"));
            permaLink = json.getString("permalink");
            fullUrl = json.getString("full_url");
            timestamp = json.getLong("timestamp");
            type = PostType.valueOf(json.getString("type"));
            title = json.getString("title");
            body = json.getString("body");
            nsfw = json.getBoolean("nsfw");
            if (json.has("parent"))
                parent = UUID.fromString(json.getString("parent"));

            JSONArray jTags = json.getJSONArray("tags");
            for (int i = 0; i < jTags.length(); i++) {
                tags.add(jTags.getString(i));
            }

        } catch (JSONException ignore) {
        }

        return this;
    }

    @Override
    public int compareTo(@NonNull IPost iPost) {
        return Long.compare(iPost.getTimestamp(), this.getTimestamp());
    }
}