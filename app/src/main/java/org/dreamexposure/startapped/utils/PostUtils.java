package org.dreamexposure.startapped.utils;

import org.dreamexposure.startapped.objects.post.AudioPost;
import org.dreamexposure.startapped.objects.post.IPost;
import org.dreamexposure.startapped.objects.post.ImagePost;
import org.dreamexposure.startapped.objects.post.Post;
import org.dreamexposure.startapped.objects.post.TextPost;
import org.dreamexposure.startapped.objects.post.VideoPost;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostUtils {
    public static List<IPost> getPostsFromArray(JSONArray jPosts) {
        List<IPost> posts = new ArrayList<>();

        for (int i = 0; i < jPosts.length(); i++) {
            try {
                IPost raw = new Post().fromJson(jPosts.getJSONObject(i));
                switch (raw.getPostType()) {
                    case TEXT:
                        posts.add(new TextPost().fromJson(jPosts.getJSONObject(i)));
                        break;
                    case IMAGE:
                        posts.add(new ImagePost().fromJson(jPosts.getJSONObject(i)));
                        break;
                    case AUDIO:
                        posts.add(new AudioPost().fromJson(jPosts.getJSONObject(i)));
                        break;
                    case VIDEO:
                        posts.add(new VideoPost().fromJson(jPosts.getJSONObject(i)));
                        break;
                }
            } catch (JSONException ignore) {}
        }

        return posts;
    }

    static IPost getPostFromArray(List<IPost> posts, UUID id) {
        for (IPost p : posts) {
            if (id.equals(p.getId()))
                return p;
        }
        return null;
    }
}
