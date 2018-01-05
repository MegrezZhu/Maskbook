package com.zyuco.maskbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.zyuco.maskbook.lib.CommonAdapter;
import com.zyuco.maskbook.lib.ViewHolder;
import com.zyuco.maskbook.model.Post;

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
        button_homepage = findViewById(R.id.homepage);
        button_homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        button_purchase_history = findViewById(R.id.purchase_history);
        button_purchase_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, PurchaseHistoryActivity.class);
                startActivity(intent);
            }
        });

        button_logout = findViewById(R.id.logout);
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 调用api注销
            }
        });

        button_publish = findViewById(R.id.publish);
        button_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, PublishActivity.class);
                startActivity(intent);
            }
        });
    }


}
