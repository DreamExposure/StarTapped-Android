package org.dreamexposure.startapped.dialogs.post;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.felipecsl.gifimageview.library.GifImageView;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.enums.blog.BlogType;
import org.dreamexposure.startapped.enums.post.PostType;
import org.dreamexposure.startapped.network.blog.self.GetBlogsSelfTask;
import org.dreamexposure.startapped.network.download.DownloadImageTask;
import org.dreamexposure.startapped.network.post.PostCreateTask;
import org.dreamexposure.startapped.objects.blog.GroupBlog;
import org.dreamexposure.startapped.objects.blog.IBlog;
import org.dreamexposure.startapped.objects.blog.PersonalBlog;
import org.dreamexposure.startapped.objects.container.AudioContainer;
import org.dreamexposure.startapped.objects.container.ImageContainer;
import org.dreamexposure.startapped.objects.container.VideoContainer;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.dreamexposure.startapped.objects.post.Post;
import org.dreamexposure.startapped.utils.ImageUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.ButterKnife;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

/**
 * @author NovaFox161
 * Date Created: 1/1/2019
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings("ConstantConditions")
public class CreatePostDialogFragment extends DialogFragment implements TaskCallback {
    View mainView;

    Toolbar toolbar;

    Button actionSelectImage;
    Button actionSelectAudio;
    Button actionSelectVideo;

    GifImageView blogIcon;
    AppCompatSpinner blogSelectSpinner;

    GifImageView postImage;
    ImageContainer imageContainer;

    VideoContainer videoContainer;

    AudioContainer audioContainer;

    EditText postTitle;
    EditText postBody;

    String filePath = "";
    final int IMAGE_CODE = 1122;
    final int AUDIO_CODE = 2233;
    final int VIDEO_CODE = 3344;
    private final String[] audioType = {"mp3", ".wav"};

    private PostType postType = PostType.TEXT;

    private LinkedList<IBlog> blogs = new LinkedList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);

    }

    @SuppressWarnings("NullableProblems")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_Slide;
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //Load all of the views...
        mainView = inflater.inflate(R.layout.post_create_container, container, false);
        ButterKnife.bind(mainView);

        toolbar = mainView.findViewById(R.id.toolbar);
        actionSelectImage = mainView.findViewById(R.id.action_select_image);
        actionSelectAudio = mainView.findViewById(R.id.action_select_audio);
        actionSelectVideo = mainView.findViewById(R.id.action_select_video);

        blogIcon = mainView.findViewById(R.id.blog_icon_image);
        blogSelectSpinner = mainView.findViewById(R.id.pick_blog_spinner);

        postImage = mainView.findViewById(R.id.post_image);
        imageContainer = new ImageContainer(postImage, getFragmentManager(), true);
        videoContainer = new VideoContainer(mainView);
        videoContainer.setAutoPlay(true);

        audioContainer = new AudioContainer(mainView, getActivity());
        audioContainer.setAutoPlay(true);

        postTitle = mainView.findViewById(R.id.post_title);
        postBody = mainView.findViewById(R.id.post_body);

        //Set the main toolbar
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setTitle("Create Post");
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.inflateMenu(R.menu.post_create_toolbar);

        //Request user's blogs to eventually load spinner...
        new GetBlogsSelfTask(this).execute();

        //register on click listeners...
        actionSelectImage.setOnClickListener(v -> onSelectImageClick());
        actionSelectAudio.setOnClickListener(v -> onSelectAudioClick());
        actionSelectVideo.setOnClickListener(v -> onSelectVideoClick());

        //Hide everything just in case...
        postImage.setVisibility(View.GONE);
        videoContainer.makeGone();
        audioContainer.makeGone();

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
        if (videoContainer.getVideoPlayer() != null)
            videoContainer.getVideoPlayer().release();
        if (audioContainer.getAudioPlayer() != null) {
            audioContainer.stop();
            audioContainer.getAudioPlayer().release();
        }
        super.onDismiss(dialog);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_post:
                //Create post!
                Post post = new Post();
                post.setPostType(postType);
                post.setTitle(postTitle.getText().toString().trim());
                post.setBody(postBody.getText().toString().trim());
                post.setOriginBlog(blogs.get(blogSelectSpinner.getSelectedItemPosition()));

                try {
                    if (postType == PostType.IMAGE || postType == PostType.AUDIO || postType == PostType.VIDEO) {
                        new PostCreateTask(this, post, ImageUtils.fileToBase64(new File(filePath))).execute();
                        //TODO: Show loading animation...
                    } else {
                        new PostCreateTask(this, post).execute();
                        //TODO: Show loading animation...
                    }
                } catch (Exception e) {
                    Log.e(StarTappedApp.TAG, "Failed to handle post create", e);
                    Toast.makeText(mainView.getContext(), "Failed to handle post create...", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void taskCallback(NetworkCallStatus status) {
        switch (status.getType()) {
            case BLOG_GET_SELF_ALL:
                if (status.isSuccess())
                    setupBlogSelection(status);
                break;
            case POST_CREATE:
                if (status.isSuccess()) {
                    Toast.makeText(mainView.getContext(), status.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(mainView.getContext(), status.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void setupBlogSelection(NetworkCallStatus status) {
        try {
            if (status.getBody().has("blogs")) {
                JSONArray jBlogs = status.getBody().getJSONArray("blogs");

                for (int i = 0; i < jBlogs.length(); i++) {
                    JSONObject jBlog = jBlogs.getJSONObject(i);
                    switch (BlogType.valueOf(jBlog.getString("type").toUpperCase())) {
                        case PERSONAL:
                            blogs.add(new PersonalBlog().fromJson(jBlog));
                            break;
                        case GROUP:
                            blogs.add(new GroupBlog().fromJson(jBlog));
                            break;
                    }
                }

                LinkedList<String> blogUrls = new LinkedList<>();
                String iconImage = "https://cdn.startapped.com/img/default/profile.jpg";
                boolean blogSelected = false;
                for (IBlog b : blogs) {
                    blogUrls.add(b.getBaseUrl());
                    if (!blogSelected) {
                        blogSelected = true;
                        iconImage = b.getIconImage().getUrl();
                    }
                }

                //Okay, add to spinner and set icon image....
                ArrayAdapter<String> adapter = new ArrayAdapter<>(mainView.getContext(), R.layout.spinner_item, blogUrls);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                blogSelectSpinner.setAdapter(adapter);
                blogSelectSpinner.setSelection(0);
                blogSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    private int iCurrentSelection = 0;

                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (iCurrentSelection != i) {
                            iCurrentSelection = i;
                            new DownloadImageTask(blogIcon).execute(blogs.get(iCurrentSelection).getIconImage().getUrl());
                        }
                        iCurrentSelection = i;
                    }

                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                new DownloadImageTask(blogIcon).execute(iconImage);
            }
        } catch (JSONException ignore) {
        }
    }

    void onSelectImageClick() {
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setActivityTheme(R.style.LibAppTheme)
                .enableImagePicker(true)
                .enableCameraSupport(true)
                .enableVideoPicker(false)
                .enableDocSupport(false)
                .showGifs(true)
                .setActivityTitle(getResources().getString(R.string.pick_post_image))
                .pickPhoto(this, IMAGE_CODE);
    }

    void onSelectAudioClick() {
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setActivityTheme(R.style.LibAppTheme)
                .enableImagePicker(false)
                .enableCameraSupport(false)
                .enableVideoPicker(false)
                .enableDocSupport(false)
                .showGifs(false)
                .addFileSupport(getResources().getString(R.string.select_media_type_audio), audioType)
                .setActivityTitle(getResources().getString(R.string.pick_post_audio))
                .pickFile(this, AUDIO_CODE);
    }

    void onSelectVideoClick() {
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setActivityTheme(R.style.LibAppTheme)
                .enableImagePicker(false)
                .enableCameraSupport(true)
                .enableVideoPicker(true)
                .enableDocSupport(false)
                .showGifs(false)
                .setActivityTitle(getResources().getString(R.string.pick_post_video))
                .pickPhoto(this, VIDEO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IMAGE_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    //Conversion to base64 will be handled once the user confirms edits.
                    ArrayList<String> photoPaths = new ArrayList<>(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    filePath = photoPaths.get(0);

                    //Check file size...
                    long fileSizeInMb = new File(filePath).length() / (1024 * 1024);
                    if (fileSizeInMb > 10) {
                        Toast.makeText(mainView.getContext(), R.string.error_over_10mb, Toast.LENGTH_LONG).show();
                        filePath = null;
                        postType = PostType.TEXT;
                        postImage.setVisibility(View.GONE);
                        videoContainer.makeGone();
                        videoContainer.getPostVideo().stopPlayback();
                        audioContainer.makeGone();
                        audioContainer.stop();
                        return;
                    }

                    //Hide other views
                    videoContainer.makeGone();
                    videoContainer.getPostVideo().stopPlayback();
                    audioContainer.makeGone();
                    audioContainer.stop();

                    //Display image
                    postType = PostType.IMAGE;

                    postImage.setVisibility(View.VISIBLE);

                    imageContainer.fromFile(filePath);
                }
                break;
            case AUDIO_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    //Conversion to base64 will be handled once the user confirms edits.
                    ArrayList<String> audioPaths = new ArrayList<>(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    filePath = audioPaths.get(0);

                    //Check file size...
                    long fileSizeInMb = new File(filePath).length() / (1024 * 1024);
                    if (fileSizeInMb > 10) {
                        Toast.makeText(mainView.getContext(), R.string.error_over_10mb, Toast.LENGTH_LONG).show();
                        filePath = null;
                        postType = PostType.TEXT;
                        postImage.setVisibility(View.GONE);
                        videoContainer.makeGone();
                        videoContainer.getPostVideo().stopPlayback();
                        audioContainer.makeGone();
                        audioContainer.getAudioPlayer().stop();
                        return;
                    }

                    //Hide other views
                    postImage.setVisibility(View.GONE);
                    videoContainer.makeGone();
                    videoContainer.getPostVideo().stopPlayback();

                    //Display audio
                    postType = PostType.AUDIO;

                    audioContainer.makeVisible();
                    audioContainer.fromPath(filePath);
                }
                break;
            case VIDEO_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    //Conversion to base64 will be handled once the user confirms edits.
                    ArrayList<String> videoPaths = new ArrayList<>(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    filePath = videoPaths.get(0);

                    //Check file size...
                    long fileSizeInMb = new File(filePath).length() / (1024 * 1024);
                    if (fileSizeInMb > 10) {
                        Toast.makeText(mainView.getContext(), R.string.error_over_10mb, Toast.LENGTH_LONG).show();
                        filePath = null;
                        postType = PostType.TEXT;
                        postImage.setVisibility(View.GONE);
                        videoContainer.makeGone();
                        videoContainer.getPostVideo().stopPlayback();
                        audioContainer.makeGone();
                        audioContainer.getAudioPlayer().stop();
                        return;
                    }

                    //Hide other views
                    postImage.setVisibility(View.GONE);
                    audioContainer.makeGone();
                    audioContainer.getAudioPlayer().stop();

                    //Display video
                    postType = PostType.VIDEO;

                    videoContainer.makeVisible();
                    videoContainer.fromPath(filePath);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
