package com.example.lonelywolf.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 */
open class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //添加activity到活动管理器
        ActivityCollector.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        //从activity的活动管理器清除
        ActivityCollector.removeActivity(this)
    }
}