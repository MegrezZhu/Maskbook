package com.zyuco.maskbook;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.melnykov.fab.FloatingActionButton;
import com.zyuco.maskbook.lib.CommonAdapter;
import com.zyuco.maskbook.lib.URLFormatter;
import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.Post;
import com.zyuco.maskbook.model.User;
import com.zyuco.maskbook.service.API;
import com.zyuco.maskbook.service.APIService;
import com.zyuco.maskbook.tool.CallBack;
import com.zyuco.maskbook.tool.PostList;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import io.reactivex.functions.Action;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "Maskbook.dashboard";

    SwipeRefreshLayout swipeRefresher;
    FloatingActionButton button_publish;
    PostList postList;
    TextView powerTextView;
    TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initListener();
        initList();
        render();
        refreshPosts();

//        hideStatusbar();
    }

    private void refreshPosts() {
        swipeRefresher.setRefreshing(true);
        postList.setLoading(true);
        API
            .getPosts()
            .doOnTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    swipeRefresher.setRefreshing(false);
                    postList.setLoading(false);
                }
            })
            .subscribe(new CallBack<List<Post>>() {
                @Override
                public void onSuccess(List<Post> posts) {
                    List<Post> list = postList.getDataList();
                    list.clear();
                    list.addAll(posts);
                    postList.getAdapter().notifyDataSetChanged();
                    postList.resetEnded();
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
        nameTextView.setText(user.getNickname());
        powerTextView.setText(String.format(getString(R.string.power), user.getPower()));
        try {
            URL url = new URL(APIService.BASE_URL);
            URL avatarUrl = new URL(url, user.getAvatar());
            GlideApp
                    .with(DashboardActivity.this)
                    .load(avatarUrl)
                    .placeholder(R.mipmap.default_avatar)
                    .into((ImageView) findViewById(R.id.avatar));
        } catch (MalformedURLException err) {
            Log.e(TAG, "render: ", err);
        }
        ((DrawerLayout) findViewById(R.id.main_drawer_layout)).addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                updateUserInfo();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        // update header image
        User self = ((MaskbookApplication) getApplication()).getUser();
        final BlurringView blur = findViewById(R.id.header_blurring_view);
        blur.setBlurredView(findViewById(R.id.header_background_wrapper));
        API
                .getHeaderPostFromUser(self.getId())
                .subscribe(new CallBack<Post>() {
                    @Override
                    public void onSuccess(Post post) {
                        GlideApp
                                .with(DashboardActivity.this)
                                .load(URLFormatter.formatImageURL(post.getImage()))
                                .placeholder(R.drawable.bg)
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
                                .into((ImageView) findViewById(R.id.header_background));
                    }

                    @Override
                    public void onFail(ErrorResponse e) {

                    }

                    @Override
                    public void onException(Throwable e) {

                    }
                });
    }

    private void initList() {
        postList = new PostList(this, "Dashboard", -1);
        button_publish = findViewById(R.id.publish);
        button_publish.attachToRecyclerView(postList.getRecyclerView());
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

        findViewById(R.id.likes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, LikesActivity.class);
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

        powerTextView = findViewById(R.id.power);
        nameTextView = findViewById(R.id.name);
    }

    private void logout() {
        // just clearing local cookies
        SharedPrefsCookiePersistor cookie = new SharedPrefsCookiePersistor(this);
        cookie.clear();
    }

    private void updateUserInfo() {
        User user = ((MaskbookApplication) getApplication()).getUser();
        powerTextView.setText(String.format(getString(R.string.power), user.getPower()));
    }

    private void hideStatusbar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

}
