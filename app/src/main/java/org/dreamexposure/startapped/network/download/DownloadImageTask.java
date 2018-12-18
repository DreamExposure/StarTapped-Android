package org.dreamexposure.startapped.network.download;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.felipecsl.gifimageview.library.GifImageView;

import org.apache.commons.io.IOUtils;
import org.dreamexposure.startapped.utils.ImageUtils;

import java.io.InputStream;

/**
 * @author NovaFox161
 * Date Created: 12/17/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class DownloadImageTask extends AsyncTask<String, Void, Boolean> {
    @SuppressLint("StaticFieldLeak")
    private GifImageView bmImage;
    private byte[] bytes = null;
    private Bitmap bitmap = null;

    public DownloadImageTask(GifImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Boolean doInBackground(String... urls) {
        if (ImageUtils.isGifFromURL(urls[0])) {
            try {
                InputStream in = new java.net.URL(urls[0]).openStream();
                bytes = IOUtils.toByteArray(in);

                return true;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        } else {
            try {
                InputStream in = new java.net.URL(urls[0]).openStream();
                bitmap = BitmapFactory.decodeStream(in);
                return true;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    protected void onPostExecute(Boolean result) {
        if (bitmap != null) {
            bmImage.setImageBitmap(bitmap);
        }
        if (bytes != null) {
            bmImage.setBytes(bytes);
            bmImage.startAnimation();
        }
    }
}