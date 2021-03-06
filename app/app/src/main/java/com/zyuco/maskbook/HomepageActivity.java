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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyuco.maskbook.lib.CommonAdapter;
import com.zyuco.maskbook.lib.HideToolBarListener;
import com.zyuco.maskbook.lib.URLFormatter;
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
import io.reactivex.functions.Consumer;

public class HomepageActivity extends AppCompatActivity {
    PostList postList;
    Toolbar toolbar;
    private static final String TAG = "Maskbook.homepage";
    SwipeRefreshLayout swipeRefresher;
    User user;
    User self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        user = (User) HomepageActivity.this.getIntent().getSerializableExtra("user");
        self = ((MaskbookApplication) getApplication()).getUser();
        initListener();
        initList();
        getSomeonePosts();
        initToolbar();
        render();
//        hideStatusbar();
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
        toolbar = findViewById(R.id.toolbar);
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

        postList = new PostList(this, "Homepage", isMine() ? -1 : user.getId());
        postList.getRecyclerView().addOnScrollListener(onScrollListener);
        postList.getRecyclerView().setNestedScrollingEnabled(true);
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

    private boolean isMine() {
        if (user == null) return true;
        return user.getId().equals(self.getId());
    }


    private void render() {
        boolean ismine = isMine();
        User u = ismine ? self : user;

        if (!ismine) {
            TextView toolbar_textView = findViewById(R.id.toolbar_title);
            toolbar_textView.setText(u.getNickname() + "'s Page");
        }
    }

    private void getSomeonePosts() {
        swipeRefresher.setRefreshing(true);
        postList.setLoading(true);
        final User u = isMine() ? self : user;
        API.getPostsFromUser(u.getId())
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
                    Post fake = new Post();
                    fake.setId(-1);
                    fake.setAuthor(u);
                    list.add(fake);
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

    private void hideStatusbar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

}
