package org.dreamexposure.startapped.dialogs.post;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import org.dreamexposure.startapped.R;

import java.util.ArrayList;

import butterknife.ButterKnife;


@SuppressWarnings("ConstantConditions")
public class AddTagsDialogFragment extends DialogFragment {
    View mainView;

    Toolbar toolbar;

    EditText tagsText;

    ArrayList<String> tags = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);

        tags = getArguments().getStringArrayList("tags");

    }

    @SuppressWarnings("NullableProblems")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_Slide;
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //Load all of the views...
        mainView = inflater.inflate(R.layout.fragment_add_tags_dialog, container, false);
        ButterKnife.bind(mainView);

        toolbar = mainView.findViewById(R.id.toolbar);

        tagsText = mainView.findViewById(R.id.tags_text);

        //Set the main toolbar
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setTitle(R.string.post_add_tags);
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.inflateMenu(R.menu.post_add_tags_toolbar);

        String tagsTextRaw = tags.toString();
        tagsText.setText(tagsTextRaw.substring(1, tagsTextRaw.length() - 1)); //Strip the [].

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

    @Override
    public void onDismiss(DialogInterface dialog) {
        //Get the tags from the text edit view, add them to the tags array list
        tags.clear();
        for (String t : tagsText.getText().toString().split(",")) {
            tags.add(t.trim());
        }

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("tags", tags);

        Intent in = new Intent();
        in.putExtras(bundle);

        onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, in);
        super.onDismiss(dialog);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.close_tags) {
            dismiss();
            return true;
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }
}
