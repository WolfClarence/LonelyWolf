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
import com.example.lonelywolf.stageActivities.FirstStageActivity
import com.example.lonelywolf.stageActivities.SixthStageActivity
import kotlinx.android.synthetic.main.activity_sixth_stage_intro.*

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 */
class SixthStageIntroActivity : AppCompatActivity() {
    var playEnabled = false
    val imageArray: IntArray = intArrayOf(
        R.drawable.blackman,
        R.drawable.blackman,
        R.drawable.blackman,
        R.drawable.blackman,
        R.drawable.xuelong,
        R.drawable.hongyu_langer,
        R.drawable.xuelong,
        R.drawable.hongyu_langer,
        R.drawable.xuelong,
        R.drawable.blackman,
        R.drawable.xuelong,
        R.drawable.hongyu_langer,
        R.drawable.xuelong,
        R.drawable.hongyu_langer,
        R.drawable.xuelong,
        R.drawable.xuelong,
        R.drawable.hongyu_langer,
        R.drawable.blackman,
        R.drawable.xuelong,
        R.drawable.hongyu_langer
    )
    val dialogArray: Array<String> = arrayOf(
        "旁白：在与小丑猴子一战中，鸿裕的兽族血脉觉醒，让他拥有了变换形态的能力，也让他与天威之魂的契合度变高了。",
        "随着死在他手下的妖族高层越来越多，各个高层都对他充满了警惕；但与此同时，鸿裕的举动也鼓舞了不少藏身在民间的原人兽族高手，大大小小的起义、示威游行和出自民间高手之手的斩首行动也偶尔会爆发。",
        "旁白：这为鸿裕的行动提供了很大的便利。他打算趁着自己的身份还没暴露，先去营救自己的亲生父亲——狼王。据传，狼王被关在妖族领主——雪龙的领土。",
        "为了削弱防御力量，鸿裕故意在都城周围显露踪迹，还为都城及周围的城市里的反妖族运动煽风点火。都城人心惶惶，妖族调集分散在外的一支支部队主力回来拱卫都城。趁着雪龙领地防守空虚，鸿裕成功潜入了雪龙的宫殿。",
        "雪龙：小子，胆子不小啊。真当我雪某人好欺负不成？",
        "鸿裕：明明是妖，却给自己起个兽类的名字，你可真是爱往自己脸上贴金啊。再杀你之前，你还有话要告诉我——狼王被关在哪里！",
        "雪龙：果然是要来找囚犯啊，我就说一个能刺杀小丑猴子那帮饭桶的刺客，怎么说也不应该在都城显露踪迹，而且都城周围的抵抗突然变得激烈了，我就在想会不会是调虎离山……",
        "鸿裕：少废话！狼王在哪！",
        "雪龙：这么想见他的话，待会我就送你去!(与鸿裕开战)",
        "旁白： 雪龙实力强劲，面对觉醒了兽类血脉,能比较熟练地使用天威之魂的鸿裕依然不落下风。",
        "雪龙：有意思！竟然能从人变成狼。我听说狼王和人类育有一个孩子，就是你吧？难怪这么着急见你那杂毛老爹！",
        "鸿裕：不许侮辱我父亲！",
        "雪龙：他都死了，我说他两句也无妨吧。",
        "鸿裕：你说……什么，他死了？",
        "雪龙：哦，忘记跟你说了，真是失礼。实不相瞒，当初流入人兽两族的控制神志的蛊毒是我开发的，但其实那只是个半成品，我一直期待着能完善它。",
        "你父亲可真是完美的实验材料。我记得他当时陷入幻觉的时候，还一直在呼唤“红芋”什么的，狼也吃芋头么？",
        "鸿裕：你这混账！",
        "旁白：（鸿裕受到刺激，浑身的血液加速流动，天威之魂对此有了反应。）",
        "雪龙：这个东西能够刺激血脉二次觉醒吗？真有意思！看来你们父子俩都是很棒的素材啊！我要敲碎你的四肢，好好研究一下你，哈哈哈哈！",
        "鸿裕：又是这种力量充盈的感觉……父亲，我这就为你报仇！"
    )
    var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sixth_stage_intro)

        if (this.getResources()
                .getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
        ) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        jump.setOnClickListener {
            val intent = Intent(this, SixthStageActivity::class.java)
            startActivity(intent)
        }

        val sixthStageIntro = ImageView(this)
        sixthStageIntro.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            x = 0f
            y = 0f
            setBackgroundResource(R.drawable.sixthstagetitle)
            alpha = 0f
        }
        layout.addView(sixthStageIntro)
        sixthStageIntro.bringToFront()
        sixthStageIntro.alpha = 0f
        val alphaTo0 = ObjectAnimator.ofFloat(sixthStageIntro, "alpha", 1f, 0f)
        alphaTo0.setDuration(6000)
        val set = AnimatorSet()
        set.playSequentially(alphaTo0)
        set.addListener(
            onEnd = {
                layout.removeView(sixthStageIntro)
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
                        val intent = Intent(this, SixthStageActivity::class.java)
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
