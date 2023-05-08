package com.example.lonelywolf.stageIntroActivities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
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
import com.example.lonelywolf.stageActivities.SecondStageActivity
import kotlinx.android.synthetic.main.activity_second_stage_intro.*

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 */
class SecondStageIntroActivity : AppCompatActivity() {
    var playEnabled = false
    val imageArray: IntArray = intArrayOf(
        R.drawable.blackman,
        R.drawable.young,
        R.drawable.jiangjun,
        R.drawable.young,
        R.drawable.jiangjun,
        R.drawable.blackman,
        R.drawable.young,
        R.drawable.jiangjun,
        R.drawable.young
    )
    val dialogArray: Array<String> = arrayOf(
        "旁白：坠鹏虽成功带领皇子逃出，但在与刺客的对战中吸入了一点毒瘴，身体逐渐变得虚弱。他们逃到妖迹罕至的北境，在深山中生存。从鸿裕皇子幼时开始，坠鹏便锻炼其体魄，教他战斗和生存的技巧，给他讲三族对峙的历史。时光飞逝，坠鹏转眼已八岁。这天晚上……",
        "鸿裕：坠鹏将军，打我记事起，就一直跟随您在这山中生活。我们没有其他朋友吗？要重建两族，我们需要人手呀！",
        "坠鹏：殿下，那妖族十分狡猾，即使是现在的人兽两族中也有不少族人向妖族投诚。我们无法辨别哪些是真朋友，哪些是背叛者，不能贸然接触其他人。我们的力量太弱小，一旦被发现，即使是这北境的虾兵蟹将也能置我们于死地。",
        "鸿裕：这可如何是好呀……",
        "坠鹏：殿下莫慌，臣心中已有一计。殿下的根骨超群，将来在习武上必有成就，若殿下能潜入妖族骨干的居所将其刺杀，便能为自己积攒名望，等时机成熟，殿下振臂一呼，必有不少人兽族人前来相助。",
        "旁白：（鸿裕突然警觉，有种被监视的感觉）",
        "鸿裕：有刺客！将军小心！",
        "坠鹏：殿下好眼力。不过是几只小狐狸罢了，我去阻挡他们，还请殿下助我一臂之力，相信以您目前的实力，足够应付这几只杂碎了。",
        "鸿裕：没问题！"
    )
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_stage_intro)

        jump.setOnClickListener {
            val intent = Intent(this, SecondStageActivity::class.java)
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
            setBackgroundResource(R.drawable.secondstagetitle)
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
                    index++
                    if (index == imageArray.size) {
                        val intent = Intent(this, SecondStageActivity::class.java)
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