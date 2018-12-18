package org.dreamexposure.startapped.objects.post;

import org.dreamexposure.startapped.enums.post.PostType;
import org.dreamexposure.startapped.objects.blog.Blog;
import org.dreamexposure.startapped.objects.user.Account;
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
public class Post implements IPost {
    private UUID id;
    private Account creator;
    private Blog originBlog;
    private String permaLink;
    private String fullUrl;
    private PostType type;
    private long timestamp;

    private String title;
    private String body;

    private boolean nsfw;


    //Getters
    public UUID getId() {
        return id;
    }

    public Account getCreator() {
        return creator;
    }

    public Blog getOriginBlog() {
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

    //Setters
    public void setId(UUID _id) {
        id = _id;
    }

    public void setCreator(Account _creator) {
        creator = _creator;
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
        } catch (JSONException ignore) {
        }

        return this;
    }
}