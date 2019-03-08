package org.dreamexposure.startapped.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.activities.account.ViewBookmarksActivity;
import org.dreamexposure.startapped.activities.account.ViewFollowingActivity;
import org.dreamexposure.startapped.activities.blog.self.BlogListSelfActivity;
import org.dreamexposure.startapped.activities.settings.SettingsActivity;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.network.post.GetPostsForSearchTask;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TaskCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.linear_content)
    LinearLayout rootLayout;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.scroll_content)
    ScrollView scrollView;

    public TimeIndex index;
    private boolean isGenerating = false;
    private boolean isRefreshing = false;
    public boolean stopRequesting = false;
    public boolean clear = false;

    //Search stuffs
    public List<String> currentTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        //Toolbar setup
        toolbar.setTitle("Search");
        setSupportActionBar(toolbar);

        //Drawer setup...
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_search);

        //Scroll view setup
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(this::scrollChangeHandler);

        index = new TimeIndex();

        boolean search = false;
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("tags")) {
                search = true;
                currentTags.clear();
                currentTags.addAll(b.getStringArrayList("tags"));
            }
        }

        if (search)
            getPosts();

        handleSearchIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleSearchIntent(intent);
    }

    private void handleSearchIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            //Split and add to current tags....
            currentTags.clear();

            for (String tagRaw : query.split(",")) {
                if (tagRaw.trim().length() > 0)
                    currentTags.add(tagRaw.trim());
            }

            if (currentTags.size() > 0) {
                stopRequesting = false;
                clear = true;
                index = new TimeIndex();

                getPosts();
            }
        }
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

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_toolbar, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
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
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_explore) {
            //TODO: Handle going to explore
        } else if (id == R.id.nav_blogs) {
            startActivity(new Intent(this, BlogListSelfActivity.class));
            finish();
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

                JSONObject range = status.getBody().getJSONObject("range");
                if (range.getLong("latest") > 0) {
                    index.setLatest(range.getLong("latest"));
                    index.setOldest(range.getLong("oldest"));
                }

                if (isRefreshing || clear)
                    rootLayout.removeAllViews();

                for (IPost p : posts) {
                    //Skip posts not in range. Probably a parent post which will be handled correctly.
                    if (p.getTimestamp() >= index.getOldest() && p.getTimestamp() <= index.getLatest()) {
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
        } catch (JSONException e) {
            Log.e(StarTappedApp.TAG, "Failed to handle JSON", e);
            Toast.makeText(this, R.string.error_bad_return, Toast.LENGTH_LONG).show();
        }
        isGenerating = false;
        clear = false;

        if (isRefreshing) {
            isRefreshing = false;
            swipeRefreshLayout.setRefreshing(false);
        }

        index.setBefore(index.getOldest() - 1);
    }

    public void getPosts() {
        if (!isGenerating && !stopRequesting) {
            //Validate once more that there is something to search for...
            if (currentTags.size() > 0) {
                isGenerating = true;
                GetPostsForSearchTask task = new GetPostsForSearchTask(this, index, currentTags);
                task.execute();
            } else {
                Toast.makeText(this, R.string.search_nothing, Toast.LENGTH_LONG).show();
            }
        }
    }

    void onRefresh() {
        if (!isRefreshing && !isGenerating) {
            //Don't refresh if there is nothing to search for...
            if (currentTags.size() > 0) {
                isRefreshing = true;
                stopRequesting = false;

                index = new TimeIndex();

                getPosts();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this, R.string.search_nothing, Toast.LENGTH_LONG).show();
            }
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
        if (status.getType() == TaskType.POST_GET_FOR_SEARCH) getPostsCallback(status);
    }
}
