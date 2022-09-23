package com.example.payez;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SpalshActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME_OUT=2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_spalsh);
        new Handler().postDelayed(() -> {
            Intent i=new Intent(SpalshActivity.this,
                    MainActivity.class);

            startActivity(i);

            finish();
        }, SPLASH_SCREEN_TIME_OUT);
    }
}