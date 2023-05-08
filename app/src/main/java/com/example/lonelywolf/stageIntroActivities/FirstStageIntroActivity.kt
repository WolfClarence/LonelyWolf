package com.example.lonelywolf.stageIntroActivities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import com.example.lonelywolf.R
import com.example.lonelywolf.otherActivities.StageChooseActivity
import com.example.lonelywolf.stageActivities.FirstStageActivity
import kotlinx.android.synthetic.main.activity_first_stage_intro.*
import kotlinx.android.synthetic.main.activity_first_stage_intro.layout

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 */
class FirstStageIntroActivity : AppCompatActivity() {
    var playEnabled = false
    val imageArray: IntArray = intArrayOf(
        R.drawable.jiangjun1,
        R.drawable.blackman,
        R.drawable.blackman,
        R.drawable.maoniu_lihui,
        R.drawable.jiangjun1,
        R.drawable.maoniu_lihui,
        R.drawable.blackman,
        R.drawable.maoniu_lihui
    )//按顺排放
    val dialogArray: Array<String> = arrayOf(
        "坠鹏：皇子殿下，咱们马上就能逃出去了！",
        "旁白：（襁褓中传来哭声）", "旁白：（暗处有飞镖突袭，坠鹏拔剑挡下，两人从暗处跃出扑向坠鹏，将其击退）",
        "刺客A：坠鹏大将军，我族大军已在城外吹响进攻号角，你们已经逃不掉了。您为人族征战多年，威名远扬，我等佩服。若你愿意将皇子交予我等，发誓永远不与妖族为敌，我等愿意放您一条生路。",
        "坠鹏：想让我放下亡国之恨，痴人说梦！今日你等既拦不住我，也不能彻底灭了这国！他日我等必将东山再起，摘取小丑猴子项上猴头！",
        "刺客B：区区一只丧家之犬，也敢在我面前唁唁狂吠！既然你敬酒不吃吃罚酒，就别怪我不客气！ ",
        "旁白：（襁褓中的哭声愈发大起来）",
        "刺客B：大言不惭！"
    )
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_stage_intro)

        jump.setOnClickListener {
            val intent = Intent(this, FirstStageActivity::class.java)
            startActivity(intent)
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val firstStageIntro = ImageView(this)
        firstStageIntro.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            x = 0f
            y = 0f
            setBackgroundResource(R.drawable.firststagetitle)
        }
        layout.addView(firstStageIntro)
        firstStageIntro.bringToFront()
        firstStageIntro.alpha = 0f
        val alphaTo0 = ObjectAnimator.ofFloat(firstStageIntro, "alpha", 1f, 0f)
        alphaTo0.duration = 6000
        val set = AnimatorSet()
        set.playSequentially(alphaTo0)
        set.addListener(
            onEnd = {
                layout.removeView(firstStageIntro)
                playEnabled = true
            }
        )
        set.start()

        textField.text = dialogArray[index]
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
                    //imageView2.setImageResource(R.drawable.duihuakuang)
                    index++
                    if (index == imageArray.size) {
                        val intent = Intent(this, FirstStageActivity::class.java)
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
