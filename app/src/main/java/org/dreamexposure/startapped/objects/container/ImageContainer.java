package org.dreamexposure.startapped.objects.container;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.felipecsl.gifimageview.library.GifImageView;

import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.async.load.LoadImageFromFileTask;
import org.dreamexposure.startapped.dialogs.image.ImageDialog;
import org.dreamexposure.startapped.network.download.DownloadImageTask;

@SuppressWarnings("unused")
public class ImageContainer {
    private GifImageView imageView;

    private String fileUrl = null;
    private String filePath = null;

    private boolean useDialog;

    private FragmentManager manager;

    public ImageContainer(GifImageView _imageView, FragmentManager _manager, boolean _useDialog) {
        imageView = _imageView;

        manager = _manager;

        useDialog = _useDialog;

        if (useDialog)
            imageView.setOnClickListener(view -> onPostImageClick());
    }

    public void fromUrl(String url) {
        fileUrl = url;
        filePath = null;

        new DownloadImageTask(imageView).execute(fileUrl);
    }

    public void fromFile(String file) {
        filePath = file;
        fileUrl = null;

        new LoadImageFromFileTask(imageView).execute(filePath);
    }

    //Getters
    public GifImageView getImageView() {
        return imageView;
    }

    public boolean isUseDialog() {
        return useDialog;
    }

    //Setters
    public void setUseDialog(boolean useDialog) {
        this.useDialog = useDialog;
    }

    //Functions
    private void onPostImageClick() {
        if (manager != null) {
            if (filePath != null) {
                ImageDialog dialog = new ImageDialog();
                FragmentTransaction ft = manager.beginTransaction();
                Bundle b = new Bundle();
                b.putString("file_path", filePath);
                dialog.setArguments(b);
                dialog.show(ft, StarTappedApp.TAG);
            } else if (fileUrl != null) {
                ImageDialog dialog = new ImageDialog();
                FragmentTransaction ft = manager.beginTransaction();
                Bundle b = new Bundle();
                b.putString("file_url", fileUrl);
                dialog.setArguments(b);
                dialog.show(ft, StarTappedApp.TAG);
            }
        }
    }
}