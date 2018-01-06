package com.zyuco.maskbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.zyuco.maskbook.lib.CommonAdapter;
import com.zyuco.maskbook.lib.HideToolBarListener;

import com.zyuco.maskbook.lib.ViewHolder;
import com.zyuco.maskbook.model.Post;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PurchaseHistoryActivity extends AppCompatActivity {
    CommonAdapter<Post> adapter;
    List<Post> list = new ArrayList<>();
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        initList();
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView textView = findViewById(R.id.toolbar_title);
        textView.setText(R.string.menuitem_unlock);
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
        list.add(new Post());
        list.add(new Post());
        list.add(new Post());
        list.add(new Post());
        toolbar = findViewById(R.id.toolbar);
        adapter = new CommonAdapter<Post>(this, R.layout.post_item, list) {
            @Override
            public void convert(ViewHolder holder, Post data) {
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

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
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
        list.addOnScrollListener(onScrollListener);
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
}
