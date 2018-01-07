package com.zyuco.maskbook.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.INotificationSideChannel;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.test.mock.MockApplication;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import io.reactivex.functions.Action;

public class PostList {
    private static final String TAG = "Maskbook.postlist";

    private Activity context;
    private CommonAdapter<Post> adapter;
    private List<Post> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private User self;

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
        this.self = ((MaskbookApplication) context.getApplication()).getUser();
        this.user_id = user_id == -1 ? this.self.getId() : user_id;

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

        adapter
                .setOnItemClickListemer(new CommonAdapter.OnItemClickListener<Post>() {
                    @Override
                    public void onClick(int position, Post data) {
                        if (!data.getUnlock() && data.getParameter() != 0 && !self.getId().equals(data.getAuthor().getId())) {
                            initDialog(data, position);
                        }
                    }

                    @Override
                    public void onLongClick(int position, Post data) {

                    }
                });
    }

    private void initDialog(final Post post, final int posInList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = this.context.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_content, null);
        final AlertDialog dialog = builder.setView(view).create();

        final CircularProgressButton unlockButton = view.findViewById(R.id.unlock);
        final TextView cost = view.findViewById(R.id.power_cost);
        final TextView times = view.findViewById(R.id.click_time);

        int clickTimes = 0, powerCost = 0;
        powerCost = Math.min(self.getPower(), post.getPrice());
        clickTimes = Math.max(0, post.getPrice() - self.getPower());
        cost.setText(String.valueOf(powerCost));
        times.setText(String.valueOf(clickTimes));
        unlockButton.setEnabled(clickTimes == 0);
        unlockButton.setAlpha(unlockButton.isEnabled() ? 1.0f : 0.2f);

        view.findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Math.max(0, Integer.valueOf(times.getText().toString()) - 1);
                times.setText(String.valueOf(num));

                YoYo.with(Techniques.BounceIn)
                        .duration(500)
                        .playOn(times);

                if (num == 0) {
                    unlockButton.setEnabled(true);
                    unlockButton.setAlpha(1.0f);
                }
            }
        });

        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockButton.startAnimation();

                API
                        .unlockPost(post.getId())
                        .doOnTerminate(new Action() {
                            @Override
                            public void run() throws Exception {
                                unlockButton.revertAnimation();
                            }
                        })
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                dialog.dismiss();
                                updateUserInfo();
                                post.setUnlock(true);
                                adapter.notifyItemChanged(posInList, true);
                            }
                        });
            }
        });

        dialog.show();
    }

    private void updateUserInfo () {
        API.getUserInformation()
                .subscribe(new CallBack<User>() {
                    @Override
                    public void onSuccess(User user) {
                        PostList.this.self = user;
                        ((MaskbookApplication) context.getApplication()).setUser(user);
                    }

                    @Override
                    public void onFail(ErrorResponse e) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onException(Throwable e) {
                        Log.e(TAG, e.getMessage());
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
