package com.zyuco.maskbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.User;
import com.zyuco.maskbook.service.API;
import com.zyuco.maskbook.tool.CallBack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.functions.Action;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "Maskbook.signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: use scroller layout instead

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initListener();
    }

    private void initListener() {
        findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegistButtonClicked();
            }
        });
    }

    private void onRegistButtonClicked() {
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        String repassword = ((EditText) findViewById(R.id.repassword)).getText().toString();
        String nickname = ((EditText) findViewById(R.id.nickname)).getText().toString();
        // TODO: get selected avatar file

        if (!checkInputFormat(username, password, repassword, nickname)) return;

        final View loadingMask = findViewById(R.id.loading_mask);
        loadingMask.setVisibility(View.VISIBLE);
        API
            .regist(username, password, nickname, null)
            .doOnTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    loadingMask.setVisibility(View.INVISIBLE);
                }
            })
            .subscribe(new CallBack<User>() {
                @Override
                public void onSuccess(User user) {
                    // regist ok
                    Intent intent = new Intent(SignupActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    Toast.makeText(SignupActivity.this, R.string.toast_signup_success, Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFail(ErrorResponse e) {
                    Log.w(TAG, String.format("regist failed, code: %s", e.getErrno().name()));
                    Toast.makeText(SignupActivity.this, R.string.toast_unknown_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onException(Throwable e) {
                    Log.e(TAG, "regist error", e);
                    Toast.makeText(SignupActivity.this, R.string.toast_network_error, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private boolean checkInputFormat(String username, String password, String repassword, String nickname) {
        if (username.equals("") || password.equals("") || nickname.equals("") || repassword.equals("")) {
            Toast.makeText(this, R.string.toast_no_empty_field, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(repassword)) {
            Toast.makeText(this, R.string.toast_no_empty_field, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username.length() < 6 || username.length() > 20) {
            Toast.makeText(this, R.string.toast_invalid_username, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (nickname.length() < 2 || nickname.length() > 20) {
            Toast.makeText(this, R.string.toast_invalid_nickname, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6 || password.length() > 20) {
            Toast.makeText(this, R.string.toast_invalid_password, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
