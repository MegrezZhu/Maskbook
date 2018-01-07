package com.zyuco.maskbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AtomicFile;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.User;
import com.zyuco.maskbook.service.API;
import com.zyuco.maskbook.tool.CallBack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.functions.Action;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "Maskbook.signup";

    private String avatarPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: use scroller layout instead

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initListener();
        hideStatusbar();
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

        findViewById(R.id.avatar_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: clicked");
                chooseImage();
            }
        });
    }

    private void onRegistButtonClicked() {
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        String nickname = ((EditText) findViewById(R.id.nickname)).getText().toString();

        if (!checkInputFormat(username, password, nickname)) return;

        File avatar = avatarPath != null ? new File(avatarPath) : null;

        final View loadingMask = findViewById(R.id.loading_mask);
        loadingMask.setVisibility(View.VISIBLE);
        API
            .regist(username, password, nickname, avatar)
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
                    ((MaskbookApplication)getApplication()).setUser(user);
                    Intent intent = new Intent(SignupActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    Toast.makeText(SignupActivity.this, R.string.toast_signup_success, Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFail(ErrorResponse e) {
                    switch (e.getErrno()) {
                        case Duplicate_Nickname:
                            Toast.makeText(SignupActivity.this, R.string.toast_duplicate_nickname, Toast.LENGTH_SHORT).show();
                            break;
                        case Duplicate_Username:
                            Toast.makeText(SignupActivity.this, R.string.toast_duplicate_username, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Log.w(TAG, String.format("regist failed, code: %s", e.getErrno().name()));
                            Toast.makeText(SignupActivity.this, R.string.toast_unknown_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onException(Throwable e) {
                    Log.e(TAG, "regist error", e);
                    Toast.makeText(SignupActivity.this, R.string.toast_network_error, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private boolean checkInputFormat(String username, String password, String nickname) {
        if (username.equals("") || password.equals("") || nickname.equals("")) {
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

    private void chooseImage() {
        PictureSelector
                .create(SignupActivity.this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(1)// 最大图片选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .enableCrop(true)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(true)// 是否开启点击声音 true or false
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

                    // 选取头像的路径
                    if (!selectList.isEmpty()) {
                        LocalMedia localMedia = selectList.get(0);
                        if (localMedia.isCut()) {
                            if (localMedia.isCompressed()) {
                                avatarPath = localMedia.getCompressPath();
                            } else {
                                avatarPath = localMedia.getCutPath();
                            }
                        } else {
                            avatarPath = localMedia.getPath();
                        }
                        Glide.with(SignupActivity.this).load(new File(avatarPath)).into((ImageView) findViewById(R.id.avatar));
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PictureFileUtils.deleteCacheDirFile(this);
    }

    private void hideStatusbar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
