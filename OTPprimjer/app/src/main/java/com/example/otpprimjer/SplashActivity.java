package com.example.otpprimjer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.otpprimjer.utils.FirebaseUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // prilikom pokretanja zadrzat cemo se 3000 ms na SplashActivity
        // a zatim cemo preci na LoginPhoneNumberAcitivity
        // uradjeno sve sa ovom funkcijom ispot !
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseUtil.isLogin()){

                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                }else {
                    startActivity(new Intent(SplashActivity.this, LoginPhoneNumberActivity.class));
                }
                finish();
            }
        },1000);



    }
}