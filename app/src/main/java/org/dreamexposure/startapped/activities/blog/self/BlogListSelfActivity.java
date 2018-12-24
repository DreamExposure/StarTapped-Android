package org.dreamexposure.startapped.activities.blog.self;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.felipecsl.gifimageview.library.GifImageView;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.activities.blog.ViewBlogActivity;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.enums.blog.BlogType;
import org.dreamexposure.startapped.network.blog.self.GetBlogsSelfTask;
import org.dreamexposure.startapped.network.download.DownloadImageTask;
import org.dreamexposure.startapped.objects.blog.Blog;
import org.dreamexposure.startapped.objects.blog.IBlog;
import org.dreamexposure.startapped.objects.blog.PersonalBlog;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.dreamexposure.startapped.utils.MathUtils;
import org.dreamexposure.startapped.utils.SettingsManager;
import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlogListSelfActivity extends AppCompatActivity implements TaskCallback {
    @BindView(R.id.self_blog_linear)
    LinearLayout rootLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_list_self);
        ButterKnife.bind(this);

        toolbar.setTitle("Your Blogs");
        setSupportActionBar(toolbar);

        //Load blogs...
        new GetBlogsSelfTask(this).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Load blogs...
        new GetBlogsSelfTask(this).execute();
    }

    @SuppressLint("SetTextI18n")
    public void callbackOnBlogGet(NetworkCallStatus status) {
        try {
            if (status.isSuccess()) {
                //Remove old stuffs
                rootLayout.removeAllViews();

                JSONArray jBlogs = status.getBody().getJSONArray("blogs");

                for (int i = 0; i < status.getBody().getInt("count"); i++) {
                    IBlog blog = new Blog().fromJson(jBlogs.getJSONObject(i));
                    //Get all the android views
                    View view = LayoutInflater.from(this).inflate(R.layout.self_blog_container, null);
                    view.setBackgroundColor(Color.parseColor(blog.getBackgroundColor()));
                    GifImageView background = view.findViewById(R.id.blog_background_image);
                    GifImageView icon = view.findViewById(R.id.blog_icon_image);
                    TextView url = view.findViewById(R.id.blog_url);
                    TextView title = view.findViewById(R.id.blog_title);
                    TextView desc = view.findViewById(R.id.blog_description);
                    TextView nsfwBadge = view.findViewById(R.id.nsfw_badge);
                    TextView adultOnlyBadge = view.findViewById(R.id.adult_only_badge);
                    TextView ageBadge = view.findViewById(R.id.age_badge);
                    Button editBlogButton = view.findViewById(R.id.edit_blog_button);

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

                    if (blog.getType() == BlogType.PERSONAL) {
                        PersonalBlog pBlog = new PersonalBlog().fromJson(jBlogs.getJSONObject(i));
                        if (pBlog.isDisplayAge()) {
                            ageBadge.setText(MathUtils.determineAge(SettingsManager.getManager().getSettings().getBirthday()) + "");
                            ageBadge.setVisibility(View.VISIBLE);
                        } else {
                            ageBadge.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        //Hide age badge
                        ageBadge.setVisibility(View.INVISIBLE);
                    }

                    //Edit Blog button click handler
                    editBlogButton.setOnClickListener(v -> {
                        Intent intent = new Intent(this, BlogEditActivity.class);
                        Bundle b = new Bundle();
                        b.putString("blog", blog.getBlogId().toString());
                        intent.putExtras(b);
                        startActivity(intent);
                    });

                    //Blog url click handler
                    url.setOnClickListener(v -> {
                        Intent intent = new Intent(this, ViewBlogActivity.class);
                        Bundle b = new Bundle();
                        b.putString("blog", blog.getBlogId().toString());
                        intent.putExtras(b);
                        startActivity(intent);
                    });

                    //Download images
                    new DownloadImageTask(background).execute(blog.getBackgroundUrl());
                    new DownloadImageTask(icon).execute(blog.getIconUrl());

                    rootLayout.addView(view);
                }
            } else {
                Toast.makeText(this, status.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException ignore) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_list_self_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_blog:
                Intent intent = new Intent(this, BlogCreateActivity.class);
                startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void taskCallback(NetworkCallStatus status) {
        if (status.getType() == TaskType.BLOG_GET_SELF_ALL) {
            callbackOnBlogGet(status);
        }
    }
}
