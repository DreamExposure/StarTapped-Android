package org.dreamexposure.startapped.objects.file;

import android.util.Log;

import org.dreamexposure.startapped.StarTappedApp;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class UploadedFile {
    private String hash;
    private String path;
    private String type;
    private String url;
    private UUID uploader;
    private long timestamp;
    private String name;

    //Getters
    public String getHash() {
        return hash;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public UUID getUploader() {
        return uploader;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    //Setters
    public void setHash(String _hash) {
        hash = _hash;
    }

    public void setPath(String _path) {
        path = _path;
    }

    public void setType(String _type) {
        type = _type;
    }

    public void setUrl(String _url) {
        url = _url;
    }

    public void setUploader(UUID _uploader) {
        uploader = _uploader;
    }

    public void setTimestamp(long _timestamp) {
        timestamp = _timestamp;
    }

    public void setName(String _name) {
        name = _name;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("hash", hash);
            json.put("path", path);
            json.put("url", url);
            json.put("uploader", uploader.toString());
            json.put("timestamp", timestamp);
            json.put("name", name);
        } catch (JSONException ignore) {
        }

        return json;
    }

    public UploadedFile fromJson(JSONObject json) {
        try {
            hash = json.getString("hash");
            path = json.getString("path");
            url = json.getString("url");
            uploader = UUID.fromString(json.getString("uploader"));
            timestamp = json.getLong("timestamp");
            name = json.getString("name");
        } catch (JSONException e) {
            Log.e(StarTappedApp.TAG, "Failed to get UploadedFile from JSON", e);
        }

        return this;
    }
}
