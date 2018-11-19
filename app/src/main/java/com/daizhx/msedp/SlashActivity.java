package com.daizhx.msedp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.daizhx.msedp.login.LoginActivity;

public class SlashActivity extends AppCompatActivity {

    //是否已登录
    boolean isSignIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isSignIn) {
                    startActivity(new Intent(SlashActivity.this, MainActivity.class));
                }else{
                    startActivity(new Intent(SlashActivity.this, LoginActivity.class));
                }
                finish();
            }
        },3000);
        //TODO 获取登录状态

    }
}
