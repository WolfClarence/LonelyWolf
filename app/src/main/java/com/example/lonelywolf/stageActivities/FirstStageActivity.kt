package com.example.lonelywolf.stageActivities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.*
import android.widget.TextView
import com.example.lonelywolf.base.MyImageView
import com.example.lonelywolf.R
import com.example.lonelywolf.base.MyActivity
import com.example.lonelywolf.characterControl.CharacterParams
import com.example.lonelywolf.grid.Grid
import com.example.lonelywolf.characterControl.EnemyVector
import com.example.lonelywolf.characterControl.HeroVector
import com.example.lonelywolf.characterControl.MyImageViewFactory
import com.example.lonelywolf.otherActivities.DefeatActivity
import com.example.lonelywolf.otherActivities.StageChooseActivity
import com.example.lonelywolf.stageEndActivities.FirstStageEndActivity
import kotlinx.android.synthetic.main.activity_first_stage.*
import kotlinx.android.synthetic.main.activity_first_stage.button1
import kotlinx.android.synthetic.main.activity_first_stage.button2
import kotlinx.android.synthetic.main.activity_first_stage.button3
import kotlinx.android.synthetic.main.activity_first_stage.button4
import kotlinx.android.synthetic.main.activity_first_stage.button5
import kotlinx.android.synthetic.main.activity_first_stage.button_cancel
import kotlinx.android.synthetic.main.activity_first_stage.gridView1
import kotlinx.android.synthetic.main.activity_first_stage.layout
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 */
class FirstStageActivity : MyActivity() {
    //这些boolean变量用于控制点击事件
    private var heroMoveEnabled = false
    private var attackEnabled = false
    private var whirlWindEnabled = false
    private var sprintEnabled = false
    private var ghostStrikeEnabled = false
    private var fenghuangFlyEnabled = false

    //储存绿色方形网格的链表
    private var grids = LinkedList<Grid>()
    val row = 5
    val column = 7

    //给handler传递信息的常量
    val init = 1
    val deleteHero = 3
    val heroActEnd = 4
    val enemy0Act1End = 6
    val enemy0Act2End = 11
    val enemy0Act3End = 12
    val enemy1Act1End = 7
    val enemy1Act2End = 13
    val enemy1Act3End = 14
    val deleteWhirlWind = 8
    val judgeDead = 9
    val deleteDescriptionField = 10
    val doGhostStrike0 = 15
    val doGhostStrike1 = 16
    val victoryInformation = 20

    //敌人回合储存链表
    private var enemies = ArrayList<EnemyVector>()

    //英雄容器和敌人容器
    lateinit var heroVector: HeroVector
    lateinit var enemyVector0: EnemyVector
    lateinit var enemyVector1: EnemyVector

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
                    val intent = Intent(this@FirstStageActivity, DefeatActivity::class.java)
                    startActivity(intent)
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
                //
                judgeDead -> {
                    if (enemyVector0.dead) {
                        enemyVector0.disappear(layout)
                        enemyVector0.grid = Grid(gridView1, 10, 10)
                    }
                    if (enemyVector1.dead) {
                        enemyVector1.disappear(layout)
                        enemyVector1.grid = Grid(gridView1, 10, 10)
                    }
                    if (enemyVector0.dead && enemyVector1.dead) {
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
                        enemyVector0.grid, gridView1, this@FirstStageActivity, layout, enemyVector0,
                        enemies, (heroVector.attack * 1.5 + 15).toInt()
                    )
                    mpText.text = "剩余体力：" + heroVector.mp.toString()
                }
                doGhostStrike1 -> {
                    val enemies = ArrayList<EnemyVector>()
                    enemies.add(enemyVector0)
                    heroVector.ghostStrike(
                        enemyVector1.grid, gridView1, this@FirstStageActivity, layout, enemyVector1,
                        enemies, (heroVector.attack * 1.5 + 15).toInt()
                    )
                    mpText.text = "剩余体力：" + heroVector.mp.toString()
                }
                victoryInformation -> {
                    val intent = Intent(this@FirstStageActivity, FirstStageEndActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_stage)
        //对系统状态栏进行设置，隐藏系统状态栏
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        overridePendingTransition(0, 0)//覆盖即将到来的跳转动画

        thread {
            val message = Message()
            message.what = init
            Thread.sleep(100)
            handler.sendMessage(message)
        }

        //极寒之核
        button1.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField(
                        "技能：极寒之核\n 向一名敌人发射法球，造成${(0.8 * heroVector.attack + 10).toInt()}" +
                                "(攻击力*0.8+10)点伤害\n 消耗：2体力"
                    )
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 2) {
                        attackEnabled = true
                        buttonInvisible()
                        if (!enemyVector0.dead) {
                            generateGreenGridAt(enemyVector0.grid)
                        }
                        if (!enemyVector1.dead) {
                            generateGreenGridAt(enemyVector1.grid)
                        }
                    } else {
                        generateDescriptionField("体力不够!", 1000)
                    }
                }
            }
            return@setOnTouchListener true
        }
        //快速拔枪
        button2.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField("技能：快速拔枪\n向一个网格移动,然后永久增加2点攻击和1点护甲\n消耗：1体力")
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 1) {
                        generateGreenGridAround(heroVector.grid)
                        heroMoveEnabled = true
                    } else {
                        generateDescriptionField("体力不够!", 1000)
                    }
                }
            }
            return@setOnTouchListener true
        }
        //深境螺旋
        button3.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField(
                        "技能：深境螺旋\n制造一个螺旋，对周围所有人造成${20 + (heroVector.attack * 1.1).toInt()}" +
                                "（攻击力*1.1+20）点伤害\n消耗：2体力"
                    )
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 2) {
                        whirlWindEnabled = true
                        for (index1 in heroVector.grid.gridX - 1..heroVector.grid.gridX + 1) {
                            for (index2 in heroVector.grid.gridY - 1..heroVector.grid.gridY + 1) {
                                if (index1 >= 1 && index2 >= 1 && index1 <= column && index2 <= row &&
                                    Grid(gridView1, index1, index2) != heroVector.grid
                                ) {
                                    generateGreenGridAt(Grid(gridView1, index1, index2))
                                    grids.add(Grid(gridView1, index1, index2))
                                }
                            }
                        }
                        buttonInvisible()
                    } else {
                        generateDescriptionField("体力不够!", 1000)
                    }
                }
            }
            return@setOnTouchListener true
        }
        //极寒突刺
        button4.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField(
                        "技能：极寒突刺\n向一个3格内的地点冲刺，\n对触碰到的" +
                                "所有人造成${(heroVector.attack * 0.5 + 60).toInt()}(0.5*攻击力+60)点伤害\n消耗：3体力"
                    )
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 3) {
                        sprintEnabled = true
                        for (index1 in heroVector.grid.gridX - 3..heroVector.grid.gridX + 3) {
                            for (index2 in heroVector.grid.gridY - 3..heroVector.grid.gridY + 3) {
                                if (index1 >= 1 && index2 >= 1 && index1 <= column && index2 <= row &&
                                    Grid(gridView1, index1, index2) != heroVector.grid &&
                                    Grid(gridView1, index1, index2) != enemyVector0.grid &&
                                    Grid(gridView1, index1, index2) != enemyVector1.grid
                                ) {
                                    generateGreenGridAt(Grid(gridView1, index1, index2))
                                    grids.add(Grid(gridView1, index1, index2))
                                }
                            }
                        }
                        buttonInvisible()
                    } else {
                        generateDescriptionField("体力不够!", 1000)
                    }
                }
            }
            return@setOnTouchListener true
        }
        //涌泉之恨
        button5.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField(
                        "技能：涌泉之恨\n向一个3格内的敌人突袭，\n" +
                                "造成${(heroVector.attack * 1.5 + 15).toInt()}(1.5*攻击力+15)点伤害\n消耗：2体力"
                    )
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 2) {
                        if (heroVector.grid.radiusContain(3, enemyVector0.grid) ||
                            heroVector.grid.radiusContain(3, enemyVector1.grid)
                        ) {
                            ghostStrikeEnabled = true
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
                            buttonInvisible()
                        } else {
                            generateDescriptionField("攻击距离3，无可选中目标！", 1000)
                        }
                    } else {
                        generateDescriptionField("体力不够!", 1000)
                    }
                }
            }
            return@setOnTouchListener true
        }
        //凤舞九天
        button6.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField(
                        "终结技：凤舞九天\n坠鹏觉醒凤凰血脉，燃烧生命释放凤凰之火，\n" +
                                "造成${(heroVector.attack * 2.5 + 50).toInt()}(2.5*攻击力+50)点伤害\n并且损失40点生命\n" +
                                "消耗：8体力"
                    )
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 8 && heroVector.hp > 50) {
                        fenghuangFlyEnabled = true
                        for (index1 in heroVector.grid.gridX - 6..heroVector.grid.gridX + 6) {
                            for (index2 in heroVector.grid.gridY - 4..heroVector.grid.gridY + 4) {
                                if (index1 >= 1 && index2 >= 1 && index1 <= column && index2 <= row &&
                                    Grid(gridView1, index1, index2) != heroVector.grid
                                ) {
                                    generateGreenGridAt(Grid(gridView1, index1, index2))
                                    grids.add(Grid(gridView1, index1, index2))
                                }
                            }
                        }
                        buttonInvisible()
                    } else if (heroVector.mp < 8) {
                        generateDescriptionField("体力不够!", 1000)
                    } else {
                        generateDescriptionField("再放我就要死了！", 1000)
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
                layout.removeView(findViewById(R.id.id_greenRect))
                layout.removeView(findViewById(R.id.id_greenRect))
                attackEnabled = false
                buttonVisible()
            }
            if (whirlWindEnabled) {
                for (index in 0 until grids.size) {
                    layout.removeView(findViewById(R.id.id_greenRect))
                }
                grids.clear()
                whirlWindEnabled = false
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
            if (ghostStrikeEnabled) {
                layout.removeView(findViewById(R.id.id_greenRect))
                layout.removeView(findViewById(R.id.id_greenRect))
                ghostStrikeEnabled = false
                buttonVisible()
            }
            if (fenghuangFlyEnabled) {
                for (index in 0 until grids.size) {
                    layout.removeView(findViewById(R.id.id_greenRect))
                }
                grids.clear()
                fenghuangFlyEnabled = false
                buttonVisible()
            }
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (heroVector.grid.contains(event.x, event.y)) {
                    generateSkillField(
                        "大将军坠鹏\n攻击：${heroVector.attack}\n护甲：${heroVector.defence}\n" +
                                "减免${((1 - 100.0 / (100 + heroVector.defence)) * 100).toInt()}%的伤害\n" + "剩余体力：${heroVector.mp}"
                    )
                }
                if (enemyVector0.grid.contains(event.x, event.y)) {
                    generateSkillField(
                        "牛辟\n攻击：${enemyVector0.attack}\n护甲：${enemyVector0.defence}\n" +
                                "减免${((1 - 100.0 / (100 + enemyVector0.defence)) * 100).toInt()}%的伤害"
                    )
                }
                if (enemyVector1.grid.contains(event.x, event.y)) {
                    generateSkillField(
                        "牛紫\n攻击：${enemyVector1.attack}\n护甲：${enemyVector1.defence}\n" +
                                "减免${((1 - 100.0 / (100 + enemyVector1.defence)) * 100).toInt()}%的伤害"
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
                            heroVector.attack += 2
                            heroVector.defence += 1
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
                    if (!enemyVector0.dead) {
                        if (enemyVector0.grid.contains(event.x, event.y)) {
                            heroVector.shot(
                                this,
                                layout,
                                enemyVector0,
                                enemyVector0.grid,
                                (0.8 * heroVector.attack + 10).toInt()
                            )
                            mpText.text = "剩余体力：" + heroVector.mp.toString()
                            layout.removeView(findViewById(R.id.id_greenRect))
                            if (!enemyVector1.dead) {
                                layout.removeView(findViewById(R.id.id_greenRect))
                            }
                            thread {
                                val message = Message()
                                message.what = judgeDead
                                Thread.sleep(800)
                                handler.sendMessage(message)
                            }
                            if (heroVector.mp == 0) {
                                button_cancel.visibility = View.INVISIBLE
                                thread {
                                    val message = Message()
                                    message.what = heroActEnd
                                    Thread.sleep(1200)
                                    handler.sendMessage(message)
                                }
                            } else {
                                buttonVisible()
                            }
                            attackEnabled = false
                        }
                    }

                    if (!enemyVector1.dead) {
                        if (enemyVector1.grid.contains(event.x, event.y)) {
                            heroVector.shot(
                                this,
                                layout,
                                enemyVector1,
                                enemyVector1.grid,
                                (0.8 * heroVector.attack + 10).toInt()
                            )
                            mpText.text = "剩余体力：" + heroVector.mp.toString()
                            layout.removeView(findViewById(R.id.id_greenRect))
                            if (!enemyVector1.dead) {
                                layout.removeView(findViewById(R.id.id_greenRect))
                            }
                            thread {
                                val message = Message()
                                message.what = judgeDead
                                Thread.sleep(1000)
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
        if (whirlWindEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    for (grid: Grid in grids) {
                        if (grid.contains(event.x, event.y)) {
                            for (index in 0 until grids.size) {
                                layout.removeView(findViewById(R.id.id_greenRect))
                            }
                            grids.clear()
                            heroVector.whirlWindKill(
                                this, layout, enemyVector0, enemyVector1,
                                20 + (heroVector.attack * 1.1).toInt()
                            )
                            mpText.text = "剩余体力：" + heroVector.mp.toString()
                            buttonVisible()
                            whirlWindEnabled = false
                            thread {
                                val message = Message()
                                message.what = deleteWhirlWind
                                Thread.sleep(350)
                                handler.sendMessage(message)
                            }
                            if (heroVector.mp == 0) {
                                button_cancel.visibility = View.INVISIBLE
                                thread {
                                    val message = Message()
                                    message.what = heroActEnd
                                    Thread.sleep(700)
                                    handler.sendMessage(message)
                                }
                            }
                            thread {
                                val message = Message()
                                message.what = judgeDead
                                Thread.sleep(1000)
                                handler.sendMessage(message)
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
                            val enemyVectors = ArrayList<EnemyVector>()
                            enemyVectors.add(enemyVector0)
                            enemyVectors.add(enemyVector1)
                            heroVector.sprint(
                                grid,
                                enemyVectors,
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
                                    Thread.sleep(700)
                                    handler.sendMessage(message)
                                }
                            }
                            thread {
                                val message = Message()
                                message.what = judgeDead
                                Thread.sleep(800)
                                handler.sendMessage(message)
                            }
                        }
                    }
                }
            }
        }
        if (ghostStrikeEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!enemyVector0.dead) {
                        if (enemyVector0.grid.contains(event.x, event.y)) {
                            heroVector.goToVoid(300)
                            thread {
                                val message = Message()
                                message.what = doGhostStrike0
                                Thread.sleep(300)
                                handler.sendMessage(message)
                            }
                            layout.removeView(findViewById(R.id.id_greenRect))
                            if (!enemyVector1.dead) {
                                layout.removeView(findViewById(R.id.id_greenRect))
                            }
                            thread {
                                val message = Message()
                                message.what = judgeDead
                                Thread.sleep(1000)
                                handler.sendMessage(message)
                            }
                            if (heroVector.mp == 2) {
                                button_cancel.visibility = View.INVISIBLE
                                thread {
                                    val message = Message()
                                    message.what = heroActEnd
                                    Thread.sleep(1600)
                                    handler.sendMessage(message)
                                }
                            } else {
                                buttonVisible()
                            }
                            ghostStrikeEnabled = false
                        }
                    }

                    if (!enemyVector1.dead) {
                        if (enemyVector1.grid.contains(event.x, event.y)) {
                            heroVector.goToVoid(300)
                            thread {
                                val message = Message()
                                message.what = doGhostStrike1
                                Thread.sleep(300)
                                handler.sendMessage(message)
                            }
                            layout.removeView(findViewById(R.id.id_greenRect))
                            if (!enemyVector0.dead) {
                                layout.removeView(findViewById(R.id.id_greenRect))
                            }
                            thread {
                                val message = Message()
                                message.what = judgeDead
                                Thread.sleep(1000)
                                handler.sendMessage(message)
                            }
                            if (heroVector.mp == 2) {
                                button_cancel.visibility = View.INVISIBLE
                                thread {
                                    val message = Message()
                                    message.what = heroActEnd
                                    Thread.sleep(1600)
                                    handler.sendMessage(message)
                                }
                            } else {
                                buttonVisible()
                            }
                            ghostStrikeEnabled = false
                        }
                    }
                }
            }
        }
        if (fenghuangFlyEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    heroVector.fenghuangFly(this, enemies, layout)
                    mpText.text = "剩余体力：" + heroVector.mp.toString()
                    fenghuangFlyEnabled = false
                    for (grid: Grid in grids) {
                        layout.removeView(findViewById(R.id.id_greenRect))
                    }
                    grids.clear()
                    for (enemyVector: EnemyVector in enemies) {
                        enemyVector.hpDecrease((heroVector.attack * 2.5 + 50).toInt(), this, layout)
                    }
                    thread {
                        val message = Message()
                        message.what = judgeDead
                        Thread.sleep(3000)
                        handler.sendMessage(message)
                    }
                    if (heroVector.mp == 0) {
                        button_cancel.visibility = View.INVISIBLE
                        thread {
                            val message = Message()
                            message.what = heroActEnd
                            Thread.sleep(3500)
                            handler.sendMessage(message)
                        }
                    } else {
                        buttonVisible()
                    }
                }
            }
        }
        return true
    }

    /**
     * 生成敌人
     */
    private fun generateEnemy() {
        enemyVector0 = MyImageViewFactory.generateEnemy(
            R.drawable.maoniu, CharacterParams(260, 41, 25), Grid(gridView1, 6, 1),
            layout, this, 0
        )
        enemyVector1 = MyImageViewFactory.generateEnemy(
            R.drawable.maoniu, CharacterParams(300, 36, 28), Grid(gridView1, 6, 2),
            layout, this, 1
        )
        enemies.add(enemyVector0)
        enemies.add(enemyVector1)
    }

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
            R.drawable.jiangjunma, CharacterParams(100, 30, 35), Grid(gridView1, 1, 1),
            layout, this
        )
        mpText.text = "剩余体力：" + heroVector.mp.toString()
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
        }
        if (!enemyVector.dead) {
            if (enemyVector.grid.radiusContain(1, heroVector.grid)) {
                enemyVector.bite(heroVector, (enemyVector.attack), false, this, layout)
                thread {
                    val message = Message()
                    message.what = endInformation
                    Thread.sleep(800)
                    handler.sendMessage(message)
                }
                if (heroVector.dead) {
                    thread {
                        val message = Message()
                        message.what = deleteHero
                        Thread.sleep(800)
                        handler.sendMessage(message)
                    }
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
        val maxIndex = 1
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
            }
        }
        if (index == maxIndex) {
            if (turn == 1 || turn == 2) {
                if (!enemyVector.dead) {
                    enemyAct(maxIndex, turn + 1)
                } else {
                    buttonVisible()
                    heroVector.mp = 8
                    mpText.text = "剩余体力：" + heroVector.mp.toString()
                }
            } else {
                buttonVisible()
                heroVector.mp = 8
                mpText.text = "剩余体力：" + heroVector.mp.toString()
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
                    mpText.text = "剩余体力：" + heroVector.mp.toString()
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
            AlertDialog.Builder(this@FirstStageActivity).setTitle("提示")
                .setIcon(R.drawable.exit)
                .setMessage("确定要退出吗?")
                .setPositiveButton(
                    "确认"
                ) { dialog, which ->
                    val intent = Intent(this, StageChooseActivity::class.java)
                    startActivity(intent)

                    this@FirstStageActivity.finish()
                }
                .setNegativeButton("取消", null)
                .create().show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}