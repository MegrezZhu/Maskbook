package com.zyuco.maskbook;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyuco.maskbook.lib.CommonAdapter;
import com.zyuco.maskbook.lib.HideToolBarListener;
import com.zyuco.maskbook.lib.ViewHolder;
import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.Post;
import com.zyuco.maskbook.model.User;
import com.zyuco.maskbook.service.API;
import com.zyuco.maskbook.service.APIService;
import com.zyuco.maskbook.tool.CallBack;
import com.zyuco.maskbook.tool.PostList;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Action;

public class HomepageActivity extends AppCompatActivity {
    PostList postList;
    Toolbar toolbar;
    private static final String TAG = "Maskbook.homepage";
    SwipeRefreshLayout swipeRefresher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        initListener();
        initList();
        getSomeonePosts();
        initToolbar();
    }

    private void initListener() {
        swipeRefresher = findViewById(R.id.swipe_refresh);
        swipeRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSomeonePosts();
            }
        });
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView textView = findViewById(R.id.toolbar_title);
        textView.setText(R.string.menuitem_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initList() {

        RecyclerView.OnScrollListener onScrollListener = new HideToolBarListener(this) {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }

            @Override
            public void onMoved(int distance) {
                moveViews(distance);
            }
        };

        postList = new PostList(this, "Homepage");
        postList.getRecyclerView().addOnScrollListener(onScrollListener);
    }

    private void hideViews() {
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(1));
    }

    private void showViews() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1));
    }

    private void moveViews(int distance) {
        toolbar.setTranslationY(-distance);
    }

    private void render() {
        if (postList.getDataList().isEmpty()) return;
        User user = postList.getDataList().get(0).getAuthor();
        TextView name_textView = findViewById(R.id.name);
        name_textView.setText(user.getNickname());

        if (user.getId() != ((MaskbookApplication) getApplication()).getUser().getId()) {
            TextView toolbar_textView = findViewById(R.id.toolbar_title);
            toolbar_textView.setText(user.getNickname() + "的主页");
        }

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

    private void getSomeonePosts() {
        User user = ((MaskbookApplication) getApplication()).getUser();
        API.getPostsFromUser(user.getId())
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

                            render();
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
