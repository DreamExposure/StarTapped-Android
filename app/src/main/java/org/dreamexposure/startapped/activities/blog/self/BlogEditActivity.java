package org.dreamexposure.startapped.activities.blog.self;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.felipecsl.gifimageview.library.GifImageView;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.enums.blog.BlogType;
import org.dreamexposure.startapped.network.blog.self.GetBlogSelfForEditTask;
import org.dreamexposure.startapped.network.blog.self.UpdateBlogTask;
import org.dreamexposure.startapped.network.download.DownloadImageTask;
import org.dreamexposure.startapped.objects.blog.Blog;
import org.dreamexposure.startapped.objects.blog.GroupBlog;
import org.dreamexposure.startapped.objects.blog.IBlog;
import org.dreamexposure.startapped.objects.blog.PersonalBlog;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.json.JSONException;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlogEditActivity extends AppCompatActivity {

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

    IBlog blogFromResponse;

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
        new GetBlogSelfForEditTask().execute(this, this, blogId);
    }

    public void callbackOnBlogGet(NetworkCallStatus status) {
        try {
            if (status.isSuccess()) {
                IBlog iBlog = new Blog().fromJson(status.getBody().getJSONObject("blog"));
                if (iBlog.getType() == BlogType.PERSONAL) {
                    PersonalBlog blog = new PersonalBlog().fromJson(status.getBody().getJSONObject("blog"));
                    blogFromResponse = blog;

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
                } else {
                    GroupBlog blog = new GroupBlog().fromJson(status.getBody().getJSONObject("blog"));
                    blogFromResponse = blog;

                    //Get all the android views
                    GifImageView background = findViewById(R.id.blog_background_image);
                    GifImageView icon = findViewById(R.id.blog_icon_image);
                    TextView url = findViewById(R.id.blog_url);
                    TextView title = findViewById(R.id.blog_title);
                    TextView desc = findViewById(R.id.blog_description);
                    TextView nsfwBadge = findViewById(R.id.nsfw_badge);
                    TextView adultOnlyBadge = findViewById(R.id.adult_only_badge);

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
                }
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
                blogFromResponse.setName(title.getText().toString().trim());
                blogFromResponse.setDescription(desc.getText().toString().trim());

                //TODO: Handle background and icon image
                //TODO: Handle nsfw and 18 only status and show age
                //TODO: Handle blog background color

                new UpdateBlogTask().execute(this, blogFromResponse);
                return true;
            case R.id.action_cancel:
                //Cancel editing, go past Go, do not collect $200. Go to self blog list.
                Intent intent = new Intent(this, BlogListSelfActivity.class);
                startActivity(intent);
                finish();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
