package org.dreamexposure.startapped.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.activities.account.ViewFollowingActivity;
import org.dreamexposure.startapped.activities.blog.self.BlogListSelfActivity;
import org.dreamexposure.startapped.activities.settings.SettingsActivity;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.dialogs.post.CreatePostDialogFragment;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.network.post.GetPostsForHubTask;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.dreamexposure.startapped.objects.post.AudioPost;
import org.dreamexposure.startapped.objects.post.IPost;
import org.dreamexposure.startapped.objects.post.ImagePost;
import org.dreamexposure.startapped.objects.post.TextPost;
import org.dreamexposure.startapped.objects.post.VideoPost;
import org.dreamexposure.startapped.objects.time.TimeIndex;
import org.dreamexposure.startapped.utils.PostUtils;
import org.dreamexposure.startapped.utils.PostViewUtils;
import org.dreamexposure.startapped.utils.RequestPermissionHandler;
import org.dreamexposure.startapped.utils.ViewUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HubActivity extends AppCompatActivity implements TaskCallback {
    //TODO: Handle getting more posts when at bottom
    //TODO: Display loading icon when getting posts (when at bottom, refresh already has one)

    private RequestPermissionHandler mRequestPermissionHandler;
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.linear_content)
    LinearLayout rootLayout;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.scroll_content)
    ScrollView scrollView;

    @BindView(R.id.action_create_post)
    FloatingActionButton createPostFab;

    private TimeIndex index;
    private boolean isGenerating = false;
    private boolean isRefreshing = false;
    private boolean scrollUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);
        mRequestPermissionHandler = new RequestPermissionHandler();

        ButterKnife.bind(this);

        toolbar.setTitle("Hub");
        setSupportActionBar(toolbar);

        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);

        //Check permissions on start
        doPermissionsCheck();

        index = new TimeIndex();

        scrollUp = true;

        getPosts();
    }

    @OnClick(R.id.action_create_post)
    void onCreatePostFabClick() {
        CreatePostDialogFragment dialog = new CreatePostDialogFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dialog.show(ft, StarTappedApp.TAG);
    }

    private void doPermissionsCheck() {
        mRequestPermissionHandler.requestPermission(this, permissions, 123, new RequestPermissionHandler.RequestPermissionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailed() {
                Toast.makeText(HubActivity.this, "StarTapped may not function as intended without all permissions accepted!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_blogs:
                startActivity(new Intent(this, BlogListSelfActivity.class));
                return true;
            case R.id.action_following:
                startActivity(new Intent(this, ViewFollowingActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void getPostsCallback(NetworkCallStatus status) {
        try {
            if (status.isSuccess()) {
                JSONArray jPosts = status.getBody().getJSONArray("posts");
                List<IPost> posts = PostUtils.getPostsFromArray(jPosts);
                Collections.sort(posts);

                if (isRefreshing)
                    rootLayout.removeAllViews();

                for (IPost p : posts) {
                    //Skip posts not in range. Probably a parent post which will be handled correctly.
                    if (p.getTimestamp() > index.getStart().getMillis() && p.getTimestamp() < index.getStop().getMillis()) {
                        if (p.getParent() != null) {
                            View view = PostViewUtils.generatePostViewFromTree(p, posts, this);

                            rootLayout.addView(view);
                            rootLayout.addView(ViewUtils.smallSpace(this));
                        } else {
                            if (p instanceof TextPost) {
                                //Handle single post (no parent)
                                View view = PostViewUtils.generateTextPostView((TextPost)p, null, true, true, this);
                                rootLayout.addView(view);
                                rootLayout.addView(ViewUtils.smallSpace(this));
                            } else if (p instanceof ImagePost) {
                                //Handle single post (no parent)
                                View view = PostViewUtils.generateImagePostView((ImagePost)p, null, true, true, this);
                                rootLayout.addView(view);
                                rootLayout.addView(ViewUtils.smallSpace(this));
                            } else if (p instanceof AudioPost) {
                                //Handle single post (no parent)
                                View view = PostViewUtils.generateAudioPostView((AudioPost)p, null, true, true, this);
                                rootLayout.addView(view);
                                rootLayout.addView(ViewUtils.smallSpace(this));
                            } else if (p instanceof VideoPost) {
                                //Handle single post (no parent)
                                View view = PostViewUtils.generateVideoPostView((VideoPost)p, null, true, true, this);
                                rootLayout.addView(view);
                                rootLayout.addView(ViewUtils.smallSpace(this));
                            }
                        }
                    }
                }

                if (scrollUp)
                    scrollView.post(() -> scrollView.smoothScrollTo(0, 0));

                if (!isRefreshing)
                    index.backwardOneMonth();

            } else {
                Toast.makeText(this, status.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException ignore) {
            Toast.makeText(this, R.string.error_bad_return, Toast.LENGTH_LONG).show();
        }
        isGenerating = false;
        scrollUp = false;

        if (isRefreshing) {
            isRefreshing = false;
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void getPosts() {
        if (!isGenerating) {
            isGenerating = true;
            GetPostsForHubTask task = new GetPostsForHubTask(this, index);
            task.execute();
        }
    }

    void onRefresh() {
        if (!isRefreshing && !isGenerating) {
            isRefreshing = true;

            index = new TimeIndex();

            scrollUp = true;

            getPosts();
        }
    }

    @Override
    public void taskCallback(NetworkCallStatus status) {
        if (status.getType() == TaskType.POST_GET_FOR_HUB) getPostsCallback(status);
    }
}
