package org.dreamexposure.startapped.utils;

import android.graphics.Movie;
import android.util.Log;

import org.dreamexposure.startapped.StarTappedApp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author NovaFox161
 * Date Created: 12/17/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class ImageUtils {
    public static boolean isGifFromURL(String path) {
        try {
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            InputStream inputStream = conn.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;

            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }

            inputStream.close();
            byte[] bytes = outStream.toByteArray();

            Movie gif = Movie.decodeByteArray(bytes, 0, bytes.length);
            //If the result is true, its a animated GIF
            return gif != null;
        } catch (IOException e) {
            Log.e(StarTappedApp.TAG, "Gif from URL lookup failed", e);
        }
        return false;
    }

    public static boolean isGifFromGallery(String filePath) {
        try {
            //filePath is a String converted from a selected image's URI
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;

            while ((len = fileInputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }

            fileInputStream.close();
            byte[] bytes = outStream.toByteArray();

            Movie gif = Movie.decodeByteArray(bytes, 0, bytes.length);
            //If the result is true, its a animated GIF
            return gif != null;
        } catch (IOException ie) {
            Log.e(StarTappedApp.TAG, "Gif from Gallery lookup failed", ie);
        }
        return false;
    }
}
