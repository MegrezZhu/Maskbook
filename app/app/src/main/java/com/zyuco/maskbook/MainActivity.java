package com.zyuco.maskbook;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.Post;
import com.zyuco.maskbook.model.User;
import com.zyuco.maskbook.service.API;
import com.zyuco.maskbook.tool.CallBack;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private Gson gson = new Gson();
    com.gc.materialdesign.views.ButtonRectangle buttonSignup;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        API.init(this);
        final File file = new File(Environment.getExternalStorageDirectory() + "/1.jpeg");
        findViewById(R.id.regist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                API.regist("andiedie", "123456", "Andie", file)
                        .subscribe(new CallBack<User>() {
                            @Override
                            public void onSuccess(User user) {
                                Log.e("INFO", String.valueOf(user.getId()));
                            }

                            @Override
                            public void onFail(ErrorResponse e) {
                                Log.e("INFO", e.getMessage());
                            }

                            @Override
                            public void onException(Throwable e) {
                                Log.e("INFO", e.getMessage());
                            }
                        });
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                API.login("andiedie", "123456")
                        .subscribe(new CallBack<User>() {
                            @Override
                            public void onSuccess(User user) {
                                Log.e("INFO", String.valueOf(user.getId()));
                            }

                            @Override
                            public void onFail(ErrorResponse e) {
                                Log.e("INFO", e.getMessage());
                            }

                            @Override
                            public void onException(Throwable e) {
                                Log.e("INFO", e.getMessage());
                            }
                        });
            }
        });

        findViewById(R.id.post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                API.newPost(file, 1.23, "Hello", 123)
                        .subscribe(new CallBack<Post>() {
                            @Override
                            public void onSuccess(Post post) {
                                Log.e("INFO", String.valueOf(post.getId()));
                            }

                            @Override
                            public void onFail(ErrorResponse e) {
                                Log.e("INFO", e.getMessage());
                            }

                            @Override
                            public void onException(Throwable e) {
                                Log.e("INFO", e.getMessage());
                            }
                        });
            }
        });
        initListener();
    }

    private void initListener() {
        buttonSignup = findViewById(R.id.signup);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
