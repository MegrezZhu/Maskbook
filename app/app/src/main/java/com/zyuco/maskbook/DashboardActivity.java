package com.zyuco.maskbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.melnykov.fab.FloatingActionButton;
import com.zyuco.maskbook.lib.CommonAdapter;
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
    private static final String TAG = "Maskbook.dashborad";

    SwipeRefreshLayout swipeRefresher;
    FloatingActionButton button_publish;
    PostList postList;

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
        postList = new PostList(this);
        button_publish = findViewById(R.id.publish);
        button_publish.attachToRecyclerView(postList.getRecyclerView());

        postList
            .getAdapter()
            .setOnItemClickListemer(new CommonAdapter.OnItemClickListener<Post>() {
                @Override
                public void onClick(int position, Post data) {
                    User self = ((MaskbookApplication) getApplication()).getUser();
                    if (!data.getUnlock() && data.getParameter() != 0 && self.getId().intValue() != data.getAuthor().getId().intValue()) {
                        initDialog(data, position);
                    }
                }

                @Override
                public void onLongClick(int position, Post data) {

                }
            });
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

    private void initDialog(final Post post, final int posInList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        LayoutInflater inflater = DashboardActivity.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_content, null);
        final AlertDialog dialog = builder.setView(view).create();

        final CircularProgressButton unlockButton = view.findViewById(R.id.unlock);
        final TextView times = view.findViewById(R.id.click_time);

        times.setText(post.getPrice().toString());
        unlockButton.setEnabled(false);
        unlockButton.setAlpha(0.5f);
        view.findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Math.max(0, Integer.parseInt(times.getText().toString()) - 1);
                times.setText(String.valueOf(num));

                YoYo.with(Techniques.BounceIn)
                    .duration(500)
                    .repeat(1)
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
                            unlockButton.revertAnimation();
                            dialog.dismiss();

                            post.setUnlock(true);
                            postList.getAdapter().notifyItemChanged(posInList, true);
                        }
                    });
            }
        });

        dialog.show();
    }

}
