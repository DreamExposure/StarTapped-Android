package org.dreamexposure.startapped.activities.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.felipecsl.gifimageview.library.GifImageView;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.activities.SearchActivity;
import org.dreamexposure.startapped.activities.blog.ViewBlogActivity;
import org.dreamexposure.startapped.activities.blog.self.BlogListSelfActivity;
import org.dreamexposure.startapped.activities.settings.SettingsActivity;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.network.download.DownloadImageTask;
import org.dreamexposure.startapped.network.relation.GetFollowingBlogsTask;
import org.dreamexposure.startapped.objects.blog.Blog;
import org.dreamexposure.startapped.objects.blog.IBlog;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewFollowingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TaskCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.following_linear_container)
    LinearLayout linearContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_following);
        ButterKnife.bind(this);

        //Handle toolbar
        toolbar.setTitle(R.string.following_title);

        //Drawer setup...
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        //Everything else
        new GetFollowingBlogsTask(this).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new GetFollowingBlogsTask(this).execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (id == R.id.nav_hub) {
            finish();
        } else if (id == R.id.nav_search) {
            startActivity(new Intent(this, SearchActivity.class));
            finish();
        } else if (id == R.id.nav_explore) {
            //TODO: Handle going to explore
        } else if (id == R.id.nav_blogs) {
            startActivity(new Intent(this, BlogListSelfActivity.class));
            finish();
        } else if (id == R.id.nav_following) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_bookmarks) {
            startActivity(new Intent(this, ViewBookmarksActivity.class));
            finish();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void taskCallback(NetworkCallStatus status) {
        if (status.getType() == TaskType.FOLLOW_GET_FOLLOWING) {
            if (status.isSuccess()) {
                linearContainer.removeAllViews();
                try {
                    JSONArray jBlogs = status.getBody().getJSONArray("blogs");
                    for (int i = 0; i < jBlogs.length(); i++) {
                        IBlog blog = new Blog().fromJson(jBlogs.getJSONObject(i));

                        View view = LayoutInflater.from(this).inflate(R.layout.following_view_container, null);
                        LinearLayout contentContainer = view.findViewById(R.id.content_container);
                        GifImageView icon = view.findViewById(R.id.blog_icon_image);
                        TextView url = view.findViewById(R.id.blog_url);

                        contentContainer.setOnClickListener(v -> {
                            Intent intent = new Intent(this, ViewBlogActivity.class);
                            Bundle b = new Bundle();
                            b.putString("blog", blog.getBlogId().toString());
                            intent.putExtras(b);
                            startActivity(intent);
                        });

                        url.setText(blog.getBaseUrl());

                        new DownloadImageTask(icon).execute(blog.getIconImage().getUrl());

                        linearContainer.addView(view);
                    }
                } catch (JSONException ignore) {
                }
            } else {
                Toast.makeText(this, "Failed to get following", Toast.LENGTH_LONG).show();
            }
        }
    }
}
