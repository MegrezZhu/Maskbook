package com.zyuco.maskbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;

import com.google.gson.Gson;
import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.User;
import com.zyuco.maskbook.service.APIServiceFactor;
import com.zyuco.maskbook.tool.SimpleObserver;

public class MainActivity extends AppCompatActivity {
    private Gson gson = new Gson();

    @Override
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

    }
}
