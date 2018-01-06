package com.zyuco.maskbook;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fivehundredpx.android.blur.BlurringView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.Post;
import com.zyuco.maskbook.service.API;
import com.zyuco.maskbook.tool.CallBack;

import java.io.File;
import java.util.List;

import io.reactivex.functions.Action;

public class PublishActivity extends AppCompatActivity {
    private static final String TAG = "Maskbook.publish";

    private BlurringView blur;
    private int radius = 0;
    private File selectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        initListener();
        initBlurringView();
        initSeekbar();
        initToolbar();
    }

    private void initSeekbar() {
        SeekBar bar = findViewById(R.id.seekbar);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateBlur((int) Math.floor((double) progress / seekBar.getMax() * 25.f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initBlurringView() {
        blur = findViewById(R.id.blurring_view);
        View blurredView = findViewById(R.id.blurred_view);

        blur.setBlurredView(blurredView);
    }

    private void updateBlur(int radius) {
        this.radius = radius;
        if (radius == 0) {
            blur.setVisibility(View.GONE);
        } else {
            blur.setVisibility(View.VISIBLE);
            blur.setBlurRadius(radius);
            blur.invalidate();
        }
    }

    private void initListener() {
        final ImageView imagePicker = findViewById(R.id.imagepick);
        final ImageView imagePreview = findViewById(R.id.image_preview);

        findViewById(R.id.publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post();
            }
        });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        };
        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 还原原来的加号
                findViewById(R.id.imagepick).setVisibility(View.VISIBLE);
                blur.setVisibility(View.GONE);
                // 顺便清除包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
                PictureFileUtils.deleteCacheDirFile(PublishActivity.this);
                imagePreview.setImageResource(0);
                return true;
            }
        };

        imagePicker.setOnClickListener(clickListener);
        imagePicker.setOnLongClickListener(longClickListener);
        imagePreview.setOnClickListener(clickListener);
        imagePreview.setOnLongClickListener(longClickListener);
    }

    private void chooseImage() {
        PictureSelector
            .create(PublishActivity.this)
            .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .maxSelectNum(1)// 最大图片选择数量 int
            .imageSpanCount(4)// 每行显示个数 int
            .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
            .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
            .openClickSound(true)// 是否开启点击声音 true or false
            .compress(false)
            .enableCrop(false)
            .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    private void post() {
        String content = ((EditText) findViewById(R.id.content)).getText().toString();
        String price = ((EditText)findViewById(R.id.price)).getText().toString();
        int iPrice;
        if (selectedFile == null) {
            Toast.makeText(this, R.string.toast_missing_image, Toast.LENGTH_SHORT).show();
            return;
        }
        if (price.equals("")) {
            Toast.makeText(this, R.string.toast_invalid_price, Toast.LENGTH_SHORT).show();
            return;
        } else {
            iPrice = Integer.parseInt(price);
            if (iPrice < 0 || iPrice > 100) {
                Toast.makeText(this, R.string.toast_invalid_price, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        final View loadingMask = findViewById(R.id.loading_mask);
        loadingMask.setVisibility(View.VISIBLE);

        API
            .newPost(selectedFile, radius, content, iPrice)
            .doOnTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    loadingMask.setVisibility(View.INVISIBLE);
                }
            })
            .subscribe(new CallBack<Post>() {
                @Override
                public void onSuccess(Post post) {
                    // TODO: add post to dashboard list
                    Log.i(TAG, "post succeeded.");

                    Toast.makeText(PublishActivity.this, R.string.toast_post_success, Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFail(ErrorResponse e) {
                    Log.e(TAG, String.format("unknown error: %s", e.getErrno().name()));
                    Toast.makeText(PublishActivity.this, R.string.toast_unknown_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onException(Throwable e) {
                    Log.e(TAG, "post error", e);
                    Toast.makeText(PublishActivity.this, R.string.toast_network_error, Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);

                    String path;
                    if (!selectList.isEmpty()) {
                        LocalMedia localMedia = selectList.get(0);
                        if (localMedia.isCut()) {
                            if (localMedia.isCompressed()) {
                                path = localMedia.getCompressPath();
                            } else {
                                path = localMedia.getCutPath();
                            }
                        } else {
                            path = localMedia.getPath();
                        }
                        selectedFile = new File(path);

                        // load image & refresh blur effect
                        findViewById(R.id.imagepick).setVisibility(View.GONE);
                        GlideApp
                            .with(PublishActivity.this)
                            .load(selectedFile)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    // image loaded
                                    updateBlur(radius);
                                    return false;
                                }
                            })
                            .into((ImageView) findViewById(R.id.image_preview));
                    }
                    break;
            }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView textView = findViewById(R.id.toolbar_title);
        textView.setText(R.string.toolbar_publish);
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

    private void getInfo() {

    }
}
