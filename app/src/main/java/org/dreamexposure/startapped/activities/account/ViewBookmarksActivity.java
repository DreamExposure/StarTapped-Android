package org.dreamexposure.startapped.activities.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.activities.SearchActivity;
import org.dreamexposure.startapped.activities.blog.self.BlogListSelfActivity;
import org.dreamexposure.startapped.activities.settings.SettingsActivity;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.network.post.GetBookmarkedPostsTask;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.dreamexposure.startapped.objects.post.AudioPost;
import org.dreamexposure.startapped.objects.post.IPost;
import org.dreamexposure.startapped.objects.post.ImagePost;
import org.dreamexposure.startapped.objects.post.TextPost;
import org.dreamexposure.startapped.objects.post.VideoPost;
import org.dreamexposure.startapped.objects.time.TimeIndex;
import org.dreamexposure.startapped.utils.PostUtils;
import org.dreamexposure.startapped.utils.PostViewUtils;
import org.dreamexposure.startapped.utils.ViewUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewBookmarksActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TaskCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.linear_content)
    LinearLayout rootLayout;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.scroll_content)
    ScrollView scrollView;

    private TimeIndex index;
    private boolean isGenerating = false;
    private boolean isRefreshing = false;
    private boolean stopRequesting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookmarks);
        ButterKnife.bind(this);

        //Handle toolbar
        toolbar.setTitle(R.string.menu_bookmarks);

        //Drawer setup...
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_bookmarks);

        //Scroll view setup
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(this::scrollChangeHandler);

        index = new TimeIndex();

        getPosts();
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
            startActivity(new Intent(this, ViewFollowingActivity.class));
            finish();
        } else if (id == R.id.nav_bookmarks) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public void getPostsCallback(NetworkCallStatus status) {
        try {
            if (status.isSuccess()) {
                JSONArray jPosts = status.getBody().getJSONArray("posts");
                List<IPost> posts = PostUtils.getPostsFromArray(jPosts);
                Collections.sort(posts);

                if (posts.isEmpty()) {
                    stopRequesting = true;
                    Toast.makeText(this, R.string.no_more_posts, Toast.LENGTH_LONG).show();
                }

                if (status.getBody().getJSONObject("range").getLong("latest") > 0) {
                    index.setLatest(status.getBody().getJSONObject("range").getLong("latest"));
                    index.setOldest(status.getBody().getJSONObject("range").getLong("oldest"));
                }

                if (isRefreshing) {
                    rootLayout.removeAllViews();
                }

                for (IPost p : posts) {
                    //Skip posts not in range. Probably a parent post which will be handled correctly.
                    if (p.getTimestamp() >= index.getOldest() && p.getTimestamp() <= index.getLatest() && p.isBookmarked()) {
                        if (p.getParent() != null) {
                            View view = PostViewUtils.generatePostViewFromTree(p, posts, this);

                            rootLayout.addView(view);
                            rootLayout.addView(ViewUtils.smallSpace(this));
                        } else {
                            if (p instanceof TextPost) {
                                //Handle single post (no parent)
                                View view = PostViewUtils.generateTextPostView((TextPost) p, null, true, true, true, this);
                                rootLayout.addView(view);
                                rootLayout.addView(ViewUtils.smallSpace(this));
                            } else if (p instanceof ImagePost) {
                                //Handle single post (no parent)
                                View view = PostViewUtils.generateImagePostView((ImagePost) p, null, true, true, true, this);
                                rootLayout.addView(view);
                                rootLayout.addView(ViewUtils.smallSpace(this));
                            } else if (p instanceof AudioPost) {
                                //Handle single post (no parent)
                                View view = PostViewUtils.generateAudioPostView((AudioPost) p, null, true, true, true, this);
                                rootLayout.addView(view);
                                rootLayout.addView(ViewUtils.smallSpace(this));
                            } else if (p instanceof VideoPost) {
                                //Handle single post (no parent)
                                View view = PostViewUtils.generateVideoPostView((VideoPost) p, null, true, true, true, this);
                                rootLayout.addView(view);
                                rootLayout.addView(ViewUtils.smallSpace(this));
                            }
                        }
                    }
                }

            } else {
                //Hit the epoch.
                if (status.getCode() == 417) {
                    Toast.makeText(this, R.string.no_more_posts, Toast.LENGTH_LONG).show();
                    stopRequesting = true;
                } else {
                    Toast.makeText(this, status.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException ignore) {
            Toast.makeText(this, R.string.error_bad_return, Toast.LENGTH_LONG).show();
        }

        isGenerating = false;

        if (isRefreshing) {
            isRefreshing = false;
            swipeRefreshLayout.setRefreshing(false);
        }

        index.setBefore(index.getOldest() - 1);
    }

    public void getPosts() {
        if (!isGenerating && !stopRequesting) {
            isGenerating = true;
            GetBookmarkedPostsTask task = new GetBookmarkedPostsTask(this, index);
            task.execute();
        }
    }

    void onRefresh() {
        if (!isRefreshing && !isGenerating) {
            isRefreshing = true;
            stopRequesting = false;
            index = new TimeIndex();
            getPosts();
        }
    }

    void scrollChangeHandler() {
        if (scrollView.getChildAt(0).getBottom() <= (scrollView.getHeight() + scrollView.getScrollY())) {
            //At bottom...
            getPosts();
        }
    }

    @Override
    public void taskCallback(NetworkCallStatus status) {
        //Unsupported callback...
        if (status.getType() == TaskType.POST_GET_BOOKMARKED) {
            getPostsCallback(status);
        }
    }
}
