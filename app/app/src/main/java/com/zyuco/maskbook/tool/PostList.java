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
import com.zyuco.maskbook.DashboardActivity;
import com.zyuco.maskbook.GlideApp;
import com.zyuco.maskbook.HomepageActivity;
import com.zyuco.maskbook.MaskbookApplication;
import com.zyuco.maskbook.R;
import com.zyuco.maskbook.lib.CommonAdapter;
import com.zyuco.maskbook.lib.ViewHolder;
import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.Post;
import com.zyuco.maskbook.service.API;
import com.zyuco.maskbook.service.APIService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public PostList(final Activity context, String mode) {
        this.context = context;
        this.mode = mode;

        adapter = new CommonAdapter<Post>(context, R.layout.post_item, list) {
            @Override
            public void convert(final ViewHolder holder, final Post post) {
                PostList.this.convert(holder, post);
            }
        };

        adapter.setOnItemClickListemer(new CommonAdapter.OnItemClickListener<Post>() {
            @Override
            public void onClick(int position, Post data) {
            }

            @Override
            public void onLongClick(int position, Post data) {
            }
        });

        recyclerView = context.findViewById(R.id.post_list);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);

//        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (loading || ended) return;
//                int visibleItemCount = layoutManager.getChildCount();
//                int totalItemCount = layoutManager.getItemCount();
//                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
//                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
//                    Log.i(TAG, "end of list");
//                    loadMore();
//                }
//            }
//        };

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

//        recyclerView.setOnScrollListener(scrollListener);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void loadMore() {
        setLoading(true);
        Date earliest = new Date();
        for (Post post : list) {
            if (post.getDate().before(earliest)) earliest = post.getDate();
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
            int id = ((MaskbookApplication) context.getApplication()).getUser().getId();
            API.getPostsFromUser(id, earliest, 30)
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

    private void convert(final ViewHolder holder, final Post post) {
        TextView name = holder.getView(R.id.name);
        name.setText(post.getAuthor().getNickname());
        final TextView content = holder.getView(R.id.content);
        content.setText(post.getContent());
        final BlurringView blur = holder.getView(R.id.blurring_view);
        final ImageView image = holder.getView(R.id.image);
        image.layout(0, 0, 0, 0);

        // image loading
        Log.i(TAG, String.format("image url: %s", post.getImage()));
        URL imageURL, avatarURL;
        try {
            imageURL = new URL(new URL(APIService.BASE_URL), post.getImage());
            avatarURL = new URL(new URL(APIService.BASE_URL), post.getAuthor().getAvatar());
        } catch (MalformedURLException e) {
            Log.e(TAG, "convert: ", e);
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
//                            blur.invalidate();
//                            if (post.getParameter() != 0) {
//                                View image = holder.getView(R.id.image_wrapper);
//                                blur.setVisibility(View.VISIBLE);
//                                blur.setBlurRadius(post.getParameter().intValue());
//                                blur.setBlurredView(image);
//                            }
                    return false;
                }
            })
            .fitCenter()
            .placeholder(R.drawable.placeholder)
            .into(image);
        GlideApp
            .with(context)
            .load(avatarURL)
            .placeholder(R.mipmap.default_avatar)
            .into((ImageView) holder.getView(R.id.avatar));

        final TextView like_num = holder.getView(R.id.like_num);
        like_num.setText("10");//点赞数目
        ThumbUpView tpv = holder.getView(R.id.tpv);//点赞
        tpv.setUnLikeType(ThumbUpView.LikeType.broken);
        tpv.setCracksColor(Color.WHITE);
        tpv.setFillColor(Color.rgb(11, 200, 77));
        tpv.setEdgeColor(Color.rgb(33, 3, 219));
        tpv.setOnThumbUp(new ThumbUpView.OnThumbUp() {
            @Override
            public void like(boolean like) {
                if (like) {
                    like_num.setText(String.valueOf(Integer.valueOf(like_num.getText().toString()) + 1));
                } else {
                    like_num.setText(String.valueOf(Integer.valueOf(like_num.getText().toString()) - 1));

                }
            }
        });

       holder.getView(R.id.avatar_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(context, HomepageActivity.class);
                intent.putExtra("user", post.getAuthor());
                context.startActivity(intent);
            }
        });
    }
}