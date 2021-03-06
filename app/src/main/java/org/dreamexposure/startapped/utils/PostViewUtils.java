package org.dreamexposure.startapped.utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.activities.SearchActivity;
import org.dreamexposure.startapped.activities.blog.ViewBlogActivity;
import org.dreamexposure.startapped.network.bookmark.AddBookmarkTask;
import org.dreamexposure.startapped.network.bookmark.RemoveBookmarkTask;
import org.dreamexposure.startapped.objects.container.AudioContainer;
import org.dreamexposure.startapped.objects.container.ImageContainer;
import org.dreamexposure.startapped.objects.container.VideoContainer;
import org.dreamexposure.startapped.objects.post.AudioPost;
import org.dreamexposure.startapped.objects.post.IPost;
import org.dreamexposure.startapped.objects.post.ImagePost;
import org.dreamexposure.startapped.objects.post.TextPost;
import org.dreamexposure.startapped.objects.post.VideoPost;
import org.dreamexposure.startapped.objects.time.TimeIndex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@SuppressLint("InflateParams")
public class PostViewUtils {
    //From tree
    public static View generatePostViewFromTree(IPost lowest, List<IPost> posts, AppCompatActivity activity) {
        LinearLayout root = new LinearLayout(activity);

        LinkedList<IPost> postsInOrder = new LinkedList<>();

        IPost current = lowest;

        while (current.getParent() != null) {
            IPost parent = PostUtils.getPostFromArray(posts, current.getParent());
            if (parent != null) {
                postsInOrder.add(parent);

                current = parent;
            } else {
                break;
            }
        }
        //It should start with the highest parent and end with the second to last....

        View first = null;
        for (IPost p : postsInOrder) {
            View v = null;
            if (p instanceof TextPost) {
                v = generateTextPostView((TextPost) p, null, false, false, false, activity);
            } else if (p instanceof ImagePost) {
                v = generateImagePostView((ImagePost) p, null, false, false, false, activity);
            } else if (p instanceof AudioPost) {
                v = generateAudioPostView((AudioPost) p, null, false, false, false, activity);
            } else if (p instanceof VideoPost) {
                v = generateVideoPostView((VideoPost) p, null, false, false, false, activity);
            }
            if (first == null)
                first = v;
            root.addView(v);
        }

        //Add bottom child...
        View child = null;
        if (lowest instanceof TextPost) {
            child = generateTextPostView((TextPost) lowest, null, false, true, true, activity);
        } else if (lowest instanceof ImagePost) {
            child = generateImagePostView((ImagePost) lowest, null, false, true, true, activity);
        } else if (lowest instanceof AudioPost) {
            child = generateAudioPostView((AudioPost) lowest, null, false, true, true, activity);
        } else if (lowest instanceof VideoPost) {
            child = generateVideoPostView((VideoPost) lowest, null, false, true, true, activity);
        }
        root.addView(child);

        if (first != null) {
            TextView blogUrlLatest = first.findViewById(R.id.blog_url_latest);
            TextView blogUrlSecond = first.findViewById(R.id.blog_url_second);
            ImageView reblogIcon = first.findViewById(R.id.reblog_icon);
            View divider = first.findViewById(R.id.divider4);

            blogUrlLatest.setText(lowest.getOriginBlog().getBaseUrl());
            blogUrlSecond.setText(postsInOrder.get(0).getOriginBlog().getBaseUrl());
            blogUrlLatest.setVisibility(View.VISIBLE);
            blogUrlSecond.setVisibility(View.VISIBLE);
            reblogIcon.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);

            //Set handlers for url click...
            blogUrlLatest.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", lowest.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                activity.startActivity(intent);
            });
            blogUrlSecond.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", postsInOrder.get(0).getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                activity.startActivity(intent);
            });
        }

        return root;
    }

    public static View generatePostViewFromTree(IPost lowest, List<IPost> posts, FragmentActivity fragment) {
        LinearLayout root = new LinearLayout(fragment);

        LinkedList<IPost> postsInOrder = new LinkedList<>();

        IPost current = lowest;

        while (current.getParent() != null) {
            IPost parent = PostUtils.getPostFromArray(posts, current.getParent());
            if (parent != null) {
                postsInOrder.add(parent);

                current = parent;
            } else {
                break;
            }
        }
        //It should start with the highest parent and end with the second to last....

        View first = null;
        for (IPost p : postsInOrder) {
            View v = null;
            if (p instanceof TextPost) {
                v = generateTextPostView((TextPost) p, null, false, false, false, fragment);
            } else if (p instanceof ImagePost) {
                v = generateImagePostView((ImagePost) p, null, false, false, false, fragment);
            } else if (p instanceof AudioPost) {
                v = generateAudioPostView((AudioPost) p, null, false, false, false, fragment);
            } else if (p instanceof VideoPost) {
                v = generateVideoPostView((VideoPost) p, null, false, false, false, fragment);
            }
            if (first == null)
                first = v;
            root.addView(v);
        }

        //Add bottom child...
        View child = null;
        if (lowest instanceof TextPost) {
            child = generateTextPostView((TextPost) lowest, null, false, true, true, fragment);
        } else if (lowest instanceof ImagePost) {
            child = generateImagePostView((ImagePost) lowest, null, false, true, true, fragment);
        } else if (lowest instanceof AudioPost) {
            child = generateAudioPostView((AudioPost) lowest, null, false, true, true, fragment);
        } else if (lowest instanceof VideoPost) {
            child = generateVideoPostView((VideoPost) lowest, null, false, true, true, fragment);
        }
        root.addView(child);

        if (first != null) {
            TextView blogUrlLatest = first.findViewById(R.id.blog_url_latest);
            TextView blogUrlSecond = first.findViewById(R.id.blog_url_second);
            ImageView reblogIcon = first.findViewById(R.id.reblog_icon);
            View divider = first.findViewById(R.id.divider4);

            blogUrlLatest.setText(lowest.getOriginBlog().getBaseUrl());
            blogUrlSecond.setText(postsInOrder.get(0).getOriginBlog().getBaseUrl());
            blogUrlLatest.setVisibility(View.VISIBLE);
            blogUrlSecond.setVisibility(View.VISIBLE);
            reblogIcon.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);

            //Set handlers for url click...
            blogUrlLatest.setOnClickListener(v -> {
                Intent intent = new Intent(fragment, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", lowest.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                fragment.startActivity(intent);
            });
            blogUrlSecond.setOnClickListener(v -> {
                Intent intent = new Intent(fragment, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", postsInOrder.get(0).getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                fragment.startActivity(intent);
            });
        }

        return root;
    }

    //Text
    @SuppressLint("SetTextI18n")
    public static View generateTextPostView(TextPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, boolean showTags, AppCompatActivity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.post_text_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);
        View divider = view.findViewById(R.id.divider4);

        TextView postTitle = view.findViewById(R.id.post_title);
        TextView postText = view.findViewById(R.id.post_text);

        HorizontalScrollView postTagsScroll = view.findViewById(R.id.post_tags_scroll);
        LinearLayout postTags = view.findViewById(R.id.post_tags);

        TextView source = view.findViewById(R.id.source);
        ImageView bookmarkPost = view.findViewById(R.id.bookmark_post);
        ImageView reblogPost = view.findViewById(R.id.reblog_post);

        //Set data...
        blogUrlLatest.setText(post.getOriginBlog().getBaseUrl());
        if (parent != null) {
            blogUrlSecond.setText(parent.getOriginBlog().getBaseUrl());
        } else {
            reblogIcon.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
        }

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        for (String tag : post.getTags()) {
            Button button = (Button) activity.getLayoutInflater().inflate(R.layout.post_tag_item, null);
            button.setText("#" + tag.trim());

            button.setOnClickListener(view12 -> {
                if (activity instanceof SearchActivity) {
                    SearchActivity sa = (SearchActivity) activity;
                    sa.currentTags.clear();
                    sa.currentTags.add(tag.trim());
                    sa.clear = true;
                    sa.stopRequesting = false;
                    sa.index = new TimeIndex();
                    sa.getPosts();
                } else {
                    Intent intent = new Intent(activity, SearchActivity.class);
                    Bundle b = new Bundle();
                    b.putStringArrayList("tags", new ArrayList<>(Collections.singletonList(tag.trim())));
                    intent.putExtras(b);
                    activity.startActivity(intent);
                }
            });

            postTags.addView(button);
        }

        source.setText(String.format(activity.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

        if (!showTopBar) {
            blogUrlLatest.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
            reblogIcon.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        if (!showBottomBar) {
            source.setVisibility(View.GONE);
            bookmarkPost.setVisibility(View.GONE);
            reblogPost.setVisibility(View.GONE);
        }
        if (post.tagsToString().length() <= 0 || !showTags) {
            postTagsScroll.setVisibility(View.GONE);
        }

        //Set handlers for buttons...
        if (showTopBar) {
            blogUrlLatest.setOnClickListener(view1 -> {
                Intent intent = new Intent(activity, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                activity.startActivity(intent);
            });
            if (parent != null) {
                blogUrlSecond.setOnClickListener(view1 -> {
                    Intent intent = new Intent(activity, ViewBlogActivity.class);
                    Bundle b = new Bundle();
                    b.putString("blog", parent.getOriginBlog().getBlogId().toString());
                    intent.putExtras(b);
                    activity.startActivity(intent);
                });
            }
        }
        if (showBottomBar) {
            source.setOnClickListener(view1 -> {
                Intent intent = new Intent(activity, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                activity.startActivity(intent);
            });

            if (post.isBookmarked())
                bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Primary)));
            else
                bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Dark)));

            bookmarkPost.setOnClickListener(view1 -> {
                if (post.isBookmarked()) {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Dark)));
                    //Remove bookmark...
                    new RemoveBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(false);
                        Toast.makeText(activity, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                } else {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Primary)));
                    //Add bookmark...
                    new AddBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(true);
                        Toast.makeText(activity, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                }
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    public static View generateTextPostView(TextPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, boolean showTags, FragmentActivity fragment) {
        View view = LayoutInflater.from(fragment).inflate(R.layout.post_text_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);
        View divider = view.findViewById(R.id.divider4);

        TextView postTitle = view.findViewById(R.id.post_title);
        TextView postText = view.findViewById(R.id.post_text);

        HorizontalScrollView postTagsScroll = view.findViewById(R.id.post_tags_scroll);
        LinearLayout postTags = view.findViewById(R.id.post_tags);

        TextView source = view.findViewById(R.id.source);
        ImageView bookmarkPost = view.findViewById(R.id.bookmark_post);
        ImageView reblogPost = view.findViewById(R.id.reblog_post);

        //Set data...
        blogUrlLatest.setText(post.getOriginBlog().getBaseUrl());
        if (parent != null) {
            blogUrlSecond.setText(parent.getOriginBlog().getBaseUrl());
        } else {
            reblogIcon.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
        }

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        for (String tag : post.getTags()) {
            Button button = (Button) fragment.getLayoutInflater().inflate(R.layout.post_tag_item, null);
            button.setText("#" + tag.trim());

            button.setOnClickListener(view12 -> {
                Intent intent = new Intent(fragment, SearchActivity.class);
                Bundle b = new Bundle();
                b.putStringArrayList("tags", new ArrayList<>(Collections.singletonList(tag.trim())));
                intent.putExtras(b);
                fragment.startActivity(intent);
            });

            postTags.addView(button);
        }

        source.setText(String.format(fragment.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

        if (!showTopBar) {
            blogUrlLatest.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
            reblogIcon.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        if (!showBottomBar) {
            source.setVisibility(View.GONE);
            bookmarkPost.setVisibility(View.GONE);
            reblogPost.setVisibility(View.GONE);
        }
        if (post.tagsToString().length() <= 0 || !showTags) {
            postTagsScroll.setVisibility(View.GONE);
        }

        //Set handlers for buttons...
        if (showTopBar) {
            blogUrlLatest.setOnClickListener(view1 -> {
                Intent intent = new Intent(fragment, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                fragment.startActivity(intent);
            });
            if (parent != null) {
                blogUrlSecond.setOnClickListener(view1 -> {
                    Intent intent = new Intent(fragment, ViewBlogActivity.class);
                    Bundle b = new Bundle();
                    b.putString("blog", parent.getOriginBlog().getBlogId().toString());
                    intent.putExtras(b);
                    fragment.startActivity(intent);
                });
            }
        }
        if (showBottomBar) {
            source.setOnClickListener(view1 -> {
                Intent intent = new Intent(fragment, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                fragment.startActivity(intent);
            });

            if (post.isBookmarked())
                bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Primary)));
            else
                bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Dark)));

            bookmarkPost.setOnClickListener(view1 -> {
                if (post.isBookmarked()) {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Dark)));
                    //Remove bookmark...
                    new RemoveBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(false);
                        Toast.makeText(fragment, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                } else {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Primary)));
                    //Add bookmark...
                    new AddBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(true);
                        Toast.makeText(fragment, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                }
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }

    //Image
    @SuppressLint("SetTextI18n")
    public static View generateImagePostView(ImagePost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, boolean showTags, AppCompatActivity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.post_image_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);
        View divider = view.findViewById(R.id.divider4);

        ImageView postImage = view.findViewById(R.id.post_image);
        ImageContainer imageContainer = new ImageContainer(postImage, activity.getSupportFragmentManager(), true);

        TextView postTitle = view.findViewById(R.id.post_title);
        TextView postText = view.findViewById(R.id.post_text);

        HorizontalScrollView postTagsScroll = view.findViewById(R.id.post_tags_scroll);
        LinearLayout postTags = view.findViewById(R.id.post_tags);

        TextView source = view.findViewById(R.id.source);
        ImageView bookmarkPost = view.findViewById(R.id.bookmark_post);
        ImageView reblogPost = view.findViewById(R.id.reblog_post);

        //Set data...
        blogUrlLatest.setText(post.getOriginBlog().getBaseUrl());
        if (parent != null) {
            blogUrlSecond.setText(parent.getOriginBlog().getBaseUrl());
        } else {
            reblogIcon.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
        }

        imageContainer.fromUrl(post.getImage().getUrl());

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        for (String tag : post.getTags()) {
            Button button = (Button) activity.getLayoutInflater().inflate(R.layout.post_tag_item, null);
            button.setText("#" + tag.trim());

            button.setOnClickListener(view12 -> {
                if (activity instanceof SearchActivity) {
                    SearchActivity sa = (SearchActivity) activity;
                    sa.currentTags.clear();
                    sa.currentTags.add(tag.trim());
                    sa.clear = true;
                    sa.stopRequesting = false;
                    sa.index = new TimeIndex();
                    sa.getPosts();
                } else {
                    Intent intent = new Intent(activity, SearchActivity.class);
                    Bundle b = new Bundle();
                    b.putStringArrayList("tags", new ArrayList<>(Collections.singletonList(tag.trim())));
                    intent.putExtras(b);
                    activity.startActivity(intent);
                }
            });

            postTags.addView(button);
        }

        source.setText(String.format(activity.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

        if (!showTopBar) {
            blogUrlLatest.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
            reblogIcon.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        if (!showBottomBar) {
            source.setVisibility(View.GONE);
            bookmarkPost.setVisibility(View.GONE);
            reblogPost.setVisibility(View.GONE);
        }
        if (post.tagsToString().length() <= 0 || !showTags) {
            postTagsScroll.setVisibility(View.GONE);
        }

        if (showTopBar) {
            blogUrlLatest.setOnClickListener(view1 -> {
                Intent intent = new Intent(activity, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                activity.startActivity(intent);
            });
            if (parent != null) {
                blogUrlSecond.setOnClickListener(view1 -> {
                    Intent intent = new Intent(activity, ViewBlogActivity.class);
                    Bundle b = new Bundle();
                    b.putString("blog", parent.getOriginBlog().getBlogId().toString());
                    intent.putExtras(b);
                    activity.startActivity(intent);
                });
            }
        }
        if (showBottomBar) {
            source.setOnClickListener(view1 -> {
                Intent intent = new Intent(activity, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                activity.startActivity(intent);
            });

            if (post.isBookmarked())
                bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Primary)));
            else
                bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Dark)));

            bookmarkPost.setOnClickListener(view1 -> {
                if (post.isBookmarked()) {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Dark)));
                    //Remove bookmark...
                    new RemoveBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(false);
                        Toast.makeText(activity, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                } else {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Primary)));
                    //Add bookmark...
                    new AddBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(true);
                        Toast.makeText(activity, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                }
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }

    @SuppressLint("SetTextI18n")
    public static View generateImagePostView(ImagePost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, boolean showTags, FragmentActivity fragment) {
        View view = LayoutInflater.from(fragment).inflate(R.layout.post_image_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);
        View divider = view.findViewById(R.id.divider4);

        ImageView postImage = view.findViewById(R.id.post_image);
        ImageContainer imageContainer = new ImageContainer(postImage, fragment.getSupportFragmentManager(), true);

        TextView postTitle = view.findViewById(R.id.post_title);
        TextView postText = view.findViewById(R.id.post_text);

        HorizontalScrollView postTagsScroll = view.findViewById(R.id.post_tags_scroll);
        LinearLayout postTags = view.findViewById(R.id.post_tags);

        TextView source = view.findViewById(R.id.source);
        ImageView bookmarkPost = view.findViewById(R.id.bookmark_post);
        ImageView reblogPost = view.findViewById(R.id.reblog_post);

        //Set data...
        blogUrlLatest.setText(post.getOriginBlog().getBaseUrl());
        if (parent != null) {
            blogUrlSecond.setText(parent.getOriginBlog().getBaseUrl());
        } else {
            reblogIcon.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
        }

        imageContainer.fromUrl(post.getImage().getUrl());

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        for (String tag : post.getTags()) {
            Button button = (Button) fragment.getLayoutInflater().inflate(R.layout.post_tag_item, null);
            button.setText("#" + tag.trim());

            button.setOnClickListener(view12 -> {
                Intent intent = new Intent(fragment, SearchActivity.class);
                Bundle b = new Bundle();
                b.putStringArrayList("tags", new ArrayList<>(Collections.singletonList(tag.trim())));
                intent.putExtras(b);
                fragment.startActivity(intent);
            });

            postTags.addView(button);
        }

        source.setText(String.format(fragment.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

        if (!showTopBar) {
            blogUrlLatest.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
            reblogIcon.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        if (!showBottomBar) {
            source.setVisibility(View.GONE);
            bookmarkPost.setVisibility(View.GONE);
            reblogPost.setVisibility(View.GONE);
        }
        if (post.tagsToString().length() <= 0 || !showTags) {
            postTagsScroll.setVisibility(View.GONE);
        }

        if (showTopBar) {
            blogUrlLatest.setOnClickListener(view1 -> {
                Intent intent = new Intent(fragment, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                fragment.startActivity(intent);
            });
            if (parent != null) {
                blogUrlSecond.setOnClickListener(view1 -> {
                    Intent intent = new Intent(fragment, ViewBlogActivity.class);
                    Bundle b = new Bundle();
                    b.putString("blog", parent.getOriginBlog().getBlogId().toString());
                    intent.putExtras(b);
                    fragment.startActivity(intent);
                });
            }
        }
        if (showBottomBar) {
            source.setOnClickListener(view1 -> {
                Intent intent = new Intent(fragment, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                fragment.startActivity(intent);
            });

            if (post.isBookmarked())
                bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Primary)));
            else
                bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Dark)));

            bookmarkPost.setOnClickListener(view1 -> {
                if (post.isBookmarked()) {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Dark)));
                    //Remove bookmark...
                    new RemoveBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(false);
                        Toast.makeText(fragment, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                } else {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Primary)));
                    //Add bookmark...
                    new AddBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(true);
                        Toast.makeText(fragment, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                }
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }

    //Audio
    @SuppressLint("SetTextI18n")
    public static View generateAudioPostView(AudioPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, boolean showTags, AppCompatActivity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.post_audio_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);
        View divider = view.findViewById(R.id.divider4);

        AudioContainer container = new AudioContainer(view, activity);

        TextView postTitle = view.findViewById(R.id.post_title);
        TextView postText = view.findViewById(R.id.post_text);

        HorizontalScrollView postTagsScroll = view.findViewById(R.id.post_tags_scroll);
        LinearLayout postTags = view.findViewById(R.id.post_tags);

        TextView source = view.findViewById(R.id.source);
        ImageView bookmarkPost = view.findViewById(R.id.bookmark_post);
        ImageView reblogPost = view.findViewById(R.id.reblog_post);

        //Set data...
        blogUrlLatest.setText(post.getOriginBlog().getBaseUrl());
        if (parent != null) {
            blogUrlSecond.setText(parent.getOriginBlog().getBaseUrl());
        } else {
            reblogIcon.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
        }

        container.setAutoPlay(false);
        container.fromURL(post.getAudio());

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        for (String tag : post.getTags()) {
            Button button = (Button) activity.getLayoutInflater().inflate(R.layout.post_tag_item, null);
            button.setText("#" + tag.trim());

            button.setOnClickListener(view12 -> {
                if (activity instanceof SearchActivity) {
                    SearchActivity sa = (SearchActivity) activity;
                    sa.currentTags.clear();
                    sa.currentTags.add(tag.trim());
                    sa.clear = true;
                    sa.stopRequesting = false;
                    sa.index = new TimeIndex();
                    sa.getPosts();
                } else {
                    Intent intent = new Intent(activity, SearchActivity.class);
                    Bundle b = new Bundle();
                    b.putStringArrayList("tags", new ArrayList<>(Collections.singletonList(tag.trim())));
                    intent.putExtras(b);
                    activity.startActivity(intent);
                }
            });

            postTags.addView(button);
        }

        source.setText(String.format(activity.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

        if (!showTopBar) {
            blogUrlLatest.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
            reblogIcon.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        if (!showBottomBar) {
            source.setVisibility(View.GONE);
            bookmarkPost.setVisibility(View.GONE);
            reblogPost.setVisibility(View.GONE);
        }
        if (post.tagsToString().length() <= 0 || !showTags) {
            postTagsScroll.setVisibility(View.GONE);
        }

        if (showTopBar) {
            blogUrlLatest.setOnClickListener(view1 -> {
                Intent intent = new Intent(activity, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                activity.startActivity(intent);
            });
            if (parent != null) {
                blogUrlSecond.setOnClickListener(view1 -> {
                    Intent intent = new Intent(activity, ViewBlogActivity.class);
                    Bundle b = new Bundle();
                    b.putString("blog", parent.getOriginBlog().getBlogId().toString());
                    intent.putExtras(b);
                    activity.startActivity(intent);
                });
            }
        }
        if (showBottomBar) {
            source.setOnClickListener(view1 -> {
                Intent intent = new Intent(activity, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                activity.startActivity(intent);
            });

            if (post.isBookmarked())
                bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Primary)));
            else
                bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Dark)));

            bookmarkPost.setOnClickListener(view1 -> {
                if (post.isBookmarked()) {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Dark)));
                    //Remove bookmark...
                    new RemoveBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(false);
                        Toast.makeText(activity, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                } else {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Primary)));
                    //Add bookmark...
                    new AddBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(true);
                        Toast.makeText(activity, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                }
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }

    @SuppressLint("SetTextI18n")
    public static View generateAudioPostView(AudioPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, boolean showTags, FragmentActivity fragment) {
        View view = LayoutInflater.from(fragment).inflate(R.layout.post_audio_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);
        View divider = view.findViewById(R.id.divider4);

        AudioContainer container = new AudioContainer(view, fragment);

        TextView postTitle = view.findViewById(R.id.post_title);
        TextView postText = view.findViewById(R.id.post_text);

        HorizontalScrollView postTagsScroll = view.findViewById(R.id.post_tags_scroll);
        LinearLayout postTags = view.findViewById(R.id.post_tags);

        TextView source = view.findViewById(R.id.source);
        ImageView bookmarkPost = view.findViewById(R.id.bookmark_post);
        ImageView reblogPost = view.findViewById(R.id.reblog_post);

        //Set data...
        blogUrlLatest.setText(post.getOriginBlog().getBaseUrl());
        if (parent != null) {
            blogUrlSecond.setText(parent.getOriginBlog().getBaseUrl());
        } else {
            reblogIcon.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
        }

        container.setAutoPlay(false);
        container.fromURL(post.getAudio());

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        for (String tag : post.getTags()) {
            Button button = (Button) fragment.getLayoutInflater().inflate(R.layout.post_tag_item, null);
            button.setText("#" + tag.trim());

            button.setOnClickListener(view12 -> {
                Intent intent = new Intent(fragment, SearchActivity.class);
                Bundle b = new Bundle();
                b.putStringArrayList("tags", new ArrayList<>(Collections.singletonList(tag.trim())));
                intent.putExtras(b);
                fragment.startActivity(intent);
            });

            postTags.addView(button);
        }

        source.setText(String.format(fragment.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

        if (!showTopBar) {
            blogUrlLatest.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
            reblogIcon.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        if (!showBottomBar) {
            source.setVisibility(View.GONE);
            bookmarkPost.setVisibility(View.GONE);
            reblogPost.setVisibility(View.GONE);
        }
        if (post.tagsToString().length() <= 0 || !showTags) {
            postTagsScroll.setVisibility(View.GONE);
        }

        if (showTopBar) {
            blogUrlLatest.setOnClickListener(view1 -> {
                Intent intent = new Intent(fragment, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                fragment.startActivity(intent);
            });
            if (parent != null) {
                blogUrlSecond.setOnClickListener(view1 -> {
                    Intent intent = new Intent(fragment, ViewBlogActivity.class);
                    Bundle b = new Bundle();
                    b.putString("blog", parent.getOriginBlog().getBlogId().toString());
                    intent.putExtras(b);
                    fragment.startActivity(intent);
                });
            }
        }
        if (showBottomBar) {
            source.setOnClickListener(view1 -> {
                Intent intent = new Intent(fragment, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                fragment.startActivity(intent);
            });

            if (post.isBookmarked())
                bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Primary)));
            else
                bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Dark)));

            bookmarkPost.setOnClickListener(view1 -> {
                if (post.isBookmarked()) {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Dark)));
                    //Remove bookmark...
                    new RemoveBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(false);
                        Toast.makeText(fragment, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                } else {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Primary)));
                    //Add bookmark...
                    new AddBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(true);
                        Toast.makeText(fragment, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                }
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }

    //Video
    @SuppressLint("SetTextI18n")
    public static View generateVideoPostView(VideoPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, boolean showTags, AppCompatActivity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.post_video_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);
        View divider = view.findViewById(R.id.divider4);

        VideoContainer container = new VideoContainer(view);

        TextView postTitle = view.findViewById(R.id.post_title);
        TextView postText = view.findViewById(R.id.post_text);

        HorizontalScrollView postTagsScroll = view.findViewById(R.id.post_tags_scroll);
        LinearLayout postTags = view.findViewById(R.id.post_tags);

        TextView source = view.findViewById(R.id.source);
        ImageView bookmarkPost = view.findViewById(R.id.bookmark_post);
        ImageView reblogPost = view.findViewById(R.id.reblog_post);

        //Set data...
        blogUrlLatest.setText(post.getOriginBlog().getBaseUrl());
        if (parent != null) {
            blogUrlSecond.setText(parent.getOriginBlog().getBaseUrl());
        } else {
            reblogIcon.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
        }

        container.setAutoPlay(false);
        container.fromURL(post.getVideo().getUrl());

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        for (String tag : post.getTags()) {
            Button button = (Button) activity.getLayoutInflater().inflate(R.layout.post_tag_item, null);
            button.setText("#" + tag.trim());

            button.setOnClickListener(view12 -> {
                if (activity instanceof SearchActivity) {
                    SearchActivity sa = (SearchActivity) activity;
                    sa.currentTags.clear();
                    sa.currentTags.add(tag.trim());
                    sa.clear = true;
                    sa.stopRequesting = false;
                    sa.index = new TimeIndex();
                    sa.getPosts();
                } else {
                    Intent intent = new Intent(activity, SearchActivity.class);
                    Bundle b = new Bundle();
                    b.putStringArrayList("tags", new ArrayList<>(Collections.singletonList(tag.trim())));
                    intent.putExtras(b);
                    activity.startActivity(intent);
                }
            });

            postTags.addView(button);
        }

        source.setText(String.format(activity.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

        if (!showTopBar) {
            blogUrlLatest.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
            reblogIcon.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        if (!showBottomBar) {
            source.setVisibility(View.GONE);
            bookmarkPost.setVisibility(View.GONE);
            reblogPost.setVisibility(View.GONE);
        }
        if (post.tagsToString().length() <= 0 || !showTags) {
            postTagsScroll.setVisibility(View.GONE);
        }

        if (showTopBar) {
            blogUrlLatest.setOnClickListener(view1 -> {
                Intent intent = new Intent(activity, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                activity.startActivity(intent);
            });
            if (parent != null) {
                blogUrlSecond.setOnClickListener(view1 -> {
                    Intent intent = new Intent(activity, ViewBlogActivity.class);
                    Bundle b = new Bundle();
                    b.putString("blog", parent.getOriginBlog().getBlogId().toString());
                    intent.putExtras(b);
                    activity.startActivity(intent);
                });
            }
        }
        if (showBottomBar) {
            source.setOnClickListener(view1 -> {
                Intent intent = new Intent(activity, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                activity.startActivity(intent);
            });

            if (post.isBookmarked())
                bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Primary)));
            else
                bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Dark)));

            bookmarkPost.setOnClickListener(view1 -> {
                if (post.isBookmarked()) {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Dark)));
                    //Remove bookmark...
                    new RemoveBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(false);
                        Toast.makeText(activity, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                } else {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.Primary)));
                    //Add bookmark...
                    new AddBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(true);
                        Toast.makeText(activity, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                }
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }

    @SuppressLint("SetTextI18n")
    public static View generateVideoPostView(VideoPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, boolean showTags, FragmentActivity fragment) {
        View view = LayoutInflater.from(fragment).inflate(R.layout.post_video_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);
        View divider = view.findViewById(R.id.divider4);

        VideoContainer container = new VideoContainer(view);

        TextView postTitle = view.findViewById(R.id.post_title);
        TextView postText = view.findViewById(R.id.post_text);

        HorizontalScrollView postTagsScroll = view.findViewById(R.id.post_tags_scroll);
        LinearLayout postTags = view.findViewById(R.id.post_tags);

        TextView source = view.findViewById(R.id.source);
        ImageView bookmarkPost = view.findViewById(R.id.bookmark_post);
        ImageView reblogPost = view.findViewById(R.id.reblog_post);

        //Set data...
        blogUrlLatest.setText(post.getOriginBlog().getBaseUrl());
        if (parent != null) {
            blogUrlSecond.setText(parent.getOriginBlog().getBaseUrl());
        } else {
            reblogIcon.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
        }

        container.setAutoPlay(false);
        container.fromURL(post.getVideo().getUrl());

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        for (String tag : post.getTags()) {
            Button button = (Button) fragment.getLayoutInflater().inflate(R.layout.post_tag_item, null);
            button.setText("#" + tag.trim());

            button.setOnClickListener(view12 -> {
                Intent intent = new Intent(fragment, SearchActivity.class);
                Bundle b = new Bundle();
                b.putStringArrayList("tags", new ArrayList<>(Collections.singletonList(tag.trim())));
                intent.putExtras(b);
                fragment.startActivity(intent);
            });

            postTags.addView(button);
        }

        source.setText(String.format(fragment.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

        if (!showTopBar) {
            blogUrlLatest.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
            reblogIcon.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        if (!showBottomBar) {
            source.setVisibility(View.GONE);
            bookmarkPost.setVisibility(View.GONE);
            reblogPost.setVisibility(View.GONE);
        }
        if (post.tagsToString().length() <= 0 || !showTags) {
            postTagsScroll.setVisibility(View.GONE);
        }
        if (showTopBar) {
            blogUrlLatest.setOnClickListener(view1 -> {
                Intent intent = new Intent(fragment.getApplicationContext(), ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                fragment.getApplicationContext().startActivity(intent);
            });
            if (parent != null) {
                blogUrlSecond.setOnClickListener(view1 -> {
                    Intent intent = new Intent(fragment.getApplicationContext(), ViewBlogActivity.class);
                    Bundle b = new Bundle();
                    b.putString("blog", parent.getOriginBlog().getBlogId().toString());
                    intent.putExtras(b);
                    fragment.getApplicationContext().startActivity(intent);
                });
            }
        }
        if (showBottomBar) {
            source.setOnClickListener(view1 -> {
                Intent intent = new Intent(fragment.getApplicationContext(), ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                fragment.getApplicationContext().startActivity(intent);
            });

            if (post.isBookmarked())
                bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Primary)));
            else
                bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Dark)));

            bookmarkPost.setOnClickListener(view1 -> {
                if (post.isBookmarked()) {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Dark)));
                    //Remove bookmark...
                    new RemoveBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(false);
                        Toast.makeText(fragment, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                } else {
                    bookmarkPost.setImageTintList(ColorStateList.valueOf(fragment.getResources().getColor(R.color.Primary)));
                    //Add bookmark...
                    new AddBookmarkTask(status -> {
                        if (status.isSuccess())
                            post.setBookmarked(true);
                        Toast.makeText(fragment, status.getMessage(), Toast.LENGTH_SHORT).show();
                    }, post.getId()).execute();
                }
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }
}
