package org.dreamexposure.startapped.activities.blog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.felipecsl.gifimageview.library.GifImageView;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.enums.blog.BlogType;
import org.dreamexposure.startapped.network.account.blog.GetAccountForBlogViewTask;
import org.dreamexposure.startapped.network.blog.view.GetBlogViewTask;
import org.dreamexposure.startapped.network.download.DownloadImageTask;
import org.dreamexposure.startapped.objects.blog.Blog;
import org.dreamexposure.startapped.objects.blog.IBlog;
import org.dreamexposure.startapped.objects.blog.PersonalBlog;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.dreamexposure.startapped.utils.MathUtils;
import org.dreamexposure.startapped.utils.SettingsManager;
import org.json.JSONException;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewBlogActivity extends AppCompatActivity implements TaskCallback {
    //TODO: Handle getting more posts when "buffer" runs out.

    @BindView(R.id.blog_view_linear)
    LinearLayout rootLayout;
    @BindView(R.id.blog_view_relative)
    RelativeLayout parentLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    TextView ageBadge;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog);
        ButterKnife.bind(this);

        //Get the bundle
        Bundle b = getIntent().getExtras();
        UUID blogId = UUID.fromString(b.getString("blog"));

        //Get blog...
        new GetBlogViewTask(this, blogId).execute();
    }

    @SuppressLint("SetTextI18n")
    public void getBlogCallback(NetworkCallStatus status) {
        try {
            if (status.isSuccess()) {
                IBlog blog = new Blog().fromJson(status.getBody().getJSONObject("blog"));

                if (!blog.isAllowUnder18() && MathUtils.determineAge(SettingsManager.getManager().getSettings().getBirthday()) < 18) {
                    //Minor trying to view adult only blog, block action...
                    View view = LayoutInflater.from(this).inflate(R.layout.adult_only_block, null);

                    //Get all the android views
                    Button goBackButton = view.findViewById(R.id.go_back_button);

                    //Update toolbar
                    toolbar.setTitle(blog.getBaseUrl());

                    //Setup data
                    goBackButton.setOnClickListener(v -> finish());

                    rootLayout.addView(view);
                } else if (blog.isNsfw() && SettingsManager.getManager().getSettings().isSafeSearch()) {
                    //User is using safe search... hide content and prompt to settings
                    View view = LayoutInflater.from(this).inflate(R.layout.nsfw_blog_block, null);

                    //Get all the android views
                    Button goBackButton = view.findViewById(R.id.go_back_button);

                    //Update toolbar
                    toolbar.setTitle(blog.getBaseUrl());

                    //Setup data
                    goBackButton.setOnClickListener(v -> finish());

                    rootLayout.addView(view);
                } else {
                    //Get all the android views
                    View view = LayoutInflater.from(this).inflate(R.layout.view_blog_container, null);
                    view.setBackgroundColor(Color.parseColor(blog.getBackgroundColor()));
                    parentLayout.setBackgroundColor(Color.parseColor(blog.getBackgroundColor()));
                    GifImageView background = view.findViewById(R.id.blog_background_image);
                    GifImageView icon = view.findViewById(R.id.blog_icon_image);
                    TextView url = view.findViewById(R.id.blog_url);
                    TextView title = view.findViewById(R.id.blog_title);
                    TextView desc = view.findViewById(R.id.blog_description);
                    TextView nsfwBadge = view.findViewById(R.id.nsfw_badge);
                    TextView adultOnlyBadge = view.findViewById(R.id.adult_only_badge);
                    ageBadge = view.findViewById(R.id.age_badge);

                    //Update the toolbar with stuffs
                    toolbar.setTitle(blog.getBaseUrl());

                    //Setup actual data
                    title.setText(blog.getName());
                    desc.setText(blog.getDescription());
                    url.setText(blog.getBaseUrl());
                    if (blog.isNsfw())
                        nsfwBadge.setVisibility(View.VISIBLE);
                    else
                        nsfwBadge.setVisibility(View.INVISIBLE);
                    if (blog.isAllowUnder18())
                        adultOnlyBadge.setVisibility(View.INVISIBLE);
                    else
                        adultOnlyBadge.setVisibility(View.VISIBLE);

                    //Download images
                    new DownloadImageTask(background).execute(blog.getBackgroundUrl());
                    new DownloadImageTask(icon).execute(blog.getIconUrl());

                    if (blog.getType() == BlogType.PERSONAL) {
                        PersonalBlog pBlog = new PersonalBlog().fromJson(status.getBody().getJSONObject("blog"));
                        if (pBlog.isDisplayAge()) {
                            //Get blog owner's age...
                            new GetAccountForBlogViewTask(this, pBlog.getOwnerId()).execute();
                            ageBadge.setVisibility(View.VISIBLE);
                        } else {
                            ageBadge.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        //Hide age badge
                        ageBadge.setVisibility(View.INVISIBLE);
                    }

                    rootLayout.addView(view);
                }
            } else {
                Toast.makeText(this, status.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException ignore) {
        }

        //TODO: Get posts from blog.
    }

    @SuppressLint("SetTextI18n")
    public void getBlogOwnerAge(NetworkCallStatus status) {
        try {
            if (status.isSuccess()) {
                String birthday = status.getBody().getJSONObject("account").getString("birthday");
                int age = MathUtils.determineAge(birthday);
                ageBadge.setText(age + "");
            } else {
                ageBadge.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException ignore) {
            ageBadge.setVisibility(View.INVISIBLE);
        }
    }

    public void getPostsCallback(NetworkCallStatus status) {
        //TODO: Display posts
    }

    @Override
    public void taskCallback(NetworkCallStatus status) {
        if (status.getType() == TaskType.ACCOUNT_GET_BLOG) getBlogOwnerAge(status);
        else if (status.getType() == TaskType.BLOG_GET_VIEW) getBlogCallback(status);
    }
}
