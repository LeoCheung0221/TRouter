package com.tufusi.trouter

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tufusi.trouter.annotation.Parameter
import com.tufusi.trouter.annotation.TRouter


@TRouter(path = "/app/MainActivity")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @JvmField
    @Parameter(name = "username")
    var name: String? = null

    @JvmField
    @Parameter()
    var password: String? = null

    fun jumpOrder(view: View) {
//        val group: TRouterGroupLoad = `TRouter$$Group$$order`()
//        val map: Map<String, Class<out TRouterPathLoad?>> =
//            group.loadGroup()
//
//        // 通过order组名获取对应路由路径对象
//        // 通过order组名获取对应路由路径对象
//        val clazz: Class<out TRouterPathLoad?>? = map["order"]
//
//        try {
//            // 类加载动态加载路由路径对象
//            val path: `TRouter$$Path$$order`? = clazz!!.newInstance() as `TRouter$$Path$$order`?
//            val pathMap: Map<String, RouterBean> = path.loadPath()
//            // 获取目标对象封装
//            val bean = pathMap["/order/Order_MainActivity"]
//            if (bean != null) {
//                val intent = Intent(this, bean.clazz)
//                intent.putExtra("name", "simon")
//                startActivity(intent)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    fun jumpLogin(view: View) {

    }

    fun jumpMessage(view: View) {
        val intent = Intent(this, MessageActivity::class.java)
        startActivity(intent)
    }
}