package org.dreamexposure.startapped.async.load;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.felipecsl.gifimageview.library.GifImageView;

import org.apache.commons.io.IOUtils;
import org.dreamexposure.startapped.utils.ImageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author NovaFox161
 * Date Created: 12/20/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class LoadImageFromFileTask extends AsyncTask<String, Void, Boolean> {
    @SuppressLint("StaticFieldLeak")
    private GifImageView bmImage;
    private byte[] bytes = null;
    private Bitmap bitmap = null;

    public LoadImageFromFileTask(GifImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Boolean doInBackground(String... filePaths) {
        if (ImageUtils.isGifFromGallery(filePaths[0])) {
            try {
                InputStream in = new FileInputStream(new File(filePaths[0]));
                bytes = IOUtils.toByteArray(in);

                return true;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        } else {
            try {
                InputStream in = new FileInputStream(new File(filePaths[0]));
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
        bmImage.setImageBitmap(null);

        if (bitmap != null) {
            if (bmImage.isAnimating())
                bmImage.clear();
            bmImage.setImageBitmap(bitmap);
        }
        if (bytes != null) {
            bmImage.setBytes(bytes);
            bmImage.gotoFrame(0);
            if (!bmImage.isAnimating())
                bmImage.startAnimation();
        }
    }
}
