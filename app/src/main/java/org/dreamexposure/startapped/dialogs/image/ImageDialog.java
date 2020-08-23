package org.dreamexposure.startapped.dialogs.image;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

import org.dreamexposure.startapped.R;

import java.io.File;

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

    ImageView imageView;


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
            Glide.with(mainView).load(new File(filePath)).into(imageView);
        else if (fileUrl != null)
            Glide.with(mainView).load(fileUrl).into(imageView);

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
