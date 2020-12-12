package com.tufusi.trouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.tufusi.trouter.annotation.Parameter;
import com.tufusi.trouter.api.manager.RouterManager;

public class MessageActivity extends AppCompatActivity {

    @Parameter(name = "username")
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
    }

    public void jumpMain(View view) {
        RouterManager.getInstance().build("/app/MainActivity")
                .withString("money", "200元")
                .navigation(this);
    }

    public void jumpLogin(View view) {
    }
}