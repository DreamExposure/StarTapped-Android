package org.dreamexposure.startapped.dialogs.image;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.felipecsl.gifimageview.library.GifImageView;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.async.load.LoadImageFromFileTask;
import org.dreamexposure.startapped.network.download.DownloadImageTask;

/**
 * @author NovaFox161
 * Date Created: 1/2/2019
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings("ConstantConditions")
public class ImageDialog extends DialogFragment {
    View mainView;

    String filePath;
    String fileUrl;

    GifImageView imageView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle_Transparent);

        Bundle b = getArguments();
        if (b.containsKey("file_path")) {
            filePath = b.getString("file_path");
            fileUrl = null;
        } else if (b.containsKey("file_url")) {
            fileUrl = b.getString("file_url");
            filePath = null;
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_Slide;
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //Load all of the views...
        mainView = inflater.inflate(R.layout.image_dialog, container, false);

        imageView = mainView.findViewById(R.id.dialog_image);

        if (filePath != null)
            new LoadImageFromFileTask(imageView).execute(filePath);
        else if (fileUrl != null)
            new DownloadImageTask(imageView).execute(fileUrl);

        return mainView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}