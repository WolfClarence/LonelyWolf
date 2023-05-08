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
import com.example.lonelywolf.stageActivities.SecondStageActivity
import com.example.lonelywolf.stageActivities.ThirdStageActivity
import kotlinx.android.synthetic.main.activity_third_stage_intro.*

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 */
class ThirdStageIntroActivity : AppCompatActivity() {
    var playEnabled = false
    val imageArray: IntArray = intArrayOf(
        R.drawable.blackman,
        R.drawable.blackman,
        R.drawable.jiangjun,
        R.drawable.hongyu,
        R.drawable.jiangjun,
        R.drawable.hongyu,
        R.drawable.jiangjun,
        R.drawable.hongyu,
        R.drawable.jiangjun,
        R.drawable.hongyu,
        R.drawable.jiangjun,
        R.drawable.jiangjun,
        R.drawable.jiangjun,
        R.drawable.jiangjun,
        R.drawable.hongyu,
        R.drawable.hongyu,
        R.drawable.blackman
    )
    val dialogArray: Array<String> = arrayOf(
        "旁白：在山中的生活虽然艰苦，但除了偶尔出现的刺客袭击外，二人的处境还算安全，北境有一支延绵不绝的山脉，二人经常在不同的山间转移，所以小丑猴子虽然知道他们很可能藏身于这片山脉里，一时也无法围剿他们。",
        "旁白：时间如流水，转眼，鸿裕十七岁了。而坠鹏的身体却每况愈下，那一缕毒瘴很是难缠，十七年来，他的生命即将被这一根细小的锁链消磨干净。",
        "坠鹏：咳咳……",
        "鸿裕：将军！你不要紧吧？",
        "坠鹏：十几年前陪都的那杂碎的毒还真要命（小声）……我不要紧。殿下。你如今即将成年，在下的一身武艺，殿下已基本掌握。现在的殿下，或许已经有掌控天威之魂的力量了。",
        "鸿裕：天威之魂？那不是只有兽族才能使用吗？",
        "坠鹏：事已至此，有些事也应该让您知晓了。兽族的狼王当年在一次外交会晤中与皇上的姐姐一见钟情，之后结婚并育有一子。而皇上天生无法生育，便与姐姐夫妇商量，将他们的孩子立为皇储。殿下，这个孩子就是您。",
        "鸿裕：……",
        "坠鹏：殿下，抱歉瞒了您这么久。当时微臣私以为，让年幼的殿下背负一份灭国之仇已经足够残酷，便不忍将真相告知殿下。如今殿下即将成年，微臣相信殿下能够鼓起勇气面对落在自己身上的责任了。",
        "鸿裕：谢谢你，将军。我会继承父亲和皇上的意志的。如今皇上已死，那我父亲现在何方？",
        "坠鹏：狼王现被妖族所俘，生死未卜。（说罢吐血）",
        "坠鹏：殿下，十七年前我护送您出城的时候遭遇刺客拦截，在击败他们后，其中一人以自身为引释放毒瘴，臣不小心吸入了一点，本以为无恙，不料这毒素有如附骨之疽，一直消磨着臣的性命。如今看来是到了穷途末路了。",
        "坠鹏：殿下，臣曾经在前线与妖族作战，后因中了小丑猴子的毒而回到宫中疗养。他们的高层人物会随身携带几种护体剧毒，如果没有解药的话，在战斗中需要十分谨慎。据臣所知，南方的巨熊部落境内有这些护体剧毒的解药。这个部落在当初开战时便倒向妖族成了走狗，虽然当时不敌联军，但如今妖族占地为王，这个部落想必也会死灰复燃。殿下去那里取到解药，在面对妖族高层时可更加从容。如果可以，请击败部落的首领吧，和强者的战斗能磨炼您的实力，为您积攒威望。",
        "坠鹏：殿下，狼王和陛下乃是至交，他们希望能创造一个人妖兽和平共处的世界。他们的一拍即合也是人兽两族最初交往变得频繁的原因之一……希望殿下能记住他们的初心，不要让新的天威成为下一个妖……族……（咽气）",
        "鸿裕：坠鹏将军！！（潸然泪下）",
        "鸿裕：（吸气）将军放心，我定当继承父亲和陛下的意志，重建天威！",
        "旁白：(鸿裕去找巨熊)"

    )
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_stage_intro)

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
            val intent = Intent(this, ThirdStageActivity::class.java)
            startActivity(intent)
        }

        val thirdStageIntro = ImageView(this)
        thirdStageIntro.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            x = 0f
            y = 0f
            setBackgroundResource(R.drawable.thirdstagetitle)
            alpha = 0f
        }
        layout.addView(thirdStageIntro)
        thirdStageIntro.bringToFront()
        thirdStageIntro.alpha = 0f
        val alphaTo0 = ObjectAnimator.ofFloat(thirdStageIntro, "alpha", 1f, 0f)
        alphaTo0.setDuration(6000)
        val set = AnimatorSet()
        set.playSequentially(alphaTo0)
        set.addListener(
            onEnd = {
                layout.removeView(thirdStageIntro)
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
                        val intent = Intent(this, ThirdStageActivity::class.java)
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
