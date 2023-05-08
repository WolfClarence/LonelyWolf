package com.example.lonelywolf.stageEndActivities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import com.example.lonelywolf.R
import com.example.lonelywolf.otherActivities.StageChooseActivity
import kotlinx.android.synthetic.main.activity_first_stage_end.*

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 */
class FirstStageEndActivity : AppCompatActivity() {
    val imageArray: IntArray = intArrayOf(R.drawable.jiangjun1, R.drawable.jiangjun1)
    val dialogArray: Array<String> = arrayOf(
        "坠鹏：殿下小心！（护住怀里的婴儿冲出重围，自己却被毒剑刺伤）",
        "坠鹏：（皱眉）这是什么毒！"
    )
    var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_stage_end)
        overridePendingTransition(0, 0)

        if (this.getResources()
                .getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
        ) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        jump.setOnClickListener {
            //跳过剧情
        }
        textField.setText(dialogArray[index])
        textField.bringToFront()
        imageView1.setImageResource(imageArray[index])
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    index++
                    if (index == imageArray.size) {
                        val intent = Intent(this, StageChooseActivity::class.java)
                        startActivity(intent)
                    } else {
                        textField.setText(dialogArray[index])
                        textField.bringToFront()
                        imageView1.setImageResource(imageArray[index])
                    }
                }

            }
        }

        return true
    }


}

