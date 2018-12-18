package org.dreamexposure.startapped.activities.blog.self;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.felipecsl.gifimageview.library.GifImageView;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.enums.blog.BlogType;
import org.dreamexposure.startapped.network.blog.self.GetBlogsSelfTask;
import org.dreamexposure.startapped.network.download.DownloadImageTask;
import org.dreamexposure.startapped.objects.blog.Blog;
import org.dreamexposure.startapped.objects.blog.GroupBlog;
import org.dreamexposure.startapped.objects.blog.IBlog;
import org.dreamexposure.startapped.objects.blog.PersonalBlog;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlogListSelfActivity extends AppCompatActivity {
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
        new GetBlogsSelfTask().execute(this, this);
    }

    public void callbackOnBlogGet(NetworkCallStatus status) {
        try {
            if (status.isSuccess()) {
                JSONArray jBlogs = status.getBody().getJSONArray("blogs");

                for (int i = 0; i < status.getBody().getInt("count"); i++) {
                    IBlog iBlog = new Blog().fromJson(jBlogs.getJSONObject(i));
                    if (iBlog.getType() == BlogType.PERSONAL) {
                        PersonalBlog blog = new PersonalBlog().fromJson(jBlogs.getJSONObject(i));

                        //Get all the android views and stuffs
                        View view = LayoutInflater.from(this).inflate(R.layout.self_blog_container, null);
                        GifImageView background = view.findViewById(R.id.blog_background_image);
                        GifImageView icon = view.findViewById(R.id.blog_icon_image);
                        TextView url = view.findViewById(R.id.blog_url);
                        TextView title = view.findViewById(R.id.blog_title);
                        TextView desc = view.findViewById(R.id.blog_description);
                        //TODO: Edit button

                        title.setText(blog.getName());
                        desc.setText(blog.getDescription());
                        url.setText(blog.getBaseUrl());
                        //TODO: Add on click listener for url text
                        new DownloadImageTask(background).execute(blog.getBackgroundUrl());
                        new DownloadImageTask(icon).execute(blog.getIconUrl());


                        rootLayout.addView(view);
                    } else {
                        GroupBlog blog = new GroupBlog().fromJson(jBlogs.getJSONObject(i));

                        //Get all the android views and stuffs
                        View view = LayoutInflater.from(this).inflate(R.layout.self_blog_container, null);
                        GifImageView background = view.findViewById(R.id.blog_background_image);
                        GifImageView icon = view.findViewById(R.id.blog_icon_image);
                        TextView url = view.findViewById(R.id.blog_url);
                        TextView title = view.findViewById(R.id.blog_title);
                        TextView desc = view.findViewById(R.id.blog_description);
                        //TODO: Edit Button

                        title.setText(blog.getName());
                        desc.setText(blog.getDescription());
                        url.setText(blog.getBaseUrl());
                        //TODO: Add on click listener for url text
                        new DownloadImageTask(background).execute(blog.getBackgroundUrl());
                        new DownloadImageTask(icon).execute(blog.getIconUrl());
                    }
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
                //TODO: Send to create Blog activity
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
