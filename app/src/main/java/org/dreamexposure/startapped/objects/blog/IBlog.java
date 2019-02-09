package org.dreamexposure.startapped.objects.blog;

import org.dreamexposure.startapped.enums.blog.BlogType;
import org.dreamexposure.startapped.objects.file.UploadedFile;
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
public interface IBlog {
    //Getters
    UUID getBlogId();

    String getBaseUrl();

    String getCompleteUrl();

    BlogType getType();

    String getName();

    String getDescription();

    UploadedFile getIconImage();

    String getBackgroundColor();

    UploadedFile getBackgroundImage();

    boolean isAllowUnder18();

    boolean isNsfw();

    List<UUID> getFollowers();

    //Setters
    void setBlogId(UUID _blogId);

    void setBaseUrl(String _baseUrl);

    void setCompleteUrl(String _completeUrl);

    void setType(BlogType _type);

    void setName(String _name);

    void setDescription(String _description);

    void setIconImage(UploadedFile _iconImage);

    void setBackgroundColor(String _backgroundColor);

    void setBackgroundImage(UploadedFile _backgroundImage);

    void setAllowUnder18(boolean _allowUnder18);

    void setNsfw(boolean _nsfw);

    JSONObject toJson();

    IBlog fromJson(JSONObject json);
}