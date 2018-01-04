package com.zyuco.maskbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.User;
import com.zyuco.maskbook.service.APIServiceFactor;
import com.zyuco.maskbook.tool.SimpleObserver;

public class MainActivity extends AppCompatActivity {
    private Gson gson = new Gson();
    com.gc.materialdesign.views.ButtonRectangle buttonSignup;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        APIServiceFactor.getInstance().get().subscribe(new SimpleObserver<User>() {
            @Override
            public void onSuccess(User user) {
                Log.i("TAG", user.getNickname());
            }

            @Override
            public void onFail(ErrorResponse e) {
                Log.i("TAG", e.getMessage());
            }

            @Override
            public void onException(Throwable e) {
                Log.e("TAG", e.getMessage());
            }
        });
        initListener();
    }

    private void initListener() {
        buttonSignup = findViewById(R.id.signup);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });
    }
}
