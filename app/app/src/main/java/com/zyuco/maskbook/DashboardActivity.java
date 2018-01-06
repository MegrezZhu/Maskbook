package com.zyuco.maskbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.fivehundredpx.android.blur.BlurringView;
import com.ldoublem.thumbUplib.ThumbUpView;

import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.zyuco.maskbook.lib.CommonAdapter;
import com.zyuco.maskbook.lib.ViewHolder;
import com.zyuco.maskbook.model.Post;
import com.zyuco.maskbook.model.User;
import com.zyuco.maskbook.service.APIService;

import org.w3c.dom.Text;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.Util;
import retrofit2.Retrofit;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "Maskbook.dashborad";

    CommonAdapter<Post> adapter;
    List<Post> list = new ArrayList<>();
    FloatingActionButton button_publish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initListener();
        initList();
        render();
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
        list.add(new Post());
        list.add(new Post());
        adapter = new CommonAdapter<Post>(this, R.layout.post_item, list) {
            @Override
            public void convert(ViewHolder holder, Post data) {
                TextView name = holder.getView(R.id.name);
                name.setText("123");
                TextView content = holder.getView(R.id.content);
                content.setText("咔咔");
              
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

                View image = holder.getView(R.id.image_wrapper);
                BlurringView blur = holder.getView(R.id.blurring_view);
                blur.setBlurredView(image);
            }
        };

        adapter.setOnItemClickListemer(new CommonAdapter.OnItemClickListener<Post>() {
            @Override
            public void onClick(int position, Post data) {
                initDialog();
            }

            @Override
            public void onLongClick(int position, Post data) {
            }
        });


        RecyclerView list = findViewById(R.id.post_list);

        button_publish = (FloatingActionButton) findViewById(R.id.publish);
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
    }

    private void logout() {
        // just clearing local cookies
        SharedPrefsCookiePersistor cookie = new SharedPrefsCookiePersistor(this);
        cookie.clear();
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        LayoutInflater inflater  = DashboardActivity.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_conetnt, null);

        final TextView textView = view.findViewById(R.id.click_time);
        view.findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.parseInt(textView.getText().toString()) + 1;
                textView.setText(String.valueOf(num));

                YoYo.with(Techniques.BounceIn)
                        .duration(500)
                        .repeat(1)
                        .playOn(textView);
            }
        });

        builder.setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create().show();
    }

}
