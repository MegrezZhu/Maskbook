package com.zyuco.maskbook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.ErrorResponseCode;
import com.zyuco.maskbook.model.User;
import com.zyuco.maskbook.service.API;
import com.zyuco.maskbook.tool.CallBack;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "Maskbook.splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        API.init(this);

        API.logout() // for debug
            .subscribe(
                new Action() {
                    @Override
                    public void run() throws Exception {
                        checkLogin();
                    }
                }
            );
    }

    private void finishSplashAndGo(Class<? extends Activity> cls) {
        Intent intent = new Intent(this, cls);
        this.startActivity(intent);
        this.finish();
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void checkLogin() {
        API.getUserInformation()
            .subscribe(new CallBack<User>() {
                @Override
                public void onSuccess(User user) {
                    // TODO: persistence user info
                    Log.i(TAG, String.format("already logged in as %s.", user.getNickname()));
                    finishSplashAndGo(MainActivity.class);
                }

                @Override
                public void onFail(ErrorResponse err) {
                    Log.i(TAG, String.format("onFail: %s", err.getErrno().name()));
                    if (err.getErrno() == ErrorResponseCode.Login_Required) {
                        Log.i(TAG, "not logged in.");
                        finishSplashAndGo(LoginActivity.class);
                        return;
                    }
                    Toast.makeText(SplashActivity.this, R.string.toast_network_error, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onFail: network error");
                }

                @Override
                public void onException(Throwable e) {

                }
            });
    }
}
