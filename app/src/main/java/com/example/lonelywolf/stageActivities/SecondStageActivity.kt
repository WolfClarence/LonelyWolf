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
import kotlinx.android.synthetic.main.activity_second_stage.*
import kotlinx.android.synthetic.main.activity_second_stage.button1
import kotlinx.android.synthetic.main.activity_second_stage.button2
import kotlinx.android.synthetic.main.activity_second_stage.button3
import kotlinx.android.synthetic.main.activity_second_stage.button4
import kotlinx.android.synthetic.main.activity_second_stage.button5
import kotlinx.android.synthetic.main.activity_second_stage.button_cancel
import kotlinx.android.synthetic.main.activity_second_stage.gridView1
import kotlinx.android.synthetic.main.activity_second_stage.layout
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 */
class SecondStageActivity : MyActivity() {
    //这些boolean变量用于控制点击事件
    private var heroMoveEnabled = false
    private var attackEnabled = false
    private var sprintEnabled = false
    private var kingSwordEnabled = false
    private var kingShieldEnabled = false
    private var kingShieldOn = false
    private var kingNecromancyEnabled = false
    private var cureEnabled = false
    private var whirlWindEnabled = false
    private var circleOfDeathEnabled = false

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
    val deleteDescriptionField = 12
    val doGhostStrike0 = 13
    val doGhostStrike1 = 14
    val enemy2Act1End = 15
    val enemy2Act2End = 16
    val enemy2Act3End = 17
    val enemy3Act1End = 18
    val enemy3Act2End = 19
    val enemy3Act3End = 20
    val deleteBlood = 21
    val victoryInformation = 22
    val deleteWave = 23
    val judgeDeath = 24

    //敌人回合储存链表
    private var enemies = ArrayList<EnemyVector>()

    //英雄容器和敌人容器
    lateinit var heroVector: HeroVector
    lateinit var enemyVector0: EnemyVector
    lateinit var enemyVector1: EnemyVector
    lateinit var enemyVector2: EnemyVector
    lateinit var enemyVector3: EnemyVector

    //handler，作用是开启新的线程延时对UI进行操作
    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        @SuppressLint("CutPasteId", "Recycle")
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                judgeDeath -> {
                    if (enemyVector0.dead) {
                        enemyVector0.disappear(layout)
                        enemyVector0.grid = Grid(gridView1, 10, 10)
                        enemies.remove(enemyVector0)
                    }
                    if (enemyVector1.dead) {
                        enemyVector1.disappear(layout)
                        enemyVector1.grid = Grid(gridView1, 10, 10)
                        enemies.remove(enemyVector1)
                    }
                    if (enemyVector2.dead) {
                        enemyVector2.disappear(layout)
                        enemyVector2.grid = Grid(gridView1, 10, 10)
                        enemies.remove(enemyVector2)
                    }
                    if (enemyVector3.dead) {
                        enemyVector3.disappear(layout)
                        enemyVector3.grid = Grid(gridView1, 10, 10)
                        enemies.remove(enemyVector3)
                    }
                    if (enemyVector0.dead && enemyVector1.dead && enemyVector2.dead && enemyVector3.dead) {
                        victory()
                    }
                }
                deleteWave -> {
                    layout.removeView(findViewById(R.id.id_waveSpin))
                }
                init -> {
                    generateEnemy()
                    initWolf()
                }
                deleteHero -> {
                    heroVector.disappear(layout)
                    val intent = Intent(this@SecondStageActivity, DefeatActivity::class.java)
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
                enemy2Act1End -> {
                    enemyActEnd(2, 1)
                }
                enemy2Act2End -> {
                    enemyActEnd(2, 2)
                }
                enemy2Act3End -> {
                    enemyActEnd(2, 3)
                }
                enemy3Act1End -> {
                    enemyActEnd(3, 1)
                }
                enemy3Act2End -> {
                    enemyActEnd(3, 2)
                }
                enemy3Act3End -> {
                    enemyActEnd(3, 3)
                }

                deleteDescriptionField -> {
                    layout.removeView(findViewById(R.id.id_descriptionfield))
                }
                doGhostStrike0 -> {
                    val enemies = ArrayList<EnemyVector>()
                    enemies.add(enemyVector1)
                    heroVector.ghostStrike(
                        enemyVector0.grid,
                        gridView1,
                        this@SecondStageActivity,
                        layout,
                        enemyVector0,
                        enemies,
                        (heroVector.attack * 1.5 + 15).toInt()
                    )
                }
                doGhostStrike1 -> {
                    val enemies = ArrayList<EnemyVector>()
                    enemies.add(enemyVector0)
                    heroVector.ghostStrike(
                        enemyVector1.grid,
                        gridView1,
                        this@SecondStageActivity,
                        layout,
                        enemyVector1,
                        enemies,
                        (heroVector.attack * 1.5 + 15).toInt()
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
                    val intent = Intent(this@SecondStageActivity, VictoryActivity::class.java)
                    startActivity(intent)
                    ActivityCollector.finishOneActivity(SecondStageActivity::class.java.name)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_stage)
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
        //生命药水
        button2.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField("治疗药水\n鸿裕喝下一瓶治疗药水，为自己治疗20点生命值\n消耗：1体力")
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 1) {
                        cureEnabled = true
                        generateGreenGridAt(heroVector.grid)
                        buttonInvisible()
                    } else {
                        generateDescriptionField("体力不够", 300)
                    }
                }
            }
            return@setOnTouchListener true
        }
        //石块攻击
        button3.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField(
                        "石块攻击\n鸿裕扔出一块石头，造成${(heroVector.attack * 0.8 + 10).toInt()}" +
                                "(0.8*攻击力+10)点伤害\n消耗：2体力"
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
                        if (!enemyVector3.dead) {
                            generateGreenGridAt(enemyVector3.grid)
                        }
                        buttonInvisible()
                    } else {
                        generateDescriptionField("体力不够", 300)
                    }
                }
            }
            return@setOnTouchListener true
        }
        //水影旋
        button4.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField(
                        "水影旋\n 将内力化为水之波纹，释放水影旋，对大范围敌人造成${(0.6 * heroVector.attack + 10).toInt()}" +
                                "(攻击力*6+10)点伤害\n 消耗：2体力"
                    )
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 2) {
                        whirlWindEnabled = true
                        if (!enemyVector0.dead) {
                            generateGreenGridAt(enemyVector0.grid)
                        }
                        if (!enemyVector1.dead) {
                            generateGreenGridAt(enemyVector1.grid)
                        }
                        if (!enemyVector2.dead) {
                            generateGreenGridAt(enemyVector2.grid)
                        }
                        if (!enemyVector3.dead) {
                            generateGreenGridAt(enemyVector3.grid)
                        }
                        buttonInvisible()
                    } else {
                        generateDescriptionField("体力不够!", 1000)
                    }
                }
            }
            return@setOnTouchListener true
        }
        //法阵
        button5.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    generateSkillField(
                        "魔术幻境\n鸿裕燃烧血液释放幻术，\n将所有敌人带入他的法阵，造成\n${(heroVector.attack * 0.8 + 20).toInt()}" +
                                "(攻击力*0.8+20)点伤害,\n并且损失20点生命 " +
                                "消耗：4体力"
                    )
                }
                MotionEvent.ACTION_UP -> {
                    layout.removeView(findViewById(R.id.id_skillfield))
                    if (heroVector.mp >= 4 && heroVector.hp > 20) {
                        circleOfDeathEnabled = true
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
                    } else if (heroVector.mp < 4) {
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
                removeGreen(4)
                attackEnabled = false
                buttonVisible()
            }
            if (circleOfDeathEnabled) {
                removeGreen(35)
                circleOfDeathEnabled = false
                buttonVisible()
            }
            if (whirlWindEnabled) {
                removeGreen(4)
                whirlWindEnabled = false
                buttonVisible()
            }
            if (cureEnabled) {
                removeGreen(1)
                cureEnabled = false
                buttonVisible()
            }
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (heroVector.grid.contains(event.x, event.y)) {
                    generateSkillField(
                        "少年鸿裕\n攻击：${heroVector.attack}\n护甲：${heroVector.defence}\n" +
                                "减免${((1 - 100.0 / (100 + heroVector.defence)) * 100).toInt()}%的伤害\n" +
                                "剩余体力：${heroVector.mp}"
                    )
                }
                if (enemyVector0.grid.contains(event.x, event.y)) {
                    generateSkillField(
                        "刺客A\n攻击：${enemyVector0.attack}\n护甲：${enemyVector0.defence}\n" +
                                "减免${((1 - 100.0 / (100 + enemyVector0.defence)) * 100).toInt()}%的伤害"
                    )
                }
                if (enemyVector1.grid.contains(event.x, event.y)) {
                    generateSkillField(
                        "刺客B\n攻击：${enemyVector1.attack}\n护甲：${enemyVector1.defence}\n" +
                                "减免${((1 - 100.0 / (100 + enemyVector1.defence)) * 100).toInt()}%的伤害"
                    )
                }
                if (enemyVector2.grid.contains(event.x, event.y)) {
                    generateSkillField(
                        "刺客C\n攻击：${enemyVector2.attack}\n护甲：${enemyVector2.defence}\n" +
                                "减免${((1 - 100.0 / (100 + enemyVector2.defence)) * 100).toInt()}%的伤害"
                    )
                }
                if (enemyVector3.grid.contains(event.x, event.y)) {
                    generateSkillField(
                        "刺客D\n攻击：${enemyVector3.attack}\n护甲：${enemyVector3.defence}\n" +
                                "减免${((1 - 100.0 / (100 + enemyVector3.defence)) * 100).toInt()}%的伤害"
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
                            mpText.text = "剩余体力：" + "${heroVector.mp}"
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
        if (cureEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (heroVector.grid.contains(event.x, event.y)) {
                        heroVector.energyStrengthen(heroVector.grid, this, layout)
                        mpText.text = "剩余体力：" + "${heroVector.mp}"
                        cureEnabled = false
                        layout.removeView(findViewById(R.id.id_greenRect))
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
                                heroVector.throwStone(
                                    this,
                                    layout,
                                    enemyVector,
                                    enemyVector.grid,
                                    (heroVector.attack * 0.8 + 10).toInt()
                                )
                                mpText.text = "剩余体力：" + "${heroVector.mp}"
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
        if (whirlWindEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    grids.clear()
                    for (enemyVector: EnemyVector in enemies) {
                        if (!enemyVector.dead) {
                            if (enemyVector.grid.contains(event.x, event.y)) {
                                heroVector.mp -= 2
                                for (enemy: EnemyVector in enemies) {
                                    if (enemyVector.grid.radiusContain(1, enemy.grid)) {
                                        heroVector.waveSpin(
                                            this,
                                            layout,
                                            enemy,
                                            (0.6 * heroVector.attack + 10).toInt()
                                        )
                                        heroVector.mp += 2
                                        mpText.text = "剩余体力：" + "${heroVector.mp}"
                                        thread {
                                            val message = Message()
                                            message.what = deleteWave
                                            Thread.sleep(560)
                                            handler.sendMessage(message)
                                        }
                                    }
                                }
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
                                whirlWindEnabled = false
                            }
                        }
                    }
                }
            }
        }
        if (circleOfDeathEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    heroVector.circleOfDeath(this, enemies, layout)
                    mpText.text = "剩余体力：" + "${heroVector.mp}"
                    circleOfDeathEnabled = false
                    for (grid: Grid in grids) {
                        layout.removeView(findViewById(R.id.id_greenRect))
                    }
                    grids.clear()
                    for (enemyVector: EnemyVector in enemies) {
                        enemyVector.hpDecrease(
                            (heroVector.attack * 0.8 + 20).toInt(),
                            this,
                            layout
                        )
                    }
                    thread {
                        val message = Message()
                        message.what = judgeDeath
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
            R.drawable.huli, CharacterParams(150, 70, 25), Grid(gridView1, 7, 1),
            layout, this, 0
        )
        enemyVector1 = MyImageViewFactory.generateEnemy(
            R.drawable.huli, CharacterParams(170, 65, 28), Grid(gridView1, 7, 2),
            layout, this, 1
        )
        enemyVector2 = MyImageViewFactory.generateEnemy(
            R.drawable.huli, CharacterParams(160, 65, 28), Grid(gridView1, 7, 4),
            layout, this, 1
        )
        enemyVector3 = MyImageViewFactory.generateEnemy(
            R.drawable.huli, CharacterParams(140, 85, 28), Grid(gridView1, 7, 5),
            layout, this, 1
        )
        enemies.add(enemyVector0)
        enemies.add(enemyVector1)
        enemies.add(enemyVector2)
        enemies.add(enemyVector3)
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
                        Grid(gridView1, index1, index2) != enemyVector2.grid &&
                        Grid(gridView1, index1, index2) != enemyVector3.grid &&
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
            R.drawable.child, CharacterParams(180, 60, 35), Grid(gridView1, 4, 3),
            layout, this
        )
        mpText.text = "剩余体力：" + "${heroVector.mp}"
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
        mpText.visibility = View.VISIBLE
        button_cancel.visibility = View.INVISIBLE
    }

    private fun buttonInvisible() {
        button1.visibility = View.INVISIBLE
        button2.visibility = View.INVISIBLE
        button3.visibility = View.INVISIBLE
        button4.visibility = View.INVISIBLE
        button5.visibility = View.INVISIBLE
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
     * index取值0-3
     * turn取值1-3
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
            3 -> {
                when (turn) {
                    1 -> information = enemy3Act1End
                    2 -> information = enemy3Act2End
                    3 -> information = enemy3Act3End
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
            3 -> {
                enemyVector = enemyVector3
                endInformation = enemy3Act3End
            }
        }
        if (!enemyVector.dead) {
            if (enemyVector.grid.radiusContain(2, heroVector.grid)) {
                enemyVector.bite(
                    heroVector,
                    (enemyVector.attack * 0.8).toInt(),
                    kingShieldOn,
                    this,
                    layout
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
                    enemyVector.shot(
                        R.drawable.shoulijian,
                        this,
                        layout,
                        heroVector,
                        heroVector.grid,
                        25,
                        false
                    )
                    if (heroVector.dead) {
                        thread {
                            val message = Message()
                            message.what = deleteHero
                            Thread.sleep(800)
                            handler.sendMessage(message)
                        }
                    }
                    thread {
                        val message = Message()
                        message.what = information
                        Thread.sleep(1400)
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
        val maxIndex = 3
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
                nextEnemyVector = enemyVector3
            }
            3 -> {
                enemyVector = enemyVector3
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