package com.tufusi.trouter.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.tufusi.trouter.annotation.TRouter

@TRouter(path = "/app/MainActivity")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun jumpOrder(view: View) {}
    fun jumpLogin(view: View) {}
}