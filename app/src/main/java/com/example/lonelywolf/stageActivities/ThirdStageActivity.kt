package com.example.lonelywolf.stageActivities

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.*
import android.widget.TextView
import androidx.core.animation.addListener
import com.example.lonelywolf.base.MyImageView
import com.example.lonelywolf.R
import com.example.lonelywolf.base.ActivityCollector
import com.example.lonelywolf.base.MyActivity
import com.example.lonelywolf.characterControl.CharacterParams
import com.example.lonelywolf.grid.Grid
import com.example.lonelywolf.characterControl.EnemyVector
import com.example.lonelywolf.characterControl.HeroVector
import com.example.lonelywolf.characterControl.MyImageViewFactory
import com.example.lonelywolf.otherActivities.DefeatActivity
import com.example.lonelywolf.otherActivities.StageChooseActivity
import com.example.lonelywolf.otherActivities.VictoryActivity
import kotlinx.android.synthetic.main.activity_third_stage.*
import kotlinx.android.synthetic.main.activity_third_stage.button1
import kotlinx.android.synthetic.main.activity_third_stage.button2
import kotlinx.android.synthetic.main.activity_third_stage.button3
import kotlinx.android.synthetic.main.activity_third_stage.button4
import kotlinx.android.synthetic.main.activity_third_stage.button5
import kotlinx.android.synthetic.main.activity_third_stage.button6
import kotlinx.android.synthetic.main.activity_third_stage.button_cancel
import kotlinx.android.synthetic.main.activity_third_stage.gridView1
import kotlinx.android.synthetic.main.activity_third_stage.layout
import kotlinx.android.synthetic.main.activity_third_stage.mpText
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 */
class ThirdStageActivity : MyActivity() {
    //这些boolean变量用于控制点击事件
    private var heroMoveEnabled = false
    private var attackEnabled = false
    private var sprintEnabled = false
    private var kingSwordEnabled = false
    private var kingShieldEnabled = false
    private var kingShieldOn = false
    private var kingNecromancyEnabled = false
    private var kingCureEnabled = false

    //储存绿色方形网格的链表
    private var grids = LinkedList<Grid>()
    val row = 5
    val column = 7

    //给handler传递信息的常量
    val init = 1
    val deleteHero = 2
    val heroActEnd = 3
    val enemy0Act1End = 4
    val enemy0Act2End = 5
    val enemy0Act3End = 6
    val enemy1Act1End = 7
    val enemy1Act2End = 8
    val enemy1Act3End = 9
    val deleteWhirlWind = 10
    val judgeDeath = 11
    val deleteDescriptionField = 12
    val doGhostStrike0 = 13
    val doGhostStrike1 = 14
    val enemy2Act1End = 15
    val enemy2Act2End = 16
    val enemy2Act3End = 17
    val deleteBlood = 21
    val victoryInformation = 22
    val jumpEnd = 23
    val judgeHeroDeath = 25

    //敌人回合储存链表
    private var enemies = ArrayList<EnemyVector>()

    //英雄容器和敌人容器
    lateinit var heroVector: HeroVector
    lateinit var enemyVector0: EnemyVector
    lateinit var enemyVector1: EnemyVector
    lateinit var enemyVector2: EnemyVector

    //handler，作用是开启新的线程延时对UI进行操作
    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        @SuppressLint("CutPasteId", "Recycle")
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                init -> {
                    generateEnemy()
                    initWolf()
                }
                deleteHero -> {
                    heroVector.disappear(layout)
                    val intent = Intent(this@ThirdStageActivity, DefeatActivity::class.java)
                    startActivity(intent)
                }
                judgeHeroDeath -> {
                    if (heroVector.dead) {
                        heroVector.disappear(layout)
                        val intent = Intent(this@ThirdStageActivity, DefeatActivity::class.java)
                        startActivity(intent)
                        ActivityCollector.finishOneActivity(ThirdStageActivity::class.java.name)
                    }
                }
                deleteWhirlWind -> {
                    layout.removeView(findViewById(R.id.id_whirlwind))
                }
                //行动顺序
                heroActEnd -> {
                    if (!enemyVector0.dead) {
                        enemyAct(0, 1)
                    } else {
                        enemyAct(1, 1)
                    }
                }
                //
                enemy0Act1End -> {
                    enemyActEnd(0, 1)
                }
                enemy0Act2End -> {
                    enemyActEnd(0, 2)
                }
                enemy0Act3End -> {
                    enemyActEnd(0, 3)
                }
                enemy1Act1End -> {
                    enemyActEnd(1, 1)
                }
                enemy1Act2End -> {
                    enemyActEnd(1, 2)
                }
                enemy1Act3End -> {
                    enemyActEnd(1, 3)
                }
                enemy2Act1End -> {
                    enemyActEnd(2, 1)
                }
                enemy2Act2End -> {
                    enemyActEnd(2, 2)
                }
                enemy2Act3End -> {
                    enemyActEnd(2, 3)
                }
                //
                judgeDeath -> {
                    if (enemyVector0.dead) {
                        enemyVector0.disappear(layout)
                        enemyVector0.grid = Grid(gridView1, 10, 10)
                    }
                    if (enemyVector1.dead) {
                        enemyVector1.disappear(layout)
                        enemyVector1.grid = Grid(gridView1, 10, 10)
                    }
                    if (enemyVector2.dead) {
                        enemyVector2.disappear(layout)
                        enemyVector2.grid = Grid(gridView1, 10, 10)
                    }
                    if (enemyVector0.dead && enemyVector1.dead && enemyVector2.dead) {
                        victory()
                    }
                }
                deleteDescriptionField -> {
                    layout.removeView(findViewById(R.id.id_descriptionfield))
                }
                doGhostStrike0 -> {
                    val enemies = ArrayList<EnemyVector>()
                    enemies.add(enemyVector1)
                    heroVector.ghostStrike(
                        enemyVector0.grid, gridView1, this@ThirdStageActivity, layout, enemyVector0,
                        enemies, (heroVector.attack * 1.5 + 15).toInt()
                    )
                }
                doGhostStrike1 -> {
                    val enemies = ArrayList<EnemyVector>()
                    enemies.add(enemyVector0)
                    heroVector.ghostStrike(
                        enemyVector1.grid, gridView1, this@ThirdStageActivity, layout, enemyVector1,
                        enemies, (heroVector.attack * 1.5 + 15).toInt()
                    )
                }
                deleteBlood -> {
                    val bloodView = findViewById<MyImageView>(R.id.id_blood)
                    val scale = ObjectAnimator.ofFloat(bloodView, "scaleX", 1f, 0f)
                    val scale1 = ObjectAnimator.ofFloat(bloodView, "scaleY", 1f, 0f)
                    scale.duration = 480
                    scale1.duration = 480
                    scale.start()
                    scale1.start()
                    scale.addListener(
                        onEnd = {
                            layout.removeView(bloodView)
                        }
                    )
                }
                victoryInformation -> {
                    val intent = Intent(this@ThirdStageActivity, VictoryActivity::class.java)
                    startActivity(intent)
                }
                jumpEnd -> {
                    enemyVector0.pounce(
                        heroVector.grid, this@ThirdStageActivity, layout, gridView1,
                        heroVector, kingShieldOn
                    )
                    if (kingShieldOn) {
                        kingShieldOn = false
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_stage)
        //对系统状态栏进行设置，隐藏系统状态栏
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        overridePendingTransition(0, 0)

        thread {
            val message = Message()
            message.what = init
            Thread.sleep(100)
            handler.sendMessage(message)
        }

        //位移
        button1.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField(
                        "技能：疾步\n 向一个位置移动\n 消耗：1体力"
                    )
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 1) {
                        generateGreenGridAround(heroVector.grid)
                        heroMoveEnabled = true
                    } else {
                        generateDescriptionField("体力不够", 300)
                    }
                }
            }
            return@setOnTouchListener true
        }
        //天威：剑
        button2.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField(
                        "天威：剑\n向一个范围2格的敌人进行\n天威：剑攻击，造成" +
                                "${(heroVector.attack * 1.2 + 20).toInt()}(攻击力*1.2+20)点伤害\n消耗：2体力"
                    )
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 2) {
                        if (heroVector.grid.radiusContain(2, enemyVector0.grid) ||
                            heroVector.grid.radiusContain(2, enemyVector1.grid) ||
                            heroVector.grid.radiusContain(2, enemyVector2.grid)
                        ) {
                            kingSwordEnabled = true
                            if (!enemyVector0.dead && heroVector.grid.radiusContain(
                                    2,
                                    enemyVector0.grid
                                )
                            ) {
                                generateGreenGridAt(enemyVector0.grid)
                            }
                            if (!enemyVector1.dead && heroVector.grid.radiusContain(
                                    2,
                                    enemyVector1.grid
                                )
                            ) {
                                generateGreenGridAt(enemyVector1.grid)
                            }
                            if (!enemyVector2.dead && heroVector.grid.radiusContain(
                                    2,
                                    enemyVector2.grid
                                )
                            ) {
                                generateGreenGridAt(enemyVector2.grid)
                            }
                            buttonInvisible()
                        } else {
                            generateDescriptionField("攻击距离2，无可选中目标！", 500)
                        }
                    } else {
                        generateDescriptionField("体力不够", 300)
                    }
                }
            }
            return@setOnTouchListener true
        }
        //天威：盾
        button3.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField(
                        "天威:盾\n天威之魂化为盾，下次受伤时抵消伤害，并回复${(heroVector.attack * 0.1 + 5).toInt()}" +
                                "(0.1*攻击力+5)点生命\n消耗：2体力"
                    )
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 2) {
                        if (!kingShieldOn) {
                            kingShieldEnabled = true
                            generateGreenGridAt(heroVector.grid)
                            buttonInvisible()
                        } else {
                            generateDescriptionField("已获得君王盾庇护", 1000)
                        }
                    } else {
                        generateDescriptionField("体力不够", 1000)
                    }
                }
            }
            return@setOnTouchListener true
        }
        //天威：魂
        button4.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField(
                        "天威：魂\n 天威之魂以真身冲向敌人，造成${(0.8 * heroVector.attack + 10).toInt()}" +
                                "(攻击力*0.8+10)点伤害\n 消耗：2体力"
                    )
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 2) {
                        attackEnabled = true
                        if (!enemyVector0.dead) {
                            generateGreenGridAt(enemyVector0.grid)
                        }
                        if (!enemyVector1.dead) {
                            generateGreenGridAt(enemyVector1.grid)
                        }
                        if (!enemyVector2.dead) {
                            generateGreenGridAt(enemyVector2.grid)
                        }
                        buttonInvisible()
                    } else {
                        generateDescriptionField("体力不够!", 1000)
                    }
                }
            }
            return@setOnTouchListener true
        }
        //天威：死灵
        button5.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField(
                        "天威：死灵\n天威之魂化作死灵形态\n对一名3格内的敌人" +
                                "造成${(heroVector.attack * 0.8 + 18).toInt()}(0.8*攻击力+18)点伤害并回复" +
                                "\n${(heroVector.attack * 0.3 + 5).toInt()}(0.3*攻击力+5)点生命\n消耗：3体力"
                    )
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 3) {
                        if (heroVector.grid.radiusContain(3, enemyVector0.grid) ||
                            heroVector.grid.radiusContain(3, enemyVector1.grid) ||
                            heroVector.grid.radiusContain(3, enemyVector2.grid)
                        ) {
                            kingNecromancyEnabled = true
                            if (!enemyVector0.dead && heroVector.grid.radiusContain(
                                    3,
                                    enemyVector0.grid
                                )
                            ) {
                                generateGreenGridAt(enemyVector0.grid)
                            }
                            if (!enemyVector1.dead && heroVector.grid.radiusContain(
                                    3,
                                    enemyVector1.grid
                                )
                            ) {
                                generateGreenGridAt(enemyVector1.grid)
                            }
                            if (!enemyVector2.dead && heroVector.grid.radiusContain(
                                    3,
                                    enemyVector2.grid
                                )
                            ) {
                                generateGreenGridAt(enemyVector2.grid)
                            }
                            buttonInvisible()
                        } else {
                            generateDescriptionField("攻击距离3，无可选中目标！", 500)
                        }
                    } else {
                        generateDescriptionField("体力不够", 300)
                    }
                }
            }
            return@setOnTouchListener true
        }
        //天威：愈
        button6.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField(
                        "天威:愈\n使天威之魂化作灵气治疗自己，回复${(0.2 * heroVector.attack).toInt()}" +
                                "(0.2*攻击力)点生命\n消耗：1体力"
                    )
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 1) {
                        kingCureEnabled = true
                        generateGreenGridAt(heroVector.grid)
                        buttonInvisible()
                    } else {
                        generateDescriptionField("体力不够", 1000)
                    }
                }
            }
            return@setOnTouchListener true
        }
        button_cancel.setOnClickListener {
            if (heroMoveEnabled) {
                for (index in 0 until grids.size) {
                    layout.removeView(findViewById(R.id.id_greenRect))
                }
                grids.clear()
                heroMoveEnabled = false
                buttonVisible()
            }
            if (attackEnabled) {
                removeGreen(4)
                attackEnabled = false
                buttonVisible()
            }
            if (sprintEnabled) {
                for (index in 0 until grids.size) {
                    layout.removeView(findViewById(R.id.id_greenRect))
                }
                grids.clear()
                sprintEnabled = false
                buttonVisible()
            }
            if (kingSwordEnabled) {
                removeGreen(4)
                kingSwordEnabled = false
                buttonVisible()
            }
            if (kingShieldEnabled) {
                removeGreen(1)
                kingShieldEnabled = false
                buttonVisible()
            }
            if (kingNecromancyEnabled) {
                removeGreen(4)
                kingNecromancyEnabled = false
                buttonVisible()
            }
            if (kingCureEnabled) {
                removeGreen(1)
                kingCureEnabled = false
                buttonVisible()
            }
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (heroVector.grid.contains(event.x, event.y)) {
                    val shieldOn = "\n君王盾庇护：开"
                    if (kingShieldOn) {
                        generateSkillField(
                            "鸿裕\n攻击：${heroVector.attack}\n护甲：${heroVector.defence}\n" +
                                    "减免${((1 - 100.0 / (100 + heroVector.defence)) * 100).toInt()}%的伤害\n" +
                                    "剩余体力：${heroVector.mp}" + shieldOn
                        )
                    } else {
                        generateSkillField(
                            "鸿裕\n攻击：${heroVector.attack}\n护甲：${heroVector.defence}\n" +
                                    "减免${((1 - 100.0 / (100 + heroVector.defence)) * 100).toInt()}%的伤害\n" +
                                    "剩余体力：${heroVector.mp}"
                        )
                    }
                }
                if (enemyVector0.grid.contains(event.x, event.y)) {
                    generateSkillField(
                        "巨熊首领\n攻击：${enemyVector0.attack}\n护甲：${enemyVector0.defence}\n" +
                                "减免${((1 - 100.0 / (100 + enemyVector0.defence)) * 100).toInt()}%的伤害"
                    )
                }
                if (enemyVector1.grid.contains(event.x, event.y)) {
                    generateSkillField(
                        "投石巨熊A\n攻击：${enemyVector1.attack}\n护甲：${enemyVector1.defence}\n" +
                                "减免${((1 - 100.0 / (100 + enemyVector1.defence)) * 100).toInt()}%的伤害"
                    )
                }
                if (enemyVector2.grid.contains(event.x, event.y)) {
                    generateSkillField(
                        "投石巨熊B\n攻击：${enemyVector2.attack}\n护甲：${enemyVector2.defence}\n" +
                                "减免${((1 - 100.0 / (100 + enemyVector2.defence)) * 100).toInt()}%的伤害"
                    )
                }
            }
            MotionEvent.ACTION_UP -> {
                layout.removeView(findViewById(R.id.id_skillfield))
            }
        }
        if (heroMoveEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    //对于每一个储存在链表里的grid，都检测是不是包含手指点击的那个点
                    for (grid: Grid in grids) {
                        if (grid.contains(event.x, event.y)) {
                            heroVector.moveTo(Grid(gridView1, grid.gridX, grid.gridY))
                            mpText.text = "剩余体力：" + heroVector.mp.toString()
                            //对greenRect进行删除操作，采用查找id的方式，有几个就删除几次
                            for (index in 0 until grids.size) {
                                layout.removeView(findViewById(R.id.id_greenRect))
                            }
                            grids.clear()
                            heroMoveEnabled = false
                            if (heroVector.mp == 0) {
                                button_cancel.visibility = View.INVISIBLE
                                thread {
                                    val message = Message()
                                    Thread.sleep(300)
                                    message.what = heroActEnd
                                    handler.sendMessage(message)
                                }
                            } else {
                                buttonVisible()
                            }
                            break
                        }
                    }
                }
            }
        }
        if (attackEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    for (enemyVector: EnemyVector in enemies) {
                        if (!enemyVector.dead) {
                            if (enemyVector.grid.contains(event.x, event.y)) {
                                heroVector.kingSoul(
                                    this, layout, enemyVector, enemyVector.grid,
                                    (heroVector.attack * 0.8 + 10).toInt()
                                )
                                mpText.text = "剩余体力：" + heroVector.mp.toString()
                                for (index in 0..3) {
                                    layout.removeView(findViewById(R.id.id_greenRect))
                                }
                                thread {
                                    val message = Message()
                                    message.what = judgeDeath
                                    Thread.sleep(1200)
                                    handler.sendMessage(message)
                                }
                                if (heroVector.mp == 0) {
                                    button_cancel.visibility = View.INVISIBLE
                                    thread {
                                        val message = Message()
                                        message.what = heroActEnd
                                        Thread.sleep(1400)
                                        handler.sendMessage(message)
                                    }
                                } else {
                                    buttonVisible()
                                }
                                attackEnabled = false
                            }
                        }
                    }
                }
            }
        }
        if (sprintEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    for (grid: Grid in grids) {
                        if (grid.contains(event.x, event.y)) {
                            for (index in 0 until grids.size) {
                                layout.removeView(findViewById(R.id.id_greenRect))
                            }
                            grids.clear()
                            heroVector.sprint(
                                grid,
                                enemies,
                                (heroVector.attack * 0.5 + 60).toInt(), this, layout
                            )
                            mpText.text = "剩余体力：" + heroVector.mp.toString()
                            buttonVisible()
                            sprintEnabled = false
                            if (heroVector.mp == 0) {
                                button_cancel.visibility = View.INVISIBLE
                                thread {
                                    val message = Message()
                                    message.what = heroActEnd
                                    Thread.sleep(420)
                                    handler.sendMessage(message)
                                }
                            }
                            thread {
                                val message = Message()
                                message.what = judgeDeath
                                Thread.sleep(800)
                                handler.sendMessage(message)
                            }
                        }
                    }
                }
            }
        }
        if (kingSwordEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    for (enemyVector: EnemyVector in enemies) {
                        if (!enemyVector.dead) {
                            if (enemyVector.grid.contains(event.x, event.y)) {
                                heroVector.kingSword(
                                    enemyVector.grid,
                                    this,
                                    layout,
                                    enemyVector,
                                    (heroVector.attack * 1.2 + 20).toInt()
                                )
                                mpText.text = "剩余体力：" + heroVector.mp.toString()
                                for (index in 0..3) {
                                    layout.removeView(findViewById(R.id.id_greenRect))
                                }
                                thread {
                                    val message = Message()
                                    message.what = judgeDeath
                                    Thread.sleep(1200)
                                    handler.sendMessage(message)
                                }
                                if (heroVector.mp == 0) {
                                    button_cancel.visibility = View.INVISIBLE
                                    thread {
                                        val message = Message()
                                        message.what = heroActEnd
                                        Thread.sleep(1400)
                                        handler.sendMessage(message)
                                    }
                                } else {
                                    buttonVisible()
                                }
                                kingSwordEnabled = false
                            }
                        }
                    }
                }
            }
        }
        if (kingShieldEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (heroVector.grid.contains(event.x, event.y)) {
                        kingShieldEnabled = false
                        kingShieldOn = true
                        layout.removeView(findViewById(R.id.id_greenRect))
                        heroVector.kingShield(heroVector.grid, this, layout)
                        mpText.text = "剩余体力：" + heroVector.mp.toString()
                        if (heroVector.mp == 0) {
                            button_cancel.visibility = View.INVISIBLE
                            thread {
                                val message = Message()
                                message.what = heroActEnd
                                Thread.sleep(1000)
                                handler.sendMessage(message)
                            }
                        } else {
                            buttonVisible()
                        }
                    }
                }
            }
        }
        if (kingNecromancyEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    for (enemyVector: EnemyVector in enemies) {
                        if (!enemyVector.dead) {
                            if (enemyVector.grid.contains(event.x, event.y)) {
                                heroVector.kingNecromancy(
                                    this,
                                    layout,
                                    enemyVector,
                                    (heroVector.attack * 0.8 + 18).toInt(),
                                    (heroVector.attack * 0.3 + 5).toInt()
                                )
                                mpText.text = "剩余体力：" + heroVector.mp.toString()
                                for (index in 0..3) {
                                    layout.removeView(findViewById(R.id.id_greenRect))
                                }
                                thread {
                                    val message = Message()
                                    message.what = deleteBlood
                                    Thread.sleep(475)
                                    handler.sendMessage(message)
                                }
                                thread {
                                    val message = Message()
                                    message.what = judgeDeath
                                    Thread.sleep(800)
                                    handler.sendMessage(message)
                                }
                                if (heroVector.mp == 0) {
                                    button_cancel.visibility = View.INVISIBLE
                                    thread {
                                        val message = Message()
                                        message.what = heroActEnd
                                        Thread.sleep(1400)
                                        handler.sendMessage(message)
                                    }
                                } else {
                                    buttonVisible()
                                }
                                kingNecromancyEnabled = false
                            }
                        }
                    }
                }
            }
        }
        if (kingCureEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (heroVector.grid.contains(event.x, event.y)) {
                        kingCureEnabled = false
                        removeGreen(1)
                        heroVector.rest(this, layout)
                        mpText.text = "剩余体力：" + heroVector.mp.toString()
                        if (heroVector.mp == 0) {
                            button_cancel.visibility = View.INVISIBLE
                            thread {
                                val message = Message()
                                message.what = heroActEnd
                                Thread.sleep(600)
                                handler.sendMessage(message)
                            }
                        } else {
                            buttonVisible()
                        }
                    }
                }
            }
        }
        return true
    }

    /**
     * 生成敌人
     */


    /**
     * @param grid 在哪个网格周围生成
     * 在某个网格周围生成网格
     */
    private fun generateGreenGridAround(grid: Grid) {
        buttonInvisible()
        for (index1 in grid.gridX - 1..grid.gridX + 1) {
            for (index2 in grid.gridY - 1..grid.gridY + 1) {
                //判断：不能超过边界，不能生成在这个网格
                if (index1 >= 1 && index2 >= 1 && index1 <= column && index2 <= row &&
                    Grid(gridView1, index1, index2) != grid
                ) {
                    //不能在有敌人的地方生成网格
                    if (Grid(gridView1, index1, index2) != enemyVector0.grid &&
                        Grid(gridView1, index1, index2) != enemyVector1.grid &&
                        Grid(gridView1, index1, index2) != enemyVector2.grid &&
                        Grid(gridView1, index1, index2) != heroVector.grid
                    ) {
                        generateGreenGridAt(Grid(gridView1, index1, index2))
                        grids.add(Grid(gridView1, index1, index2))
                    }
                }
            }
        }
    }

    /**
     * @param grid 在哪个网格生成
     * 在某个网格生成网格
     */
    private fun generateGreenGridAt(grid: Grid) {
        val greenRect = MyImageView(this)
        greenRect.id = R.id.id_greenRect
        greenRect.layoutParams = ViewGroup.LayoutParams(grid.width.toInt(), grid.height.toInt())
        greenRect.x = Grid(gridView1, grid.gridX, grid.gridY).getX()
        greenRect.y = Grid(gridView1, grid.gridX, grid.gridY).getY()
        greenRect.setImageResource(R.drawable.round_rect)
        layout.addView(greenRect)
    }

    /**
     * 英雄（狼）的初始化
     */
    fun initWolf() {
        heroVector = MyImageViewFactory.generateHero(
            R.drawable.hero, CharacterParams(180, 60, 35), Grid(gridView1, 4, 3),
            layout, this
        )
        mpText.text = "剩余体力：" + heroVector.mp.toString()
    }

    private fun generateEnemy() {
        enemyVector0 = MyImageViewFactory.generateEnemy(
            R.drawable.northbear, CharacterParams(250, 70, 25), Grid(gridView1, 7, 1),
            layout, this, 0
        )
        enemyVector1 = MyImageViewFactory.generateEnemy(
            R.drawable.northbear, CharacterParams(170, 65, 28), Grid(gridView1, 7, 2),
            layout, this, 1
        )
        enemyVector2 = MyImageViewFactory.generateEnemy(
            R.drawable.northbear, CharacterParams(160, 65, 28), Grid(gridView1, 7, 4),
            layout, this, 1
        )
        enemies.add(enemyVector0)
        enemies.add(enemyVector1)
        enemies.add(enemyVector2)
    }

    fun generateSkillField(intro: String) {
        val btn1Info = TextView(this)
        btn1Info.apply {
            id = R.id.id_skillfield
            layoutParams = ViewGroup.LayoutParams(
                (Grid(gridView1, 1, 1).width * 2).toInt(),
                (Grid(gridView1, 1, 1).height * 2).toInt()
            )
            setBackgroundResource(R.drawable.skillfield)
            x = Grid(gridView1, 1, 3).getX()
            y = Grid(gridView1, 1, 3).getY()
            text = intro
            gravity = Gravity.CENTER
        }
        layout.addView(btn1Info)
    }

    fun removeGreen(times: Int) {
        for (index in 1..times) {
            layout.removeView(findViewById(R.id.id_greenRect))
        }
    }

    /**
     * 使下面的所有按钮可见
     */
    private fun buttonVisible() {
        button1.visibility = View.VISIBLE
        button2.visibility = View.VISIBLE
        button3.visibility = View.VISIBLE
        button4.visibility = View.VISIBLE
        button5.visibility = View.VISIBLE
        button6.visibility = View.VISIBLE
        mpText.visibility = View.VISIBLE
        button_cancel.visibility = View.INVISIBLE
    }

    private fun buttonInvisible() {
        button1.visibility = View.INVISIBLE
        button2.visibility = View.INVISIBLE
        button3.visibility = View.INVISIBLE
        button4.visibility = View.INVISIBLE
        button5.visibility = View.INVISIBLE
        button6.visibility = View.INVISIBLE
        mpText.visibility = View.INVISIBLE
        button_cancel.visibility = View.VISIBLE
        button_cancel.bringToFront()
    }

    private fun generateDescriptionField(word: String, time: Long) {
        val descriptionField = TextView(this)
        descriptionField.apply {
            id = R.id.id_descriptionfield
            layoutParams = ViewGroup.LayoutParams(
                (Grid(gridView1, 1, 1).width * 2).toInt(),
                (Grid(gridView1, 1, 1).height / 3).toInt()
            )
            setBackgroundResource(R.drawable.skillfield)
            x = Grid(gridView1, 3, 1).getX() + Grid(gridView1, 2, 1).width / 2
            y = Grid(gridView1, 2, 1).getY()
            text = word
            gravity = Gravity.CENTER
        }
        layout.addView(descriptionField)
        thread {
            val message = Message()
            message.what = deleteDescriptionField
            Thread.sleep(time)
            handler.sendMessage(message)
        }
    }

    /**
     * index取值0-2
     * turn取值1-3
     * 敌人回合算法
     */
    private fun enemyAct(index: Int, turn: Int) {
        var information = 0
        //随便初始化一下
        var enemyVector = EnemyVector(
            MyImageView(this), MyImageView(this), MyImageView(this),
            TextView(this), CharacterParams
                (1, 1, 1), Grid
                (gridView1, 1, 1), 1
        )
        var endInformation = 0
        when (index) {
            0 -> {
                when (turn) {
                    1 -> information = enemy0Act1End
                    2 -> information = enemy0Act2End
                    3 -> information = enemy0Act3End
                }
            }
            1 -> {
                when (turn) {
                    1 -> information = enemy1Act1End
                    2 -> information = enemy1Act2End
                    3 -> information = enemy1Act3End
                }
            }
            2 -> {
                when (turn) {
                    1 -> information = enemy2Act1End
                    2 -> information = enemy2Act2End
                    3 -> information = enemy2Act3End
                }
            }
        }
        when (index) {
            0 -> {
                enemyVector = enemyVector0
                endInformation = enemy0Act3End
            }
            1 -> {
                enemyVector = enemyVector1
                endInformation = enemy1Act3End
            }
            2 -> {
                enemyVector = enemyVector2
                endInformation = enemy2Act3End
            }
        }
        if (!enemyVector.dead) {
            if (index == 0) {
                enemyVector.jump(300)
                thread {
                    val message = Message()
                    message.what = jumpEnd
                    Thread.sleep(300)
                    handler.sendMessage(message)
                }
                thread {
                    val message = Message()
                    message.what = endInformation
                    Thread.sleep(2000)
                    handler.sendMessage(message)
                }
                thread {
                    val message = Message()
                    message.what = judgeHeroDeath
                    Thread.sleep(2000)
                    handler.sendMessage(message)
                }
            } else if (enemyVector.grid.radiusContain(3, heroVector.grid)) {
                enemyVector.throne(
                    this, layout, heroVector, heroVector.grid, (enemyVector.attack * 0.8).toInt(),
                    kingShieldOn
                )
                if (kingShieldOn) {
                    kingShieldOn = false
                }
                thread {
                    val message = Message()
                    message.what = endInformation
                    Thread.sleep(800)
                    handler.sendMessage(message)
                }
                thread {
                    val message = Message()
                    message.what = judgeHeroDeath
                    Thread.sleep(800)
                    handler.sendMessage(message)
                }
            } else {
                if (enemyVector.moveToHero(heroVector, gridView1, enemies)) {
                    thread {
                        val message = Message()
                        message.what = information
                        Thread.sleep(800)
                        handler.sendMessage(message)
                    }
                } else {
                    thread {
                        val message = Message()
                        message.what = information
                        handler.sendMessage(message)
                    }
                }
            }
        } else {
            thread {
                val message = Message()
                message.what = endInformation
                handler.sendMessage(message)
            }
        }
    }

    private fun enemyActEnd(index: Int, turn: Int) {
        val maxIndex = 2
        var enemyVector = EnemyVector(
            MyImageView(this), MyImageView(this), MyImageView(this),
            TextView(this), CharacterParams
                (1, 1, 1), Grid
                (gridView1, 1, 1), 1
        )
        var nextEnemyVector = EnemyVector(
            MyImageView(this), MyImageView(this), MyImageView(this),
            TextView(this), CharacterParams
                (1, 1, 1), Grid
                (gridView1, 1, 1), 1
        )
        when (index) {
            0 -> {
                enemyVector = enemyVector0
                nextEnemyVector = enemyVector1
            }
            1 -> {
                enemyVector = enemyVector1
                nextEnemyVector = enemyVector2
            }
            2 -> {
                enemyVector = enemyVector2
            }
        }
        if (index == maxIndex) {
            if (turn == 1 || turn == 2) {
                if (!enemyVector.dead) {
                    enemyAct(maxIndex, turn + 1)
                } else {
                    buttonVisible()
                    heroVector.mp = 8
                    mpText.text = "剩余体力：" + "${heroVector.mp}"
                }
            } else {
                buttonVisible()
                heroVector.mp = 8
                mpText.text = "剩余体力：" + "${heroVector.mp}"
            }
        } else if (index == maxIndex - 1) {
            if (turn == 1 || turn == 2) {
                if (!enemyVector.dead) {
                    enemyAct(index, turn + 1)
                } else {
                    enemyAct(index + 1, 1)
                }
            } else {
                if (!nextEnemyVector.dead) {
                    enemyAct(index + 1, 1)
                } else {
                    buttonVisible()
                    heroVector.mp = 8
                    mpText.text = "剩余体力：" + "${heroVector.mp}"
                }
            }
        } else {
            if (turn == 1 || turn == 2) {
                if (!enemyVector.dead) {
                    enemyAct(index, turn + 1)
                } else {
                    enemyAct(index + 1, 1)
                }
            } else {
                if (!nextEnemyVector.dead) {
                    enemyAct(index + 1, 1)
                } else {
                    enemyAct(index + 2, 1)
                }
            }
        }
    }

    fun victory() {
        thread {
            val message = Message()
            message.what = victoryInformation
            Thread.sleep(300)
            handler.sendMessage(message)
        }
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