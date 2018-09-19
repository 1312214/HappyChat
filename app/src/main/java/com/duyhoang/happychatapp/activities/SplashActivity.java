package com.duyhoang.happychatapp.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.duyhoang.happychatapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private final long WAITING_TIME_MS = 3000;

    private Handler mWaitHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        mWaitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    if(FirebaseAuth.getInstance() != null) {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, LogInActivity.class));
                    }
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, WAITING_TIME_MS);

    }

    @Override
    protected void onDestroy() {
        mWaitHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
