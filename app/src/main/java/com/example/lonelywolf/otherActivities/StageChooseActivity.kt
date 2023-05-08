package com.example.lonelywolf.otherActivities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import com.example.lonelywolf.R
import com.example.lonelywolf.base.MyActivity
import com.example.lonelywolf.stageIntroActivities.*
import kotlinx.android.synthetic.main.activity_stage_chooce.*

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 * 关卡选择界面
 */
class StageChooseActivity : MyActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stage_chooce)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        button1.setOnClickListener {
            val intent = Intent(this, FirstStageIntroActivity::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            val intent = Intent(this, SecondStageIntroActivity::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener {
            val intent = Intent(this, ThirdStageIntroActivity::class.java)
            startActivity(intent)
        }

        button4.setOnClickListener {
            val intent = Intent(this, ForthStageIntroActivity::class.java)
            startActivity(intent)
        }

        button5.setOnClickListener {
            val intent = Intent(this, FifthStageIntroActivity::class.java)
            startActivity(intent)
        }

        button6.setOnClickListener {
            val intent = Intent(this, SixthStageIntroActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}