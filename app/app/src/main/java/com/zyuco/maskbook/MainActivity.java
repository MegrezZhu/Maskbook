package com.zyuco.maskbook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;

public class MainActivity extends AppCompatActivity {
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.textview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStorageDirectory() + "/1.jpeg");
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                API.getInstance()
                            .newPost(RequestBody.create(MultipartBody.FORM, String.valueOf(1.23)),
                                    RequestBody.create(MultipartBody.FORM, String.valueOf(123)),
                                    body,
                                    RequestBody.create(MultipartBody.FORM, ""))
                        .subscribe(new CallBack<Post>() {
                            @Override
                            public void onSuccess(Post post) {
                                Log.e("INFO", post.getDate().toString());
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
    }
}
