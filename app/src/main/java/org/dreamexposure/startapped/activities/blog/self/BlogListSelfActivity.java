package org.dreamexposure.startapped.activities.blog.self;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.activities.SearchActivity;
import org.dreamexposure.startapped.activities.account.ViewBookmarksActivity;
import org.dreamexposure.startapped.activities.account.ViewFollowingActivity;
import org.dreamexposure.startapped.activities.blog.ViewBlogActivity;
import org.dreamexposure.startapped.activities.settings.SettingsActivity;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.enums.blog.BlogType;
import org.dreamexposure.startapped.network.blog.self.GetBlogsSelfTask;
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

public class BlogListSelfActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TaskCallback {
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

        //Drawer setup...
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_blogs);

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
                    ImageView background = view.findViewById(R.id.blog_background_image);
                    ImageView icon = view.findViewById(R.id.blog_icon_image);
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
                    Glide.with(view).load(blog.getBackgroundImage().getUrl()).into(background);
                    Glide.with(view).load(blog.getIconImage().getUrl()).into(icon);

                    rootLayout.addView(view);
                }
            } else {
                Toast.makeText(this, status.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException ignore) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Load blogs...
        new GetBlogsSelfTask(this).execute();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_list_self_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create_blog) {
            Intent intent = new Intent(this, BlogCreateActivity.class);
            startActivity(intent);
            return true;
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (id == R.id.nav_hub) {
            finish();
            return true;
        } else if (id == R.id.nav_search) {
            startActivity(new Intent(this, SearchActivity.class));
            finish();
        } else if (id == R.id.nav_explore) {
            //TODO: Handle going to explore
        } else if (id == R.id.nav_blogs) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_following) {
            startActivity(new Intent(this, ViewFollowingActivity.class));
            finish();
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
        if (status.getType() == TaskType.BLOG_GET_SELF_ALL) {
            callbackOnBlogGet(status);
        }
    }
}
