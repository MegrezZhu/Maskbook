package com.zyuco.maskbook;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;

import com.bumptech.glide.request.target.Target;
import com.fivehundredpx.android.blur.BlurringView;
import com.ldoublem.thumbUplib.ThumbUpView;

import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.zyuco.maskbook.lib.CommonAdapter;
import com.zyuco.maskbook.lib.ViewHolder;
import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.Post;
import com.zyuco.maskbook.model.User;
import com.zyuco.maskbook.service.API;
import com.zyuco.maskbook.service.APIService;
import com.zyuco.maskbook.tool.CallBack;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Action;
import retrofit2.Retrofit;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "Maskbook.dashborad";

    CommonAdapter<Post> adapter;
    List<Post> list = new ArrayList<>();

    SwipeRefreshLayout swipeRefresher;
    FloatingActionButton button_publish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initListener();
        initList();
        render();
        refreshPosts();
    }

    private void refreshPosts() {
        swipeRefresher.setRefreshing(true);
        API
            .getPosts()
            .doOnTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    swipeRefresher.setRefreshing(false);
                }
            })
            .subscribe(new CallBack<List<Post>>() {
                @Override
                public void onSuccess(List<Post> posts) {
                    list.clear();
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

    private void render() {
        User user = ((MaskbookApplication) getApplication()).getUser();
        ((TextView) findViewById(R.id.name)).setText(user.getNickname());
        try {
            URL url = new URL(APIService.BASE_URL);
            URL avatarUrl = new URL(url, user.getAvatar());
            GlideApp
                .with(this)
                .load(avatarUrl)
                .placeholder(R.mipmap.default_avatar)
                .into((ImageView) findViewById(R.id.avatar));
        } catch (MalformedURLException err) {
            Log.e(TAG, "render: ", err);
        }
    }

    private void initList() {
        adapter = new CommonAdapter<Post>(this, R.layout.post_item, list) {
            @Override
            public void convert(final ViewHolder holder, final Post post) {
                TextView name = holder.getView(R.id.name);
                name.setText(post.getAuthor().getNickname());
                TextView content = holder.getView(R.id.content);
                content.setText(post.getContent());
                final BlurringView blur = holder.getView(R.id.blurring_view);

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
                    .with(DashboardActivity.this)
                    .load(imageURL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            blur.invalidate();
                            if (post.getParameter() != 0) {
                                View image = holder.getView(R.id.image_wrapper);
                                blur.setVisibility(View.VISIBLE);
                                blur.setBlurRadius(post.getParameter().intValue());
                                blur.setBlurredView(image);
                            }
                            return false;
                        }
                    })
//                    .placeholder() TODO: image place holder
                    .into((ImageView) holder.getView(R.id.image));
                GlideApp
                    .with(DashboardActivity.this)
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


        RecyclerView list = findViewById(R.id.post_list);

        button_publish = findViewById(R.id.publish);
        button_publish.attachToRecyclerView(list);

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }


    private void initListener() {
        findViewById(R.id.homepage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.purchase_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, PurchaseHistoryActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();

                // back to login activity
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, PublishActivity.class);
                startActivity(intent);
            }
        });

        swipeRefresher = findViewById(R.id.swipe_refresh);
        swipeRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPosts();
            }
        });
    }

    private void logout() {
        // just clearing local cookies
        SharedPrefsCookiePersistor cookie = new SharedPrefsCookiePersistor(this);
        cookie.clear();
    }
}
