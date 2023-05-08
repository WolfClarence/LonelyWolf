package com.example.lonelywolf.otherActivities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import com.example.lonelywolf.R
import com.example.lonelywolf.base.MyActivity
import kotlinx.android.synthetic.main.activity_home_page.*
import java.io.Reader
import android.view.LayoutInflater
import android.view.View

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 * 主页
 */
class HomePageActivity : MyActivity() {
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        overridePendingTransition(0, 0)//覆盖即将到来的跳转动画

        start_game.setOnClickListener {
            val intent = Intent(this, StageChooseActivity::class.java)
            startActivity(intent)
        }

        help.setOnClickListener {
            start_game.visibility = View.INVISIBLE
            help.visibility = View.INVISIBLE
            more.visibility = View.INVISIBLE
            helpView.visibility = View.VISIBLE
        }

        close.setOnClickListener {
            start_game.visibility = View.VISIBLE
            help.visibility = View.VISIBLE
            more.visibility = View.VISIBLE
            helpView.visibility = View.INVISIBLE
        }

        more.setOnClickListener {
            moreText.text = "制作团队：宋宇轩，左天伦，孟辰宇，周柏均，张皓\n联系方式：2063980370@qq.com"
            start_game.visibility = View.INVISIBLE
            help.visibility = View.INVISIBLE
            more.visibility = View.INVISIBLE
            moreView.visibility = View.VISIBLE
        }

        close_more.setOnClickListener {
            start_game.visibility = View.VISIBLE
            help.visibility = View.VISIBLE
            more.visibility = View.VISIBLE
            moreView.visibility = View.INVISIBLE
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 监控返回键
            AlertDialog.Builder(this).setTitle("提示")
                .setIcon(R.drawable.exit)
                .setMessage("要退出lonely wolf吗?")
                .setPositiveButton(
                    "确认"
                ) { dialog, which ->
                    moveTaskToBack(true)
                }
                .setNegativeButton("取消", null)
                .create().show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}