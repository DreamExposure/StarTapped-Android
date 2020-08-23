package org.dreamexposure.startapped.objects.container;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;

import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.dialogs.image.ImageDialog;

import java.io.File;

@SuppressWarnings("unused")
public class ImageContainer {
    private ImageView imageView;

    private String fileUrl = null;
    private String filePath = null;

    private boolean useDialog;

    private FragmentManager manager;

    public ImageContainer(ImageView _imageView, FragmentManager _manager, boolean _useDialog) {
        imageView = _imageView;

        manager = _manager;

        useDialog = _useDialog;

        if (useDialog)
            imageView.setOnClickListener(view -> onPostImageClick());
    }

    public void fromUrl(String url) {
        fileUrl = url;
        filePath = null;

        //TODO: May need to get reference to enclosing activity
        Glide.with(StarTappedApp.getApplication()).load(fileUrl).into(imageView);
    }

    public void fromFile(String file) {
        filePath = file;
        fileUrl = null;

        Glide.with(StarTappedApp.getApplication()).load(new File(filePath)).into(imageView);
    }

    //Getters
    public ImageView getImageView() {
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
