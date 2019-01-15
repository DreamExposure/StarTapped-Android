package org.dreamexposure.startapped.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felipecsl.gifimageview.library.GifImageView;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.activities.blog.ViewBlogActivity;
import org.dreamexposure.startapped.network.download.DownloadImageTask;
import org.dreamexposure.startapped.objects.post.AudioPost;
import org.dreamexposure.startapped.objects.post.IPost;
import org.dreamexposure.startapped.objects.post.ImagePost;
import org.dreamexposure.startapped.objects.post.TextPost;
import org.dreamexposure.startapped.objects.post.VideoPost;

import java.util.LinkedList;
import java.util.List;

@SuppressLint("InflateParams")
public class PostViewUtils {
    public static View generatePostViewFromTree(IPost lowest, List<IPost> posts, Context context) {
        LinearLayout root = new LinearLayout(context);

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
                v = generateTextPostView((TextPost)p, null, false, false, context);
            } else if (p instanceof ImagePost) {
                v = generateImagePostView((ImagePost)p, null, false, false, context);
            } else if (p instanceof AudioPost) {
                v = generateAudioPostView((AudioPost)p, null, false, false, context);
            } else if (p instanceof VideoPost) {
                v = generateVideoPostView((VideoPost)p, null, false, false, context);
            }
            if (first == null)
                first = v;
            root.addView(v);
        }

        //Add bottom child...
        View child = null;
        if (lowest instanceof TextPost) {
            child = generateTextPostView((TextPost)lowest, null, false, true, context);
        } else if (lowest instanceof ImagePost) {
            child = generateImagePostView((ImagePost)lowest, null, false, true, context);
        } else if (lowest instanceof AudioPost) {
            child = generateAudioPostView((AudioPost)lowest, null, false, true, context);
        } else if (lowest instanceof VideoPost) {
            child = generateVideoPostView((VideoPost)lowest, null, false, true, context);
        }
        root.addView(child);

        if (first != null) {
            TextView blogUrlLatest = first.findViewById(R.id.blog_url_latest);
            TextView blogUrlSecond = first.findViewById(R.id.blog_url_second);
            ImageView reblogIcon = first.findViewById(R.id.reblog_icon);

            blogUrlLatest.setText(lowest.getOriginBlog().getBaseUrl());
            blogUrlSecond.setText(postsInOrder.get(0).getOriginBlog().getBaseUrl());
            blogUrlLatest.setVisibility(View.VISIBLE);
            blogUrlSecond.setVisibility(View.VISIBLE);
            reblogIcon.setVisibility(View.VISIBLE);

            //Set handlers for url click...
            blogUrlLatest.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", lowest.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                context.startActivity(intent);
            });
            blogUrlSecond.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", postsInOrder.get(0).getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                context.startActivity(intent);
            });
        }

        return root;
    }

    public static View generateTextPostView(TextPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_text_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);

        TextView postTitle = view.findViewById(R.id.post_title);
        TextView postText = view.findViewById(R.id.post_text);

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

        source.setText(String.format(context.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

        if (!showTopBar) {
            blogUrlLatest.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
            reblogIcon.setVisibility(View.GONE);
        }
        if (!showBottomBar) {
            source.setVisibility(View.GONE);
            bookmarkPost.setVisibility(View.GONE);
            reblogPost.setVisibility(View.GONE);
        }

        //Set handlers for buttons...
        if (showTopBar) {
            blogUrlLatest.setOnClickListener(view1 -> {
                Intent intent = new Intent(context, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                context.startActivity(intent);
            });
            if (parent != null) {
                blogUrlSecond.setOnClickListener(view1 -> {
                    Intent intent = new Intent(context, ViewBlogActivity.class);
                    Bundle b = new Bundle();
                    b.putString("blog", parent.getOriginBlog().getBlogId().toString());
                    intent.putExtras(b);
                    context.startActivity(intent);
                });
            }
        }
        if (showBottomBar) {
            source.setOnClickListener(view1 -> {
                Intent intent = new Intent(context, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                context.startActivity(intent);
            });
            bookmarkPost.setOnClickListener(view1 -> {

            });
            reblogPost.setOnClickListener(view1 -> {

            });
        }

        return view;
    }

    public static View generateImagePostView(ImagePost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_image_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);

        GifImageView postImage = view.findViewById(R.id.post_image);
        TextView postTitle = view.findViewById(R.id.post_title);
        TextView postText = view.findViewById(R.id.post_text);

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

        new DownloadImageTask(postImage).execute(post.getImageUrl());

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        source.setText(String.format(context.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

        if (!showTopBar) {
            blogUrlLatest.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
            reblogIcon.setVisibility(View.GONE);
        }
        if (!showBottomBar) {
            source.setVisibility(View.GONE);
            bookmarkPost.setVisibility(View.GONE);
            reblogPost.setVisibility(View.GONE);
        }

        if (showTopBar) {
            blogUrlLatest.setOnClickListener(view1 -> {
                Intent intent = new Intent(context, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                context.startActivity(intent);
            });
            if (parent != null) {
                blogUrlSecond.setOnClickListener(view1 -> {
                    Intent intent = new Intent(context, ViewBlogActivity.class);
                    Bundle b = new Bundle();
                    b.putString("blog", parent.getOriginBlog().getBlogId().toString());
                    intent.putExtras(b);
                    context.startActivity(intent);
                });
            }
        }
        if (showBottomBar) {
            source.setOnClickListener(view1 -> {
                Intent intent = new Intent(context, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                context.startActivity(intent);
            });
            bookmarkPost.setOnClickListener(view1 -> {

            });
            reblogPost.setOnClickListener(view1 -> {

            });
        }
        //TODO: Set image expand handler...

        return view;
    }

    public static View generateAudioPostView(AudioPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_video_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);

        //TODO: Load audio views and controls...
        TextView postTitle = view.findViewById(R.id.post_title);
        TextView postText = view.findViewById(R.id.post_text);

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

        //TODO: Set audio stuffs

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        source.setText(String.format(context.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

        if (!showTopBar) {
            blogUrlLatest.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
            reblogIcon.setVisibility(View.GONE);
        }
        if (!showBottomBar) {
            source.setVisibility(View.GONE);
            bookmarkPost.setVisibility(View.GONE);
            reblogPost.setVisibility(View.GONE);
        }

        if (showTopBar) {
            blogUrlLatest.setOnClickListener(view1 -> {
                Intent intent = new Intent(context, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                context.startActivity(intent);
            });
            if (parent != null) {
                blogUrlSecond.setOnClickListener(view1 -> {
                    Intent intent = new Intent(context, ViewBlogActivity.class);
                    Bundle b = new Bundle();
                    b.putString("blog", parent.getOriginBlog().getBlogId().toString());
                    intent.putExtras(b);
                    context.startActivity(intent);
                });
            }
        }
        if (showBottomBar) {
            source.setOnClickListener(view1 -> {
                Intent intent = new Intent(context, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                context.startActivity(intent);
            });
            bookmarkPost.setOnClickListener(view1 -> {

            });
            reblogPost.setOnClickListener(view1 -> {

            });
        }
        //TODO: Setup controls for audio

        return view;
    }

    public static View generateVideoPostView(VideoPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_video_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);

        //TODO: Load video views and controls...
        TextView postTitle = view.findViewById(R.id.post_title);
        TextView postText = view.findViewById(R.id.post_text);

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

        //TODO: Set video stuffs

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        source.setText(String.format(context.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

        if (!showTopBar) {
            blogUrlLatest.setVisibility(View.GONE);
            blogUrlSecond.setVisibility(View.GONE);
            reblogIcon.setVisibility(View.GONE);
        }
        if (!showBottomBar) {
            source.setVisibility(View.GONE);
            bookmarkPost.setVisibility(View.GONE);
            reblogPost.setVisibility(View.GONE);
        }

        if (showTopBar) {
            blogUrlLatest.setOnClickListener(view1 -> {
                Intent intent = new Intent(context, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                context.startActivity(intent);
            });
            if (parent != null) {
                blogUrlSecond.setOnClickListener(view1 -> {
                    Intent intent = new Intent(context, ViewBlogActivity.class);
                    Bundle b = new Bundle();
                    b.putString("blog", parent.getOriginBlog().getBlogId().toString());
                    intent.putExtras(b);
                    context.startActivity(intent);
                });
            }
        }
        if (showBottomBar) {
            source.setOnClickListener(view1 -> {
                Intent intent = new Intent(context, ViewBlogActivity.class);
                Bundle b = new Bundle();
                b.putString("blog", post.getOriginBlog().getBlogId().toString());
                intent.putExtras(b);
                context.startActivity(intent);
            });
            bookmarkPost.setOnClickListener(view1 -> {

            });
            reblogPost.setOnClickListener(view1 -> {

            });
        }
        //TODO: Setup controls for video

        return view;
    }
}
