package com.zyuco.maskbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.User;
import com.zyuco.maskbook.service.API;
import com.zyuco.maskbook.tool.CallBack;

import io.reactivex.functions.Action;

public class LoginActivity extends AppCompatActivity {
    private final static String TAG = "Maskbook.login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: add background image (just like tumblr)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initListener();
        hideStatusbar();
    }

    private void initListener() {
        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        final EditText usernameEdit = findViewById(R.id.username);
        final EditText passwordEdit = findViewById(R.id.password);

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if (username.equals("")) {
                    Toast.makeText(LoginActivity.this, R.string.toast_empty_username, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.equals("")) {
                    Toast.makeText(LoginActivity.this, R.string.toast_empty_password, Toast.LENGTH_SHORT).show();
                    return;
                }
                login(username, password);
            }
        });
    }

    private void login(String username, String password) {
        final View loadingMask = findViewById(R.id.loading_mask);
        loadingMask.setVisibility(View.VISIBLE);
        API
            .login(username, password)
            .doOnTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    loadingMask.setVisibility(View.INVISIBLE);
                }
            })
            .subscribe(new CallBack<User>() {
                @Override
                public void onSuccess(User user) {
                    ((MaskbookApplication)getApplication()).setUser(user);
                    Log.i(TAG, String.format("login succeeded as %s.", user.getNickname()));
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFail(ErrorResponse e) {
                    Toast.makeText(LoginActivity.this, R.string.toast_incorrect_login, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onException(Throwable e) {
                    Log.e(TAG, "onException: ", e);
                }
            });
    }

    private void hideStatusbar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
