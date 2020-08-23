package org.dreamexposure.startapped.objects.post;

import androidx.annotation.NonNull;

import org.dreamexposure.startapped.enums.post.PostType;
import org.dreamexposure.startapped.objects.blog.IBlog;
import org.dreamexposure.startapped.objects.user.Account;
import org.json.JSONObject;

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
public interface IPost extends Comparable<IPost> {

    //Getters
    UUID getId();

    Account getCreator();

    IBlog getOriginBlog();

    String getPermaLink();

    String getFullUrl();

    long getTimestamp();

    PostType getPostType();

    String getTitle();

    String getBody();

    boolean isNsfw();

    UUID getParent();

    boolean isBookmarked();

    List<String> getTags();

    String tagsToString();

    //Setters
    void setId(UUID _id);

    void setCreator(Account _creator);

    void setOriginBlog(IBlog _blog);

    void setPermaLink(String _permaLink);

    void setFullUrl(String _fullLink);

    void setTimestamp(long _timestamp);

    void setPostType(PostType _type);

    void setTitle(String _title);

    void setBody(String _body);

    void setNsfw(boolean _nsfw);

    void setParent(UUID _parent);

    void setBookmarked(boolean _bookmarked);

    JSONObject toJson();

    IPost fromJson(JSONObject json);

    @Override
    int compareTo(@NonNull IPost iPost);
}
