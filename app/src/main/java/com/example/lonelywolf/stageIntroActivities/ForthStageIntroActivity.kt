package com.example.lonelywolf.stageIntroActivities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.animation.addListener
import com.example.lonelywolf.R
import com.example.lonelywolf.otherActivities.StageChooseActivity
import com.example.lonelywolf.stageActivities.FirstStageActivity
import com.example.lonelywolf.stageActivities.ForthStageActivity
import kotlinx.android.synthetic.main.activity_forth_stage_intro.*

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 */
class ForthStageIntroActivity : AppCompatActivity() {
    var playEnabled = false
    val imageArray: IntArray = intArrayOf(
        R.drawable.blackman, R.drawable.blackman, R.drawable.xiaochouhouzi, R.drawable.hongyu,
        R.drawable.xiaochouhouzi, R.drawable.xiaochouhouzi,
        R.drawable.hongyu, R.drawable.xiaochouhouzi, R.drawable.hongyu, R.drawable.xiaochouhouzi,
        R.drawable.hongyu, R.drawable.xiaochouhouzi
    )
    val dialogArray: Array<String> = arrayOf(
        "旁白：鸿裕在巨熊部落获得药物、击败首领后，按照坠鹏的计划开始了斩首行动。",
        "经历了和巨熊首领的战斗，鸿裕和天威之魂的契合度有所提升，虽然还无法发挥它的全部力量，但一股力量充盈的感觉还是让鸿裕十分手热。他决定先从妖族大将——小丑猴子开始清算。",
        "小丑猴子：谁？！（接下一招）",
        "鸿裕：小丑猴子，你毁我国民，杀我师傅，罪无可恕！今日便以你的头颅告慰我的父老乡亲！",
        "小丑猴子：你是？（恍然）你是人族的皇子啊，本座找你好久了，既然来了，今日就留下吧，我族皇帝可是寻找天威之魂好久了！（开打）",
        "小丑猴子：奇怪，我已经用我的护体毒素命中这小子几次了，按道理他早就应该失去战斗力了……是他体质特殊，还是……",
        "鸿裕：（还好有解药，他的毒素对我不起作用，先机是我的）喝！（击退小丑猴子）",
        "小丑猴子：啧，再来！",
        "鸿裕：呃……？咳咳！",
        "小丑猴子：（嗯？毒素起效了？）（恍然）巨熊部落最近被人洗劫了，就是你小子干的吧？怪不得之前的毒素对你无效！",
        "鸿裕：新的毒素……？",
        "小丑猴子：马戏表演该结束了，把天威之魂交出来吧！（与鸿裕再次战到一处）"
    )
    var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forth_stage_intro)

        if (this.resources.configuration.orientation == ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        jump.setOnClickListener {
            val intent = Intent(this, ForthStageActivity::class.java)
            startActivity(intent)
        }
        val fourthStageIntro = ImageView(this)
        fourthStageIntro.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            x = 0f
            y = 0f
            setBackgroundResource(R.drawable.fourthstagetitle)
            alpha = 0f
        }
        layout.addView(fourthStageIntro)
        fourthStageIntro.bringToFront()
        fourthStageIntro.alpha = 0f
        val alphaTo0 = ObjectAnimator.ofFloat(fourthStageIntro, "alpha", 1f, 0f)
        alphaTo0.setDuration(6000)
        val set = AnimatorSet()
        set.playSequentially(alphaTo0)
        set.addListener(
            onEnd = {
                layout.removeView(fourthStageIntro)
                playEnabled = true
            }
        )
        set.start()
        textField.setText(dialogArray[index])
        textField.bringToFront()
        imageView1.setImageResource(imageArray[index])
    }

    override fun onStart() {
        super.onStart()
        index = 0
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (playEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    index++
                    if (index == imageArray.size) {
                        val intent = Intent(this, ForthStageActivity::class.java)
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 监控返回键
            AlertDialog.Builder(this).setTitle("提示")
                .setIcon(R.drawable.exit)
                .setMessage("确定要退出吗?")
                .setPositiveButton(
                    "确认"
                ) { dialog, which ->
                    val intent = Intent(this, StageChooseActivity::class.java)
                    startActivity(intent)
                    this.finish()
                }
                .setNegativeButton("取消", null)
                .create().show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
