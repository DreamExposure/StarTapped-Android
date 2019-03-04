package org.dreamexposure.startapped.async.load;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.media.ExifInterface;
import android.util.Log;

import com.felipecsl.gifimageview.library.GifImageView;
import com.google.android.gms.common.util.IOUtils;

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
    private GifImageView imageView;
    private byte[] bytes = null;
    private Bitmap bitmap = null;

    private String filePath;

    public LoadImageFromFileTask(GifImageView imageView) {
        this.imageView = imageView;
    }

    protected Boolean doInBackground(String... filePaths) {
        filePath = filePaths[0];
        if (ImageUtils.isGifFromGallery(filePath)) {
            try {
                InputStream in = new FileInputStream(new File(filePath));
                bytes = IOUtils.toByteArray(in);

                in.close();
                return true;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        } else {
            try {
                InputStream in = new FileInputStream(new File(filePath));
                bitmap = BitmapFactory.decodeStream(in);

                in.close();
                return true;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    protected void onPostExecute(Boolean result) {
        imageView.setImageBitmap(null);

        if (bitmap != null) {
            if (imageView.isAnimating())
                imageView.clear();

            //Fix rotation...
            try {
                ExifInterface exif = new ExifInterface(new File(filePath).getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Matrix matrix = new Matrix();
                if (orientation == 6)
                    matrix.postRotate(90);
                else if (orientation == 3)
                    matrix.postRotate(180);
                else if (orientation == 8)
                    matrix.postRotate(270);

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (Exception ignore) {
            }

            imageView.setImageBitmap(bitmap);
        }
        if (bytes != null) {
            imageView.setBytes(bytes);
            imageView.gotoFrame(0);
            if (!imageView.isAnimating())
                imageView.startAnimation();
        }
    }
}
