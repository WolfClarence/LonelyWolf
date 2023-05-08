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
import kotlinx.android.synthetic.main.activity_sixth_stage_end.imageView1
import kotlinx.android.synthetic.main.activity_sixth_stage_end.jump
import kotlinx.android.synthetic.main.activity_sixth_stage_end.textField

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 */
class SixthStageEndActivity : AppCompatActivity() {

    val imageArray: IntArray = intArrayOf(
        R.drawable.blackman, R.drawable.xuelong,
        R.drawable.hongyu_langer, R.drawable.blackman, R.drawable.hongyu_langer
    )
    val dialogArray: Array<String> = arrayOf(
        "旁白：（一番鏖战后，雪龙不敌，被鸿裕重重一击打飞，倒在地上抽搐）",
        "雪龙：轻敌了……小杂毛还真有点本事。",
        "鸿裕：你这让人恶心的家伙，去向我父亲忏悔吧！",
        "旁白：（雪龙被彻底击杀）",
        "鸿裕：父亲……孩儿真的好想见您一面啊……我会继承您的遗志，创造人妖兽并存的世界的！您在天有灵，就请好好看着我吧！",
    )
    var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sixth_stage_end)
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
                        textField.text = dialogArray[index]
                        textField.bringToFront()
                        imageView1.setImageResource(imageArray[index])
                    }
                }

            }
        }

        return true


    }
}
