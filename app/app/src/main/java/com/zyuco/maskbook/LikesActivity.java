package com.zyuco.maskbook;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.zyuco.maskbook.lib.HideToolBarListener;
import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.Post;
import com.zyuco.maskbook.service.API;
import com.zyuco.maskbook.tool.CallBack;
import com.zyuco.maskbook.tool.PostList;

import java.util.Date;
import java.util.List;

import io.reactivex.functions.Action;

public class LikesActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private PostList postList;

    private SwipeRefreshLayout swipeRefresher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);

        initListener();
        initList();
        initToolbar();
        getLikes();
//        hideStatusbar();
    }

    private void initListener() {
        swipeRefresher = findViewById(R.id.swipe_refresh);
        swipeRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLikes();
            }
        });
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView textView = findViewById(R.id.toolbar_title);
        textView.setText(R.string.menuitem_likes);
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

    private void hideViews() {
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(1));
    }

    private void showViews() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1));
    }

    private void moveViews(int distance) {
        toolbar.setTranslationY(-distance);
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

        postList = new PostList(this, "Likes", -1);
        postList.getRecyclerView().addOnScrollListener(onScrollListener);
    }

    private void getLikes() {
        swipeRefresher.setRefreshing(true);
        postList.setLoading(true);
        API.getLike()
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

    private void hideStatusbar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
