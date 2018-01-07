package com.zyuco.maskbook.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.INotificationSideChannel;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fivehundredpx.android.blur.BlurringView;
import com.ldoublem.thumbUplib.ThumbUpView;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.zyuco.maskbook.DashboardActivity;
import com.zyuco.maskbook.GlideApp;
import com.zyuco.maskbook.HomepageActivity;
import com.zyuco.maskbook.MaskbookApplication;
import com.zyuco.maskbook.R;
import com.zyuco.maskbook.lib.CommonAdapter;
import com.zyuco.maskbook.lib.Converter;
import com.zyuco.maskbook.lib.ViewHolder;
import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.Post;
import com.zyuco.maskbook.model.User;
import com.zyuco.maskbook.service.API;
import com.zyuco.maskbook.service.APIService;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.functions.Action;

public class PostList {
    private static final String TAG = "Maskbook.postlist";

    private Activity context;
    private CommonAdapter<Post> adapter;
    private List<Post> list = new ArrayList<>();
    private RecyclerView recyclerView;

    private boolean loading = false;
    private boolean ended = false; // reach end

    private String mode = "Dashboard";
    private int user_id;

    public CommonAdapter<Post> getAdapter() {
        return adapter;
    }

    public List<Post> getDataList() {
        return list;
    }

    public void setLoading(boolean status) {
        loading = status;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void resetEnded() {
        ended = false;
    }

    public PostList(final Activity context, String mode, int user_id) {
        this.context = context;

        recyclerView = context.findViewById(R.id.post_list);
        this.mode = mode;
        this.user_id = user_id == -1 ? ((MaskbookApplication) context.getApplication()).getUser().getId() : user_id;

        adapter = new CommonAdapter<Post>(context, R.layout.post_item, list) {
            @Override
            public void convert(final ViewHolder holder, final Post post) {
                Converter.convert(context, holder, post);
            }

            @Override
            public void updateWithPayload(ViewHolder holder, Post data, Object payload) {
                if (payload.equals(true)) {
                    PostList.this.update(holder, data);
                } else {
                    Converter.convert(context, holder, data);
                }
            }
        };
        recyclerView.setAdapter(adapter);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (loading || ended) return;
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    Log.i(TAG, "end of list");
                    loadMore();
                }
            }
        });
    }

    private void loadMore() {
        setLoading(true);
        Date earliest = new Date();
        for (Post post : list) {
            if (post.getDate() != null && post.getDate().before(earliest))
                earliest = post.getDate();
        }

        if (mode.equals("Dashboard") || mode.equals("PurchaseHistory")) {
            API
                .getPosts(earliest, 30, mode.equals("Dashboard") ? API.PostFilter.all : API.PostFilter.unlocked)
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        setLoading(false);
                    }
                })
                .subscribe(new CallBack<List<Post>>() {
                    @Override
                    public void onSuccess(List<Post> posts) {
                        if (posts.size() == 0) {
                            ended = true;
                            return;
                        }
                        list.addAll(posts);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(ErrorResponse e) {

                    }

                    @Override
                    public void onException(Throwable e) {

                    }
                });
        } else if (mode.equals("Homepage")) {
            API.getPostsFromUser(user_id, earliest, 30)
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        setLoading(false);
                    }
                })
                .subscribe(new CallBack<List<Post>>() {
                    @Override
                    public void onSuccess(List<Post> posts) {
                        if (posts.size() == 0) {
                            ended = true;
                            return;
                        }
                        list.addAll(posts);
                        adapter.notifyDataSetChanged();
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

    private void update(final ViewHolder holder, final Post post) {
        // just unlocking
        holder.getView(R.id.blurring_view).setVisibility(View.INVISIBLE);
        Log.i(TAG, String.format("update post id: %d", post.getId()));
    }
}
