package org.dreamexposure.startapped.activities.blog;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.enums.blog.BlogType;
import org.dreamexposure.startapped.network.account.blog.GetAccountForBlogViewTask;
import org.dreamexposure.startapped.network.blog.view.GetBlogViewTask;
import org.dreamexposure.startapped.network.post.GetPostsForBlogTask;
import org.dreamexposure.startapped.network.relation.FollowBlogTask;
import org.dreamexposure.startapped.network.relation.UnfollowBlogTask;
import org.dreamexposure.startapped.objects.blog.Blog;
import org.dreamexposure.startapped.objects.blog.GroupBlog;
import org.dreamexposure.startapped.objects.blog.IBlog;
import org.dreamexposure.startapped.objects.blog.PersonalBlog;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.dreamexposure.startapped.objects.post.AudioPost;
import org.dreamexposure.startapped.objects.post.IPost;
import org.dreamexposure.startapped.objects.post.ImagePost;
import org.dreamexposure.startapped.objects.post.TextPost;
import org.dreamexposure.startapped.objects.post.VideoPost;
import org.dreamexposure.startapped.objects.time.TimeIndex;
import org.dreamexposure.startapped.objects.user.Account;
import org.dreamexposure.startapped.utils.MathUtils;
import org.dreamexposure.startapped.utils.PostUtils;
import org.dreamexposure.startapped.utils.PostViewUtils;
import org.dreamexposure.startapped.utils.SettingsManager;
import org.dreamexposure.startapped.utils.ViewUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("InflateParams")
public class ViewBlogActivity extends AppCompatActivity implements TaskCallback {

    @BindView(R.id.blog_view_linear)
    LinearLayout rootLayout;
    @BindView(R.id.blog_view_relative)
    RelativeLayout parentLayout;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.blog_view_scroll)
    ScrollView scrollView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    TextView ageBadge;

    private UUID blogId;

    private IBlog theBlog;

    private TimeIndex index;
    private boolean isGenerating = false;
    private boolean isRefreshing = false;
    private boolean stopRequesting = false;

    private boolean blockOn = false;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog);
        ButterKnife.bind(this);

        //Get the bundle
        Bundle b = getIntent().getExtras();
        blogId = UUID.fromString(b.getString("blog"));

        //Load the toolbar
        setSupportActionBar(toolbar);

        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(this::scrollChangeHandler);

        index = new TimeIndex();

        //Get blog...
        new GetBlogViewTask(this, blogId).execute();
    }

    @SuppressLint("SetTextI18n")
    public void getBlogCallback(NetworkCallStatus status) {
        try {
            if (status.isSuccess()) {
                IBlog blog = new Blog().fromJson(status.getBody().getJSONObject("blog"));

                if (!blog.isAllowUnder18() && MathUtils.determineAge(SettingsManager.getManager().getSettings().getBirthday()) < 18) {
                    blockOn = true;
                    //Minor trying to view adult only blog, block action...
                    View view = LayoutInflater.from(this).inflate(R.layout.adult_only_block, null);

                    //Get all the android views
                    Button goBackButton = view.findViewById(R.id.go_back_button);

                    //Update toolbar
                    toolbar.setTitle(blog.getBaseUrl());
                    Menu menu = toolbar.getMenu();
                    menu.findItem(R.id.action_share).setVisible(false);
                    menu.findItem(R.id.action_followers).setVisible(false);
                    menu.findItem(R.id.action_follow).setVisible(false);
                    menu.findItem(R.id.action_unfollow).setVisible(false);
                    menu.findItem(R.id.action_report).setVisible(false);
                    menu.findItem(R.id.action_block).setVisible(false);
                    menu.findItem(R.id.action_unblock).setVisible(false);


                    //Setup data
                    goBackButton.setOnClickListener(v -> finish());

                    rootLayout.addView(view);
                } else if (blog.isNsfw() && SettingsManager.getManager().getSettings().isSafeSearch()) {
                    blockOn = true;
                    //User is using safe search... hide content and prompt to settings
                    View view = LayoutInflater.from(this).inflate(R.layout.nsfw_blog_block, null);

                    //Get all the android views
                    Button goBackButton = view.findViewById(R.id.go_back_button);

                    //Update toolbar
                    toolbar.setTitle(blog.getBaseUrl());
                    Menu menu = toolbar.getMenu();
                    menu.findItem(R.id.action_share).setVisible(false);
                    menu.findItem(R.id.action_followers).setVisible(false);
                    menu.findItem(R.id.action_follow).setVisible(false);
                    menu.findItem(R.id.action_unfollow).setVisible(false);
                    menu.findItem(R.id.action_report).setVisible(false);
                    menu.findItem(R.id.action_block).setVisible(false);
                    menu.findItem(R.id.action_unblock).setVisible(false);

                    //Setup data
                    goBackButton.setOnClickListener(v -> finish());

                    rootLayout.addView(view);
                } else {
                    //Get all the android views
                    View view = LayoutInflater.from(this).inflate(R.layout.view_blog_container, null);
                    view.setBackgroundColor(Color.parseColor(blog.getBackgroundColor()));
                    parentLayout.setBackgroundColor(Color.parseColor(blog.getBackgroundColor()));
                    ImageView background = view.findViewById(R.id.blog_background_image);
                    ImageView icon = view.findViewById(R.id.blog_icon_image);
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
                    Glide.with(view).load(blog.getBackgroundImage().getUrl()).into(background);
                    Glide.with(view).load(blog.getIconImage().getUrl()).into(icon);

                    if (blog.getType() == BlogType.PERSONAL) {
                        PersonalBlog pBlog = new PersonalBlog().fromJson(status.getBody().getJSONObject("blog"));
                        theBlog = pBlog;
                        if (pBlog.isDisplayAge()) {
                            //Get blog owner's age...
                            new GetAccountForBlogViewTask(this, pBlog.getOwnerId()).execute();
                            ageBadge.setVisibility(View.VISIBLE);
                        } else {
                            ageBadge.setVisibility(View.INVISIBLE);
                        }

                        //Check if user owns blog or not...
                        if (pBlog.getOwnerId().equals(SettingsManager.getManager().getSettings().getAccountId())) {
                            //Hide block/report/follow shit.
                            Menu menu = toolbar.getMenu();
                            menu.findItem(R.id.action_followers).setVisible(true);
                            menu.findItem(R.id.action_follow).setVisible(false);
                            menu.findItem(R.id.action_unfollow).setVisible(false);
                            menu.findItem(R.id.action_report).setVisible(false);
                            menu.findItem(R.id.action_block).setVisible(false);
                            menu.findItem(R.id.action_unblock).setVisible(false);
                        } else {
                            if (pBlog.getFollowers().contains(SettingsManager.getManager().getSettings().getAccountId())) {
                                //Following, hide following, block, and unblock buttons
                                Menu menu = toolbar.getMenu();
                                menu.findItem(R.id.action_followers).setVisible(false);
                                menu.findItem(R.id.action_follow).setVisible(false);
                                menu.findItem(R.id.action_unfollow).setVisible(true);
                                menu.findItem(R.id.action_report).setVisible(false);
                                menu.findItem(R.id.action_block).setVisible(false);
                                menu.findItem(R.id.action_unblock).setVisible(false);
                            } else {
                                //Not following, hide unfollow, unblock buttons
                                Menu menu = toolbar.getMenu();
                                menu.findItem(R.id.action_followers).setVisible(false);
                                menu.findItem(R.id.action_follow).setVisible(true);
                                menu.findItem(R.id.action_unfollow).setVisible(false);
                                menu.findItem(R.id.action_report).setVisible(true);
                                menu.findItem(R.id.action_block).setVisible(true);
                                menu.findItem(R.id.action_unblock).setVisible(false);
                            }
                            //TODO: Check if blocked...
                        }
                    } else {
                        GroupBlog gBlog = new GroupBlog().fromJson(status.getBody().getJSONObject("blog"));
                        theBlog = gBlog;

                        //Hide age badge
                        ageBadge.setVisibility(View.INVISIBLE);

                        //Check if user owns blog or not...
                        if (gBlog.getOwners().contains(SettingsManager.getManager().getSettings().getAccountId())) {
                            //Hide block/report/follow shit.
                            Menu menu = toolbar.getMenu();
                            menu.findItem(R.id.action_followers).setVisible(true);
                            menu.findItem(R.id.action_follow).setVisible(false);
                            menu.findItem(R.id.action_unfollow).setVisible(false);
                            menu.findItem(R.id.action_report).setVisible(false);
                            menu.findItem(R.id.action_block).setVisible(false);
                            menu.findItem(R.id.action_unblock).setVisible(false);
                        } else {
                            //Check following status...
                            if (gBlog.getFollowers().contains(SettingsManager.getManager().getSettings().getAccountId())) {
                                //Following, hide following, block, and unblock buttons
                                Menu menu = toolbar.getMenu();
                                menu.findItem(R.id.action_followers).setVisible(false);
                                menu.findItem(R.id.action_follow).setVisible(false);
                                menu.findItem(R.id.action_unfollow).setVisible(true);
                                menu.findItem(R.id.action_report).setVisible(false);
                                menu.findItem(R.id.action_block).setVisible(false);
                                menu.findItem(R.id.action_unblock).setVisible(false);
                            } else {
                                //Not following, hide unfollow, unblock buttons
                                Menu menu = toolbar.getMenu();
                                menu.findItem(R.id.action_followers).setVisible(false);
                                menu.findItem(R.id.action_follow).setVisible(true);
                                menu.findItem(R.id.action_unfollow).setVisible(false);
                                menu.findItem(R.id.action_report).setVisible(true);
                                menu.findItem(R.id.action_block).setVisible(true);
                                menu.findItem(R.id.action_unblock).setVisible(false);
                            }
                            //TODO: Check if blocked...
                        }
                    }

                    rootLayout.addView(view);
                }


                if (!blockOn)
                    getPosts();
            } else {
                Toast.makeText(this, status.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException ignore) {
        }
    }

    @SuppressLint("SetTextI18n")
    public void getBlogOwnerAge(NetworkCallStatus status) {
        try {
            if (status.isSuccess()) {
                Account acc = new Account().fromJson(status.getBody().getJSONObject("account"));
                int age = acc.getAge();
                ageBadge.setText(age + "");
            } else {
                ageBadge.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException ignore) {
            ageBadge.setVisibility(View.INVISIBLE);
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
                    if (rootLayout.getChildCount() > 1)
                        rootLayout.removeViews(1, rootLayout.getChildCount() - 1);
                }

                for (IPost p : posts) {
                    //Skip posts not in range. Probably a parent post which will be handled correctly.
                    if (p.getTimestamp() >= index.getOldest() && p.getTimestamp() <= index.getLatest() && p.getOriginBlog().getBlogId().equals(blogId)) {
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
            GetPostsForBlogTask task = new GetPostsForBlogTask(this, blogId, index);
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
        switch (status.getType()) {
            case ACCOUNT_GET_BLOG:
                getBlogOwnerAge(status);
                break;
            case BLOG_GET_VIEW:
                getBlogCallback(status);
                break;
            case FOLLOW_FOLLOW_BLOG:
                restart();
                break;
            case FOLLOW_UNFOLLOW_BLOG:
                restart();
                break;
            case POST_GET_FOR_BLOG:
                getPostsCallback(status);
                break;
            default:
                //Unsupported callback...
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_view_toolbar, menu);

        //TODO: Proper share action handling. for now its just copying link to clipboard...

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                //TODO: Proper share action handling. for now its just copying link to clipboard.
                if (theBlog != null) {
                    ClipData data = ClipData.newPlainText("Blog URL", theBlog.getCompleteUrl());
                    StarTappedApp.getInstance().getClipboard().setPrimaryClip(data);
                    Toast.makeText(getApplicationContext(), "Blog URL Copied to Clipboard", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_followers:
                //TODO: Actually show followers... for now just display the count as a Toast.
                Toast.makeText(this, theBlog.getFollowers().size() + " follower(s)", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_follow:
                //Handle follow...
                new FollowBlogTask(this, blogId).execute();
                return true;
            case R.id.action_unfollow:
                //Handle unfollow...
                new UnfollowBlogTask(this, blogId).execute();
                return true;
            case R.id.action_report:
                //TODO: Handle report...
                return true;
            case R.id.action_block:
                //TODO: Handle block
                return true;
            case R.id.action_unblock:
                //TODO: Handle unblock
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void restart() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
