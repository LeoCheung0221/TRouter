package com.tufusi.feature.order;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tufusi.trouter.annotation.TRouter;

/**
 * Created by LeoCheung on 2020/11/20.
 *
 * @description
 */
@TRouter(path = "/order/Order_MainActivity")
public class Order_MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_main);
    }

    public void jumpApp(View view) {
    }

    public void jumpPersonal(View view) {
    }
}