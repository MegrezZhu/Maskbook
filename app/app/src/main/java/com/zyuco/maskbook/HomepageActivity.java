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
        render();
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

        User user = (User) HomepageActivity.this.getIntent().getSerializableExtra("user");
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
        User user = (User) HomepageActivity.this.getIntent().getSerializableExtra("user");
        if (user == null) return true;
        return user.getId().equals(((MaskbookApplication) getApplication()).getUser().getId());
    }


    private void render() {
        boolean ismine = isMine();
        User user = ismine ? ((MaskbookApplication) getApplication()).getUser() : (User)getIntent().getSerializableExtra("user");

        if (!ismine) {
            TextView toolbar_textView = findViewById(R.id.toolbar_title);
            toolbar_textView.setText(user.getNickname() + "'s Page");
        }
    }

    private void getSomeonePosts() {
        final User user = isMine() ? ((MaskbookApplication) getApplication()).getUser() : (User)getIntent().getSerializableExtra("user");
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
                            Post fake = new Post();
                            fake.setId(-1);
                            fake.setAuthor(user);
                            list.add(fake);
                            list.addAll(posts);
                            postList.getAdapter().notifyDataSetChanged();
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
