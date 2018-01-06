package com.zyuco.maskbook;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ldoublem.thumbUplib.ThumbUpView;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.zyuco.maskbook.lib.CommonAdapter;
import com.zyuco.maskbook.lib.ViewHolder;
import com.zyuco.maskbook.model.Post;
import com.zyuco.maskbook.model.User;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    CommonAdapter<Post> adapter;
    List<Post> list = new ArrayList<>();
    LinearLayout button_homepage;
    LinearLayout button_purchase_history;
    LinearLayout button_about;
    LinearLayout button_logout;
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
        User user = ((MaskbookApplication)getApplication()).getUser();
        ((TextView)findViewById(R.id.name)).setText(user.getNickname());
        // TODO: set avatar url
    }

    private void initList() {
        list.add(new Post());
        list.add(new Post());
        adapter = new CommonAdapter<Post>(this, R.layout.post_item, list) {
            @Override
            public void convert(ViewHolder holder, Post data) {
//                TextView name = holder.getView(R.id.name);
//                name.setText(data.name);
//                TextView belong = holder.getView(R.id.belong);
//                belong.setText(data.belong);
//                TextView description = holder.getView(R.id.abstract_description);
//                description.setText("\t\t\t\t" + data.abstractDescription);
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
}
