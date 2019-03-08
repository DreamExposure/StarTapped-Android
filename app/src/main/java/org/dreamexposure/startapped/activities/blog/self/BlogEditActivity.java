package org.dreamexposure.startapped.activities.blog.self;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.felipecsl.gifimageview.library.GifImageView;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.async.load.LoadImageFromFileTask;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.enums.blog.BlogType;
import org.dreamexposure.startapped.network.blog.self.UpdateBlogTask;
import org.dreamexposure.startapped.network.blog.view.GetBlogViewTask;
import org.dreamexposure.startapped.network.download.DownloadImageTask;
import org.dreamexposure.startapped.objects.blog.Blog;
import org.dreamexposure.startapped.objects.blog.GroupBlog;
import org.dreamexposure.startapped.objects.blog.IBlog;
import org.dreamexposure.startapped.objects.blog.PersonalBlog;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.dreamexposure.startapped.utils.MathUtils;
import org.dreamexposure.startapped.utils.SettingsManager;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class BlogEditActivity extends AppCompatActivity implements TaskCallback {
    private final int BACKGROUND_CODE = 1122;
    private final int ICON_CODE = 2211;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    //Get all the android views
    @BindView(R.id.blog_background_image)
    GifImageView background;
    @BindView(R.id.blog_icon_image)
    GifImageView icon;
    @BindView(R.id.blog_url)
    TextView url;
    @BindView(R.id.blog_title)
    TextView title;
    @BindView(R.id.blog_description)
    TextView desc;
    @BindView(R.id.nsfw_badge)
    TextView nsfwBadge;
    @BindView(R.id.adult_only_badge)
    TextView adultOnlyBadge;
    @BindView(R.id.age_badge)
    TextView ageBadge;
    @BindView(R.id.nsfw_check_box)
    CheckBox nsfwCheck;
    @BindView(R.id.allow_minors_check_box)
    CheckBox allowMinorsCheck;
    @BindView(R.id.display_age_checkbox)
    CheckBox showAgeCheck;
    @BindView(R.id.change_background_button)
    Button changeBackgroundButton;
    @BindView(R.id.change_icon_button)
    Button changeIconButton;

    PersonalBlog pBlogFromResponse;
    GroupBlog gBlogFromResponse;

    String backgroundImagePath = "";
    String iconImagePath = "";

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_edit);
        ButterKnife.bind(this);

        toolbar.setTitle("Editing Blog");
        setSupportActionBar(toolbar);

        //Get the bundle
        Bundle b = getIntent().getExtras();
        UUID blogId = UUID.fromString(b.getString("blog"));

        //Get the very latest from backend.
        new GetBlogViewTask(this, blogId).execute();
    }

    @SuppressLint("SetTextI18n")
    public void callbackOnBlogGet(NetworkCallStatus status) {
        try {
            if (status.isSuccess()) {
                IBlog iBlog = new Blog().fromJson(status.getBody().getJSONObject("blog"));

                //Setup actual data
                title.setText(iBlog.getName());
                desc.setText(iBlog.getDescription());
                url.setText(iBlog.getBaseUrl());
                if (iBlog.isNsfw())
                    nsfwBadge.setVisibility(View.VISIBLE);
                else
                    nsfwBadge.setVisibility(View.INVISIBLE);
                if (iBlog.isAllowUnder18())
                    adultOnlyBadge.setVisibility(View.INVISIBLE);
                else
                    adultOnlyBadge.setVisibility(View.VISIBLE);

                nsfwCheck.setChecked(iBlog.isNsfw());
                allowMinorsCheck.setChecked(iBlog.isAllowUnder18());

                if (iBlog.getType() == BlogType.PERSONAL) {
                    PersonalBlog blog = new PersonalBlog().fromJson(status.getBody().getJSONObject("blog"));
                    pBlogFromResponse = blog;

                    //Determine show age....
                    if (blog.isDisplayAge()) {
                        ageBadge.setText(MathUtils.determineAge(SettingsManager.getManager().getSettings().getBirthday()) + "");
                        ageBadge.setVisibility(View.VISIBLE);
                    } else {
                        ageBadge.setText(MathUtils.determineAge(SettingsManager.getManager().getSettings().getBirthday()) + "");
                        ageBadge.setVisibility(View.GONE);
                    }
                    showAgeCheck.setChecked(blog.isDisplayAge());
                } else {
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    GroupBlog blog = new GroupBlog().fromJson(status.getBody().getJSONObject("blog"));
                    gBlogFromResponse = blog;

                    ageBadge.setVisibility(View.GONE);
                    showAgeCheck.setVisibility(View.GONE);
                }

                //Download images
                new DownloadImageTask(background).execute(iBlog.getBackgroundImage().getUrl());
                new DownloadImageTask(icon).execute(iBlog.getIconImage().getUrl());
            } else {
                Toast.makeText(this, status.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException ignore) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_edit_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm_edit:
                //Save changes and go back to self blog list
                if (pBlogFromResponse != null) {
                    pBlogFromResponse.setName(title.getText().toString().trim());
                    pBlogFromResponse.setDescription(desc.getText().toString().trim());
                    pBlogFromResponse.setNsfw(nsfwCheck.isChecked());
                    pBlogFromResponse.setAllowUnder18(allowMinorsCheck.isChecked());
                    pBlogFromResponse.setDisplayAge(showAgeCheck.isChecked());

                    //TODO: Handle blog background color

                    new UpdateBlogTask(this, iconImagePath, backgroundImagePath).execute(pBlogFromResponse);
                } else {
                    gBlogFromResponse.setName(title.getText().toString().trim());
                    gBlogFromResponse.setDescription(desc.getText().toString().trim());
                    gBlogFromResponse.setNsfw(nsfwCheck.isChecked());
                    gBlogFromResponse.setAllowUnder18(allowMinorsCheck.isChecked());

                    //TODO: Handle blog background color

                    new UpdateBlogTask(this, iconImagePath, backgroundImagePath).execute(gBlogFromResponse);
                }
                return true;
            case R.id.action_cancel:
                //Cancel editing, go past Go, do not collect $200. Go to self blog list.
                finish();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @OnCheckedChanged(R.id.nsfw_check_box)
    public void onNsfwChange() {
        if (nsfwCheck.isPressed()) {
            if (nsfwCheck.isChecked())
                nsfwBadge.setVisibility(View.VISIBLE);
            else
                nsfwBadge.setVisibility(View.INVISIBLE);
        }
    }

    @OnCheckedChanged(R.id.allow_minors_check_box)
    public void onAllowMinorsChange() {
        if (allowMinorsCheck.isPressed()) {
            if (allowMinorsCheck.isChecked())
                adultOnlyBadge.setVisibility(View.INVISIBLE);
            else
                adultOnlyBadge.setVisibility(View.VISIBLE);
        }
    }

    @OnCheckedChanged(R.id.display_age_checkbox)
    public void onDisplayAgeChange() {
        if (showAgeCheck.isPressed()) {
            if (showAgeCheck.isChecked())
                ageBadge.setVisibility(View.VISIBLE);
            else
                ageBadge.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.change_background_button)
    public void handleBackgroundImageChange() {
        //Launch file picker
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setActivityTheme(R.style.LibAppTheme)
                .enableImagePicker(true)
                .enableCameraSupport(true)
                .enableVideoPicker(false)
                .enableDocSupport(false)
                .showGifs(true)
                .setActivityTitle(getResources().getString(R.string.pick_background_image))
                .pickPhoto(this, BACKGROUND_CODE);
    }

    @OnClick(R.id.change_icon_button)
    public void handleIconImageChange() {
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setActivityTheme(R.style.LibAppTheme)
                .enableImagePicker(true)
                .enableCameraSupport(true)
                .enableVideoPicker(false)
                .enableDocSupport(false)
                .showGifs(true)
                .setActivityTitle(getResources().getString(R.string.pick_icon_image))
                .pickPhoto(this, ICON_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BACKGROUND_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    //Conversion to base64 will be handled once the user confirms edits.
                    ArrayList<String> photoPaths = new ArrayList<>(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    backgroundImagePath = photoPaths.get(0);

                    //Check file size...
                    long fileSizeInMb = new File(backgroundImagePath).length() / (1024 * 1024);
                    if (fileSizeInMb > 10) {
                        Toast.makeText(this, R.string.error_over_10mb, Toast.LENGTH_LONG).show();
                        backgroundImagePath = null;
                    }

                    //Display image
                    new LoadImageFromFileTask(background).execute(backgroundImagePath);
                }
                break;
            case ICON_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    //Conversion to base64 will be handled once the user confirms edits.
                    ArrayList<String> photoPaths = new ArrayList<>(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    iconImagePath = photoPaths.get(0);

                    //Check file size...
                    long fileSizeInMb = new File(iconImagePath).length() / (1024 * 1024);
                    if (fileSizeInMb > 10) {
                        Toast.makeText(this, R.string.error_over_10mb, Toast.LENGTH_LONG).show();
                        iconImagePath = null;
                    }

                    //Display image
                    new LoadImageFromFileTask(icon).execute(iconImagePath);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void taskCallback(NetworkCallStatus status) {
        if (status.getType() == TaskType.BLOG_GET_VIEW) {
            callbackOnBlogGet(status);
        } else if (status.getType() == TaskType.BLOG_UPDATE_SELF) {
            if (status.isSuccess()) {
                finish();
            } else {
                Toast.makeText(this, status.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
