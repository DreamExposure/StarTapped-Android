package org.dreamexposure.startapped.utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felipecsl.gifimageview.library.GifImageView;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.activities.blog.ViewBlogActivity;
import org.dreamexposure.startapped.objects.container.AudioContainer;
import org.dreamexposure.startapped.objects.container.ImageContainer;
import org.dreamexposure.startapped.objects.container.VideoContainer;
import org.dreamexposure.startapped.objects.post.AudioPost;
import org.dreamexposure.startapped.objects.post.IPost;
import org.dreamexposure.startapped.objects.post.ImagePost;
import org.dreamexposure.startapped.objects.post.TextPost;
import org.dreamexposure.startapped.objects.post.VideoPost;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
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
                v = generateTextPostView((TextPost) p, null, false, false, activity);
            } else if (p instanceof ImagePost) {
                v = generateImagePostView((ImagePost) p, null, false, false, activity);
            } else if (p instanceof AudioPost) {
                v = generateAudioPostView((AudioPost) p, null, false, false, activity);
            } else if (p instanceof VideoPost) {
                v = generateVideoPostView((VideoPost) p, null, false, false, activity);
            }
            if (first == null)
                first = v;
            root.addView(v);
        }

        //Add bottom child...
        View child = null;
        if (lowest instanceof TextPost) {
            child = generateTextPostView((TextPost) lowest, null, false, true, activity);
        } else if (lowest instanceof ImagePost) {
            child = generateImagePostView((ImagePost) lowest, null, false, true, activity);
        } else if (lowest instanceof AudioPost) {
            child = generateAudioPostView((AudioPost) lowest, null, false, true, activity);
        } else if (lowest instanceof VideoPost) {
            child = generateVideoPostView((VideoPost) lowest, null, false, true, activity);
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
                v = generateTextPostView((TextPost) p, null, false, false, fragment);
            } else if (p instanceof ImagePost) {
                v = generateImagePostView((ImagePost) p, null, false, false, fragment);
            } else if (p instanceof AudioPost) {
                v = generateAudioPostView((AudioPost) p, null, false, false, fragment);
            } else if (p instanceof VideoPost) {
                v = generateVideoPostView((VideoPost) p, null, false, false, fragment);
            }
            if (first == null)
                first = v;
            root.addView(v);
        }

        //Add bottom child...
        View child = null;
        if (lowest instanceof TextPost) {
            child = generateTextPostView((TextPost) lowest, null, false, true, fragment);
        } else if (lowest instanceof ImagePost) {
            child = generateImagePostView((ImagePost) lowest, null, false, true, fragment);
        } else if (lowest instanceof AudioPost) {
            child = generateAudioPostView((AudioPost) lowest, null, false, true, fragment);
        } else if (lowest instanceof VideoPost) {
            child = generateVideoPostView((VideoPost) lowest, null, false, true, fragment);
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
    public static View generateTextPostView(TextPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, AppCompatActivity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.post_text_container, null);

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

        source.setText(String.format(activity.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

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
            bookmarkPost.setOnClickListener(view1 -> {
                //TODO: Handle bookmark
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }

    public static View generateTextPostView(TextPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, FragmentActivity fragment) {
        View view = LayoutInflater.from(fragment).inflate(R.layout.post_text_container, null);

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

        source.setText(String.format(fragment.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

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
            bookmarkPost.setOnClickListener(view1 -> {
                //TODO: Handle bookmark
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }

    //Image
    public static View generateImagePostView(ImagePost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, AppCompatActivity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.post_image_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);

        GifImageView postImage = view.findViewById(R.id.post_image);
        ImageContainer imageContainer = new ImageContainer(postImage, activity.getSupportFragmentManager(), true);

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

        imageContainer.fromUrl(post.getImage().getUrl());

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        source.setText(String.format(activity.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

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
            bookmarkPost.setOnClickListener(view1 -> {
                //TODO: Handle bookmark
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }

    public static View generateImagePostView(ImagePost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, FragmentActivity fragment) {
        View view = LayoutInflater.from(fragment).inflate(R.layout.post_image_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);

        GifImageView postImage = view.findViewById(R.id.post_image);
        ImageContainer imageContainer = new ImageContainer(postImage, fragment.getSupportFragmentManager(), true);

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

        imageContainer.fromUrl(post.getImage().getUrl());

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        source.setText(String.format(fragment.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

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
            bookmarkPost.setOnClickListener(view1 -> {
                //TODO: Handle bookmark
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }

    //Audio
    public static View generateAudioPostView(AudioPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, AppCompatActivity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.post_audio_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);

        AudioContainer container = new AudioContainer(view, activity);

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

        container.setAutoPlay(false);
        container.fromURL(post.getAudio());

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        source.setText(String.format(activity.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

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
            bookmarkPost.setOnClickListener(view1 -> {
                //TODO: Handle bookmark
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }

    public static View generateAudioPostView(AudioPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, FragmentActivity fragment) {
        View view = LayoutInflater.from(fragment).inflate(R.layout.post_audio_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);

        AudioContainer container = new AudioContainer(view, fragment);

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

        container.setAutoPlay(false);
        container.fromURL(post.getAudio());

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        source.setText(String.format(fragment.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

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
            bookmarkPost.setOnClickListener(view1 -> {
                //TODO: Handle bookmark
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }

    //Video
    public static View generateVideoPostView(VideoPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, AppCompatActivity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.post_video_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);

        VideoContainer container = new VideoContainer(view);

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

        container.setAutoPlay(false);
        container.fromURL(post.getVideo().getUrl());

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        source.setText(String.format(activity.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

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
            bookmarkPost.setOnClickListener(view1 -> {
                //TODO: Handle bookmark
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }

    public static View generateVideoPostView(VideoPost post, @Nullable IPost parent, boolean showTopBar, boolean showBottomBar, FragmentActivity fragment) {
        View view = LayoutInflater.from(fragment).inflate(R.layout.post_video_container, null);

        //Load views...
        TextView blogUrlLatest = view.findViewById(R.id.blog_url_latest);
        TextView blogUrlSecond = view.findViewById(R.id.blog_url_second);
        ImageView reblogIcon = view.findViewById(R.id.reblog_icon);

        VideoContainer container = new VideoContainer(view);

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

        container.setAutoPlay(false);
        container.fromURL(post.getVideo().getUrl());

        postTitle.setText(post.getTitle());
        postText.setText(post.getBody());

        source.setText(String.format(fragment.getResources().getString(R.string.post_source), post.getOriginBlog().getBaseUrl()));

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
            bookmarkPost.setOnClickListener(view1 -> {
                //TODO: Handle bookmark
            });
            reblogPost.setOnClickListener(view1 -> {
                //TODO: Handle reblog
            });
        }

        return view;
    }
}
