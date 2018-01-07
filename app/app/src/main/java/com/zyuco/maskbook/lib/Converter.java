package com.zyuco.maskbook.lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fivehundredpx.android.blur.BlurringView;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.zyuco.maskbook.GlideApp;
import com.zyuco.maskbook.HomepageActivity;
import com.zyuco.maskbook.LikesActivity;
import com.zyuco.maskbook.MaskbookApplication;
import com.zyuco.maskbook.R;
import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.Post;
import com.zyuco.maskbook.model.User;
import com.zyuco.maskbook.service.API;
import com.zyuco.maskbook.service.APIService;
import com.zyuco.maskbook.tool.CallBack;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class Converter {
    public static void convert(final Activity context, final ViewHolder holder, final Post post) {
        if (post.getId().equals(-1)) {
            convertHeader(context, holder, post.getAuthor());
            return;
        }

        holder.getView(R.id.post).setVisibility(View.VISIBLE);
        holder.getView(R.id.header).setVisibility(View.GONE);

        TextView name = holder.getView(R.id.name);
        name.setText(post.getAuthor().getNickname());
        TextView content = holder.getView(R.id.content);
        if (post.getContent().equals("")) {
            content.setVisibility(View.GONE);
        } else {
            content.setVisibility(View.VISIBLE);
            content.setText(post.getContent());
        }

        TextView time = holder.getView(R.id.time);
        Date localDate = new Date(post.getDate().getTime() + 1000 * 60 * 60 * 8);
        time.setText(String.format(
            context.getResources().getString(R.string.post_date),
            new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(localDate)
        ));

        final BlurringView blur = holder.getView(R.id.blurring_view);
        final ImageView image = holder.getView(R.id.image);

        // image loading
        URL imageURL, avatarURL;
        try {
            imageURL = new URL(new URL(APIService.BASE_URL), post.getImage());
            avatarURL = new URL(new URL(APIService.BASE_URL), post.getAuthor().getAvatar());
        } catch (MalformedURLException e) {
            return;
        }
        GlideApp
            .with(context)
            .load(imageURL)
            .listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    User self = ((MaskbookApplication) context.getApplication()).getUser();
                    if (!post.getUnlock() && post.getParameter() != 0 && !post.getAuthor().getId().equals(self.getId())) {
                        View image = holder.getView(R.id.image_wrapper);
                        blur.setVisibility(View.VISIBLE);
                        blur.setBlurRadius(post.getParameter().intValue());
                        blur.setBlurredView(image);
                    } else {
                        blur.setVisibility(View.INVISIBLE);
                    }
                    return false;
                }
            })
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            .placeholder(R.drawable.placeholder)
            .into(image);
        GlideApp
            .with(context)
            .load(avatarURL)
            .placeholder(R.mipmap.default_avatar)
            .into((ImageView) holder.getView(R.id.avatar));

        holder.getView(R.id.avatar_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(context, HomepageActivity.class);
                intent.putExtra("user", post.getAuthor());
                context.startActivity(intent);
            }
        });

        final ShineButton shine_button = holder.getView(R.id.shine_button);
        shine_button.setChecked(post.getLike());
        shine_button.init(context);

        shine_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.getLike()) {
                    API.unlikePost(post.getId())
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                post.setLike(false);
                                shine_button.setChecked(false);
                            }
                        });
                } else {
                    API.likePost(post.getId())
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                post.setLike(true);
                                shine_button.setChecked(true);
                            }
                        });
                }
            }
        });
    }

    public static void convertHeader(final Activity context, final ViewHolder holder, User user) {
        holder.getView(R.id.post).setVisibility(View.GONE);
        holder.getView(R.id.header).setVisibility(View.VISIBLE);

        TextView name_textView = holder.getView(R.id.header_name);
        name_textView.setText(user.getNickname());

        GlideApp
            .with(context)
            .load(URLFormatter.formatImageURL(user.getAvatar()))
            .placeholder(R.mipmap.default_avatar)
            .into((ImageView) holder.getView(R.id.header_avatar));

        final BlurringView blur = holder.getView(R.id.header_blurring_view);
        blur.setVisibility(View.INVISIBLE);
        blur.setBlurredView(holder.getView(R.id.homepage_background_wrapper));
        API
            .getFirstPostFromUser(user.getId())
            .subscribe(new CallBack<Post>() {
                @Override
                public void onSuccess(Post post) {
                    if (post == null) return;
                    GlideApp
                        .with(context)
                        .load(URLFormatter.formatImageURL(post.getImage()))
                        .placeholder(R.drawable.placeholder)
                        .centerCrop()
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                blur.setVisibility(View.VISIBLE);
                                blur.invalidate();
                                return false;
                            }
                        })
                        .into((ImageView) holder.getView(R.id.homepage_bg));
                }

                @Override
                public void onFail(ErrorResponse e) {

                }

                @Override
                public void onException(Throwable e) {

                }
            });
    }
}
