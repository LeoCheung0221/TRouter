package com.tufusi.feature.login;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tufusi.trouter.annotation.Parameter;
import com.tufusi.trouter.annotation.TRouter;

/**
 * Created by LeoCheung on 2020/11/20.
 *
 * @description
 */
@TRouter(path = "/login/Login_MainActivity")
public class Login_MainActivity extends AppCompatActivity {

    @Parameter(name = "username")
    String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
    }

    public void jumpApp(View view) {
    }

    public void jumpOrder(View view) {
    }
}