package com.example.lonelywolf.stageIntroActivities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
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
import com.example.lonelywolf.stageActivities.FifthStageActivity
import com.example.lonelywolf.stageActivities.FirstStageActivity
import kotlinx.android.synthetic.main.activity_fifth_stage_stage.*

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 */
class FifthStageIntroActivity : AppCompatActivity() {
    var playEnabled = false
    val imageArray: IntArray = intArrayOf(
        R.drawable.hongyu, R.drawable.xiaochouhouzi, R.drawable.xiaochouhouzi,
        R.drawable.hongyu, R.drawable.xiaochouhouzi,
        R.drawable.blackman, R.drawable.hongyu,
        R.drawable.xiaochouhouzi, R.drawable.xiaochouhouzi,
        R.drawable.xiaochouhouzi, R.drawable.hongyu_langer
    )
    val dialogArray: Array<String> = arrayOf(
        "鸿裕：……糟糕，失算了！这家伙真是难缠！",
        "小丑猴子：没想到你竟然有我护体毒素的解药，可惜是十几年前的，是坠鹏那个败军之将告诉你的吧。",
        "不过，哪怕掌握着这种情报，拿到先机，你也没能赢啊……实话告诉你，就凭你现在的实力，即使不依赖毒，我也可以最终压制住你，你比当年的坠鹏差远了。就这还想要重建天威？痴人说梦！",
        "鸿裕：你……",
        "小丑猴子：正好，我族皇帝雪龙寻找这天威之魂很久了，虽然不知道你为何能使用它，但我要夺得献给雪龙皇帝。",
        "旁白：（正在鸿裕奄奄一息之时，怀中的天威之魂骤生异变）",
        "鸿裕：好暖和啊……",
        "小丑猴子：是天威之魂在主动帮他治疗伤势吗？这宝物竟有这种效果！",
        "小丑猴子：等等，他的形态变了？看上去像是一匹狼！难道他是兽类的族人？不能再等了，得立刻终结掉他！（向着鸿裕冲去）",
        "小丑猴子：怎么会有这种事！（挥去的拳头被鸿裕一口咬住，身体被甩了出去）",
        "鸿裕：力量还在不断涌现，这次我能赢！"
    )
    var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fifth_stage_stage)

        if (this.resources
                .configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        ) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        jump.setOnClickListener {
            val intent = Intent(this, FifthStageActivity::class.java)
            startActivity(intent)
        }

        val fifthStageIntro = ImageView(this)
        fifthStageIntro.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            x = 0f
            y = 0f
            setBackgroundResource(R.drawable.fifthstagetitle)
            alpha = 0f
        }
        layout.addView(fifthStageIntro)
        fifthStageIntro.bringToFront()
        fifthStageIntro.alpha = 0f
        val alphaTo0 = ObjectAnimator.ofFloat(fifthStageIntro, "alpha", 1f, 0f)
        alphaTo0.setDuration(6000)
        val set = AnimatorSet()
        set.playSequentially(alphaTo0)
        set.addListener(
            onEnd = {
                layout.removeView(fifthStageIntro)
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
                        val intent = Intent(this, FifthStageActivity::class.java)
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
