package com.zyuco.maskbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SignupActivity extends AppCompatActivity {
    com.gc.materialdesign.views.ButtonFlat buttonSignin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initListener();
    }

    private void initListener() {
        buttonSignin = findViewById(R.id.signin);

        buttonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                SignupActivity.this.finish();
            }
        });
    }
}
