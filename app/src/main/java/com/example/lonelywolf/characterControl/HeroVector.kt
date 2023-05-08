package com.example.lonelywolf.characterControl

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.core.animation.addListener
import com.example.lonelywolf.R
import com.example.lonelywolf.base.MyImageView
import com.example.lonelywolf.grid.Grid
import com.example.lonelywolf.grid.MyGridView
import java.util.*
import kotlin.collections.ArrayList

/**
 * @param heroImage
 * @param hpBar
 * @param hpValue
 * @param grid 与哪个网格绑定
 * 英雄容器类，封装了以上所有变量
 */
class HeroVector(
    var heroImage: MyImageView,
    var hpBar: MyImageView, var hpText: TextView, var hpValue: MyImageView,
    characterParams: CharacterParams, grid: Grid
) {

    var grid: Grid
    var hpValueRelative = 1f
    var dead = false
    var maxHp: Int
    var hp: Int
    var mp = 8
    var attack: Int
    var defence: Int
    var frozenIndex = 0
    var frozen = false

    init {
        this.grid = grid
        maxHp = characterParams.maxHp
        hp = maxHp
        attack = characterParams.attack
        defence = characterParams.defence
    }

    /**
     * 获取整个容器移动的动画
     */
    @SuppressLint("Recycle")
    fun translateSet(grid: Grid, time: Long): AnimatorSet {
        val translateX0 = ObjectAnimator.ofFloat(
            heroImage, "x", heroImage.x,
            grid.getX()
        )
        val translateY0 = ObjectAnimator.ofFloat(
            heroImage, "y", heroImage.y,
            grid.getY()
        )
        val translateX1 = ObjectAnimator.ofFloat(
            hpBar, "x", hpBar.x,
            grid.getX() + grid.width / 6
        )
        val translateY1 = ObjectAnimator.ofFloat(
            hpBar, "y", hpBar.y,
            grid.getY() + grid.height / 16
        )
        val translateX2 = ObjectAnimator.ofFloat(
            hpValue, "x", hpValue.x,
            grid.getX() + grid.width / 6
        )
        val translateY2 = ObjectAnimator.ofFloat(
            hpValue, "y", hpValue.y,
            grid.getY() + grid.height / 16
        )
        val translateX3 = ObjectAnimator.ofFloat(
            hpText, "x", hpText.x,
            grid.getX()
        )
        val translateY3 = ObjectAnimator.ofFloat(
            hpText, "y", hpText.y,
            grid.getY()
        )
        translateY3.duration = time
        val set = AnimatorSet()
        set.playTogether(
            translateX0,
            translateX1,
            translateX2,
            translateY0,
            translateY1,
            translateY2,
            translateX3,
            translateY3
        )
        set.duration = time
        return set
    }

    /**
     * 移动到某个网格
     */
    @SuppressLint("Recycle")
    fun moveTo(grid: Grid) {
        translateSet(grid, 300).start()
        this.grid = grid
        mp -= 1
    }

    /**
     * @param damage 造成的伤害，一个0~1的浮点数，对应百分比生命值
     * 使生命降低damage（百分数）
     */
    @SuppressLint("SetTextI18n")
    fun hpDecrease(damage: Int, context: Context, parentLayout: ViewGroup) {
        //相对系数
        val relativeCoefficient = 100.0 / (100 + defence)
        val damageRelative = (damage * relativeCoefficient).toFloat() / maxHp.toFloat()
        val scaleAnimation = ObjectAnimator.ofFloat(
            hpValue, "scaleX", hpValueRelative,
            hpValueRelative - damageRelative
        )
        val textView = TextView(context)
        textView.apply {
            x = grid.getX()
            y = grid.getY()
            width = grid.width.toInt()
            height = grid.height.toInt()
            gravity = Gravity.CENTER
            textSize = grid.height / 16
            setTextColor(Color.RED)
            text = "-${(damage * relativeCoefficient).toInt()}"
        }
        parentLayout.addView(textView)
        val anim = ObjectAnimator.ofFloat(textView, "y", textView.y, textView.y - grid.height / 4)
        anim.addListener(
            onEnd = {
                parentLayout.removeView(textView)
            }
        )
        anim.duration = 1000
        anim.start()
        if (hp - damage * relativeCoefficient > 0f) {
            hp -= (damage * relativeCoefficient).toInt()
            scaleAnimation.start()
            hpValueRelative -= damageRelative
            hpText.text = hp.toString()
        } else {
            val scaleAnimation1 = ObjectAnimator.ofFloat(
                hpValue, "scaleX", hpValueRelative,
                0f
            )
            scaleAnimation1.start()
            hpValueRelative = 0f
            dead = true
            hp = 0
            hpText.text = hp.toString()
        }
    }

    fun hpReallyDecrease(damage: Int, context: Context, parentLayout: ViewGroup) {
        val damageRelative = damage.toFloat() / maxHp.toFloat()
        val scaleAnimation = ObjectAnimator.ofFloat(
            hpValue, "scaleX", hpValueRelative,
            hpValueRelative - damageRelative
        )
        val textView = TextView(context)
        textView.apply {
            x = grid.getX()
            y = grid.getY()
            width = grid.width.toInt()
            height = grid.height.toInt()
            gravity = Gravity.CENTER
            textSize = grid.height / 16
            setTextColor(Color.RED)
            text = "-${damage}"
        }
        parentLayout.addView(textView)
        val anim = ObjectAnimator.ofFloat(textView, "y", textView.y, textView.y - grid.height / 4)
        anim.addListener(
            onEnd = {
                parentLayout.removeView(textView)
            }
        )
        anim.duration = 1000
        anim.start()
        if (hp - damage > 0f) {
            hp -= damage
            scaleAnimation.start()
            hpValueRelative -= damageRelative
            hpText.text = hp.toString()
        } else {
            val scaleAnimation1 = ObjectAnimator.ofFloat(
                hpValue, "scaleX", hpValueRelative,
                0f
            )
            scaleAnimation1.start()
            hpValueRelative = -1f
            dead = true
            hp = 0
            hpText.text = hp.toString()
        }
    }

    fun hpIncrease(cure: Int, context: Context, parentLayout: ViewGroup) {
        val cureRelative = cure.toFloat() / maxHp.toFloat()
        val scaleAnimation = ObjectAnimator.ofFloat(
            hpValue, "scaleX", hpValueRelative,
            hpValueRelative + cureRelative
        )
        val textView = TextView(context)
        textView.apply {
            x = grid.getX()
            y = grid.getY()
            width = grid.width.toInt()
            height = grid.height.toInt()
            gravity = Gravity.CENTER
            textSize = grid.height / 16
            setTextColor(Color.GREEN)
            text = "+${cure}"
        }
        parentLayout.addView(textView)
        val anim = ObjectAnimator.ofFloat(textView, "y", textView.y, textView.y - grid.height / 4)
        anim.addListener(
            onEnd = {
                parentLayout.removeView(textView)
            }
        )
        anim.duration = 1000
        anim.start()
        if (hp + cure <= maxHp) {
            hp += cure
            scaleAnimation.start()
            hpValueRelative += cureRelative
            hpText.text = hp.toString()
        } else {
            val scaleAnimation1 = ObjectAnimator.ofFloat(
                hpValue, "scaleX", hpValueRelative,
                1f
            )
            scaleAnimation1.start()
            hpValueRelative = 1f
            hp = maxHp
            hpText.text = hp.toString()
        }
    }

    /**
     * @param parentLayout 由哪个父控件移除
     * 使敌人消失
     */
    fun disappear(parentLayout: ViewGroup) {
        val alphaTo0A = ObjectAnimator.ofFloat(hpValue, "alpha", 1.0f, 0.0f)
        alphaTo0A.duration = 300
        val alphaTo0B = ObjectAnimator.ofFloat(hpBar, "alpha", 1.0f, 0.0f)
        alphaTo0B.duration = 300
        val alphaTo0C = ObjectAnimator.ofFloat(heroImage, "alpha", 1.0f, 0.0f)
        alphaTo0C.duration = 300
        val set = AnimatorSet()
        set.play(alphaTo0A).with(alphaTo0B).with(alphaTo0C)
        set.start()
        set.addListener(
            onEnd = {
                parentLayout.removeView(hpValue)
                parentLayout.removeView(hpBar)
                parentLayout.removeView(heroImage)
                parentLayout.removeView(hpText)
                hpValueRelative = 1f
            }
        )
    }

    /**
     *
     * 使heroVector发射一只长矛
     */
    @SuppressLint("ResourceType")
    fun shot(
        context: Context,
        parentLayout: ViewGroup,
        enemyVector: EnemyVector,
        grid: Grid,
        damage: Int
    ) {
        //bullet初始化
        val bullet = MyImageView(context)
        bullet.id = R.id.id_faqiu
        bullet.apply {
            layoutParams =
                ViewGroup.LayoutParams((grid.width / 4).toInt(), (grid.width / 3).toInt())
            setImageResource(R.drawable.faqiu)
            x = x() + width() / 2 - layoutParams.width.toFloat() / 2
            y = y() + height() / 2 - layoutParams.height.toFloat() / 2
        }
        parentLayout.addView(bullet)
        //translate动画，出现动画，消失动画
        val translateX = ObjectAnimator.ofFloat(
            bullet,
            "x",
            bullet.x,
            grid.getX() + grid.width / 2 - grid.width / 8
        )
        translateX.duration = 200
        val translateY = ObjectAnimator.ofFloat(
            bullet,
            "y",
            bullet.y,
            grid.getY() + grid.height / 2 - grid.width / 6
        )
        translateY.duration = 200
        val translate = AnimatorSet()
        translate.play(translateX).with(translateY)
        val alphaTo0 = ObjectAnimator.ofFloat(bullet, "alpha", 1.0f, 0.0f)
        alphaTo0.duration = 200
        val alphaTo1 = ObjectAnimator.ofFloat(bullet, "alpha", 0.0f, 1.0f)
        alphaTo1.duration = 400
        val set = AnimatorSet()
        set.play(alphaTo1).before(translate)
        val set1 = AnimatorSet()
        set1.play(alphaTo0).after(set)
        translate.interpolator = AccelerateInterpolator()
        set1.start()
        mp -= 2

        set1.addListener(
            //放完了就移除
            onEnd = {
                parentLayout.removeView(bullet)
            }
        )


        translate.addListener(
            onEnd = {
                enemyVector.hpDecrease(damage, context, parentLayout)
            }
        )
    }

    fun whirlWindKill(
        context: Context, layout: ViewGroup, enemyVector0: EnemyVector, enemyVector1: EnemyVector,
        damage: Int
    ) {
        val xuanfeng = MyImageView(context)
        xuanfeng.apply {
            id = R.id.id_whirlwind
            layoutParams =
                ViewGroup.LayoutParams(3 * (grid.width).toInt(), 3 * (grid.height).toInt())
            setBackgroundResource(R.drawable.anim_xuanfeng)
            x = grid.getX() - grid.width
            y = grid.getY() - grid.height
        }
        (xuanfeng.background as AnimationDrawable).start()
        layout.addView(xuanfeng)

        if (grid.radiusContain(1, enemyVector0.grid)) {
            enemyVector0.hpDecrease(damage, context, layout)
        }
        if (grid.radiusContain(1, enemyVector1.grid)) {
            enemyVector1.hpDecrease(damage, context, layout)
        }
        mp -= 2
    }

    //极寒突刺，时长300
    fun sprint(
        grid: Grid,
        enemyVectors: ArrayList<EnemyVector>,
        damage: Int,
        context: Context,
        parentLayout: ViewGroup
    ) {
        val backgroundTmp = heroImage.background
        heroImage.setBackgroundResource(R.drawable.chongci)
        val time: Long = 300
        val translateX0 = ObjectAnimator.ofFloat(
            heroImage, "x", heroImage.x,
            grid.getX()
        )
        val translateY0 = ObjectAnimator.ofFloat(
            heroImage, "y", heroImage.y,
            grid.getY()
        )
        val translateX1 = ObjectAnimator.ofFloat(
            hpBar, "x", hpBar.x,
            grid.getX() + grid.width / 6
        )
        val translateY1 = ObjectAnimator.ofFloat(
            hpBar, "y", hpBar.y,
            grid.getY() + grid.height / 16
        )
        val translateX2 = ObjectAnimator.ofFloat(
            hpValue, "x", hpValue.x,
            grid.getX() + grid.width / 6
        )
        val translateY2 = ObjectAnimator.ofFloat(
            hpValue, "y", hpValue.y,
            grid.getY() + grid.height / 16
        )
        val translateX3 = ObjectAnimator.ofFloat(
            hpText, "x", hpText.x,
            grid.getX()
        )
        translateX3.duration = time
        val translateY3 = ObjectAnimator.ofFloat(
            hpText, "y", hpText.y,
            grid.getY()
        )
        translateY3.duration = time
        val set = AnimatorSet()
        set.playTogether(
            translateX0,
            translateX1,
            translateX2,
            translateY0,
            translateY1,
            translateY2,
            translateX3,
            translateY3
        )
        set.duration = time
        set.start()
        this.grid = grid
        mp -= 3
        translateX0.addUpdateListener {
            for (enemyVector: EnemyVector in enemyVectors) {
                if (heroImage.collideWith(enemyVector.enemyImage) && !enemyVector.hurt) {
                    enemyVector.hpDecrease(damage, context, parentLayout)
                    enemyVector.hurt = true
                }
            }
        }
        set.addListener(
            onEnd = {
                for (enemyVector: EnemyVector in enemyVectors) {
                    enemyVector.hurt = false
                }
                heroImage.background = backgroundTmp
            }
        )
    }

    //效果：遁入虚空
    fun goToVoid(time: Long) {
        val translateY0 = ObjectAnimator.ofFloat(
            heroImage, "y", heroImage.y,
            heroImage.y - grid.height / 2
        )
        val translateY1 = ObjectAnimator.ofFloat(
            hpBar, "y", hpBar.y,
            hpBar.y - grid.height / 2
        )
        val translateY2 = ObjectAnimator.ofFloat(
            hpValue, "y", hpValue.y,
            hpValue.y - grid.height / 2
        )
        val translateY3 = ObjectAnimator.ofFloat(
            hpText, "y", hpText.y,
            hpText.y - grid.height / 2
        )
        val alphaTo0A = ObjectAnimator.ofFloat(hpValue, "alpha", 1.0f, 0.0f)
        val alphaTo0B = ObjectAnimator.ofFloat(hpBar, "alpha", 1.0f, 0.0f)
        val alphaTo0C = ObjectAnimator.ofFloat(heroImage, "alpha", 1.0f, 0.0f)
        val alphaTo0D = ObjectAnimator.ofFloat(hpText, "alpha", 1.0f, 0.0f)
        val set = AnimatorSet()
        set.playTogether(
            translateY0,
            translateY1,
            translateY1,
            translateY2,
            translateY3,
            alphaTo0A,
            alphaTo0B,
            alphaTo0C,
            alphaTo0D
        )
        set.duration = time
        set.start()
    }

    @SuppressLint("Recycle")
    fun ghostStrike(
        grid: Grid, myGridView: MyGridView, context: Context, parentLayout: ViewGroup,
        enemyVector: EnemyVector, otherEnemyVectors: ArrayList<EnemyVector>, damage: Int
    ) {
        heroImage.x = grid.getX()
        heroImage.y = grid.getY() - grid.height / 2 * 5
        hpBar.x = grid.getX() + grid.width / 6
        hpBar.y = grid.getY() + grid.height / 16 - grid.height / 2 * 5
        hpValue.x = grid.getX() + grid.width / 6
        hpValue.y = grid.getY() + grid.height / 16 - grid.height / 2 * 5
        hpText.x = grid.getX()
        hpText.y = grid.getY() - grid.height / 2
        val translateY0 = ObjectAnimator.ofFloat(
            heroImage, "y", heroImage.y,
            grid.getY()
        )
        val translateY1 = ObjectAnimator.ofFloat(
            hpBar, "y", hpBar.y,
            grid.getY() + grid.height / 16
        )
        val translateY2 = ObjectAnimator.ofFloat(
            hpValue, "y", hpValue.y,
            grid.getY() + grid.height / 16
        )
        val translateY3 = ObjectAnimator.ofFloat(
            hpText, "y", hpText.y,
            grid.getY()
        )
        val alphaTo1A = ObjectAnimator.ofFloat(hpValue, "alpha", 0.0f, 1.0f)
        val alphaTo1B = ObjectAnimator.ofFloat(hpBar, "alpha", 0.0f, 1.0f)
        val alphaTo1C = ObjectAnimator.ofFloat(heroImage, "alpha", 0.0f, 1.0f)
        val alphaTo1D = ObjectAnimator.ofFloat(hpText, "alpha", 0.0f, 1.0f)
        val set = AnimatorSet()
        set.playTogether(
            translateY0, translateY1, translateY1, translateY2, translateY3,
            alphaTo1A, alphaTo1B, alphaTo1C, alphaTo1D
        )
        set.duration = 300
        set.start()

        val timeTmp = System.currentTimeMillis()
        val effect = MyImageView(context)
        effect.apply {
            layoutParams =
                ViewGroup.LayoutParams((grid.width / 2).toInt(), (grid.height * 3 / 2).toInt())
            x = grid.getX() + grid.width / 4
            y = grid.getY() - grid.height / 2
            id = R.id.id_ghoststrike
            setBackgroundResource(R.drawable.ghoststrike)
            alpha = 0f
        }
        var effectAdded = false
        translateY0.addUpdateListener {
            if (System.currentTimeMillis() - timeTmp > 100 && !effectAdded) {
                parentLayout.addView(effect)
                val alphaTo0 = ObjectAnimator.ofFloat(effect, "alpha", 0f, 1f)
                alphaTo0.duration = 200
                alphaTo0.start()
                effectAdded = true
            }
        }
        set.addListener(
            onEnd = {
                val grids = LinkedList<Grid>()
                for (index1 in grid.gridX - 1..grid.gridX + 1) {
                    for (index2 in grid.gridY - 1..grid.gridY + 1) {
                        for (enemyVector0: EnemyVector in otherEnemyVectors) {
                            if (index1 >= 1 && index2 >= 1 && index1 <= 7 && index2 <= 5 &&
                                Grid(myGridView, index1, index2) != enemyVector0.grid &&
                                Grid(myGridView, index1, index2) != enemyVector.grid
                            ) {
                                grids.add(Grid(myGridView, index1, index2))
                            }
                        }
                    }
                }
                if (grids.contains(Grid(myGridView, grid.gridX - 1, grid.gridY))) {
                    translateSet(Grid(myGridView, grid.gridX - 1, grid.gridY), 300).start()
                    this.grid = Grid(myGridView, grid.gridX - 1, grid.gridY)
                    parentLayout.removeView(effect)
                    attackEffect1(grid, context, parentLayout)
                } else {
                    val random = Random().nextInt(grids.size)
                    translateSet(grids[random], 300).start()
                    this.grid = grids[random]
                    parentLayout.removeView(effect)
                    attackEffect1(grid, context, parentLayout)
                }
            }
        )
        mp -= 2
        enemyVector.hpDecrease(damage, context, parentLayout)
    }

    /**天威：剑，时长600*/
    @SuppressLint("Recycle")
    fun kingSword(
        grid: Grid,
        context: Context,
        parentLayout: ViewGroup,
        enemyVector: EnemyVector,
        damage: Int
    ) {
        val sword = MyImageView(context)
        sword.apply {
            layoutParams = ViewGroup.LayoutParams((grid.width).toInt(), (grid.height).toInt())
            x = grid.getX() + grid.width / 2
            y = grid.getY() - grid.height / 2
            id = R.id.id_kingSword
            setBackgroundResource(R.drawable.kingsword)
            alpha = 0f
        }
        parentLayout.addView(sword)
        val alphaTo1 = ObjectAnimator.ofFloat(sword, "alpha", 0f, 1f)
        alphaTo1.duration = 100
        val alphaTo0 = ObjectAnimator.ofFloat(sword, "alpha", 1f, 0f)
        alphaTo0.duration = 300
        val translateX = ObjectAnimator.ofFloat(sword, "x", sword.x, grid.getX())
        val translateY = ObjectAnimator.ofFloat(sword, "y", sword.y, grid.getY())
        val translate = AnimatorSet()
        translate.duration = 200
        translate.playTogether(translateX, translateY)
        translate.addListener(
            onEnd = {
                attackEffect1(grid, context, parentLayout)
            }
        )
        translate.interpolator = AccelerateInterpolator()
        val set = AnimatorSet()
        set.playSequentially(alphaTo1, translate, alphaTo0)
        set.addListener(
            onEnd = {
                parentLayout.removeView(sword)
                enemyVector.hpDecrease(damage, context, parentLayout)
            }
        )
        set.start()
        mp -= 2
    }

    /**
     * 君王盾：时长800
     */
    @SuppressLint("Recycle")
    fun kingShield(grid: Grid, context: Context, parentLayout: ViewGroup) {
        val shield = MyImageView(context)
        shield.apply {
            layoutParams =
                ViewGroup.LayoutParams((grid.width).toInt(), (grid.height * 7 / 5).toInt())
            x = grid.getX()
            y = grid.getY() - grid.height / 5
            setBackgroundResource(R.drawable.junwangdun0)
            alpha = 0f
        }
        parentLayout.addView(shield)
        val alphaTo1 = ObjectAnimator.ofFloat(shield, "alpha", 0f, 1f)
        alphaTo1.duration = 300
        val alphaTo0 = ObjectAnimator.ofFloat(shield, "alpha", 1f, 0f)
        alphaTo0.duration = 500
        val set = AnimatorSet()
        set.playSequentially(alphaTo1, alphaTo0)
        set.start()
        mp -= 2
    }

    /**
     * 天威：魂，时长800
     */
    fun kingSoul(
        context: Context,
        parentLayout: ViewGroup,
        enemyVector: EnemyVector,
        grid: Grid,
        damage: Int
    ) {
        //bullet初始化
        val bullet = MyImageView(context)
        bullet.id = R.id.id_faqiu
        bullet.apply {
            layoutParams =
                ViewGroup.LayoutParams((grid.width / 4).toInt(), (grid.width / 3).toInt())
            setImageResource(R.drawable.faqiu0)
            x = x() + width() / 2 - layoutParams.width.toFloat() / 2
            y = y() + height() / 2 - layoutParams.height.toFloat() / 2
        }
        parentLayout.addView(bullet)
        //translate动画，出现动画，消失动画
        val translateX = ObjectAnimator.ofFloat(
            bullet,
            "x",
            bullet.x,
            grid.getX() + grid.width / 2 - grid.width / 8
        )
        translateX.duration = 200
        val translateY = ObjectAnimator.ofFloat(
            bullet,
            "y",
            bullet.y,
            grid.getY() + grid.height / 2 - grid.width / 6
        )
        translateY.duration = 200
        val translate = AnimatorSet()
        translate.play(translateX).with(translateY)
        val alphaTo0 = ObjectAnimator.ofFloat(bullet, "alpha", 1.0f, 0.0f)
        alphaTo0.duration = 100
        val alphaTo1 = ObjectAnimator.ofFloat(bullet, "alpha", 0.0f, 1.0f)
        alphaTo1.duration = 300
        val set = AnimatorSet()
        set.play(alphaTo1).before(translate)
        val set1 = AnimatorSet()
        set1.play(alphaTo0).after(set)
        translate.interpolator = AccelerateInterpolator()
        set1.start()
        mp -= 2

        set1.addListener(
            //放完了就移除
            onEnd = {
                parentLayout.removeView(bullet)
            }
        )


        translate.addListener(
            onEnd = {
                enemyVector.hpDecrease(damage, context, parentLayout)
            }
        )
    }

    /**
     * 天威：死灵
     */
    fun kingNecromancy(
        context: Context,
        parentLayout: ViewGroup,
        enemyVector: EnemyVector,
        damage: Int,
        cure: Int
    ) {
        val grid = enemyVector.grid
        val blood = MyImageView(context)
        blood.apply {
            layoutParams =
                ViewGroup.LayoutParams((grid.width).toInt(), (grid.height * 7 / 5).toInt())
            x = grid.getX()
            y = grid.getY() - grid.height / 5
            setBackgroundResource(R.drawable.anim_blood)
            id = R.id.id_blood
            alpha = 0f
        }
        parentLayout.addView(blood)
        val alphaTo1 = ObjectAnimator.ofFloat(blood, "alpha", 0f, 1f)
        alphaTo1.duration = 100
        val anim = (blood.background as AnimationDrawable)
        alphaTo1.start()
        alphaTo1.addListener(
            onEnd = {
                anim.start()
            }
        )
        enemyVector.hpDecrease(damage, context, parentLayout)
        hpIncrease(cure, context, parentLayout)
        mp -= 3
    }

    fun attackEffect1(grid: Grid, context: Context, parentLayout: ViewGroup) {
        val effect1 = MyImageView(context)
        effect1.apply {
            layoutParams = ViewGroup.LayoutParams((grid.width).toInt(), (grid.height).toInt())
            x = grid.getX()
            y = grid.getY()
            id = R.id.id_texiao1
            setBackgroundResource(R.drawable.texiao1)
            scaleX = 0f
            scaleY = 0f
        }
        parentLayout.addView(effect1)
        val scaleXAnim = ObjectAnimator.ofFloat(effect1, "scaleX", 0f, 0.5f, 0f)
        val scaleYAnim = ObjectAnimator.ofFloat(effect1, "scaleY", 0f, 0.5f, 0f)
        val set1 = AnimatorSet()
        set1.playTogether(scaleXAnim, scaleYAnim)
        set1.start()
        set1.addListener(
            onEnd = {
                parentLayout.removeView(effect1)
            }
        )
    }

    //凤舞九天，时长3500
    fun fenghuangFly(
        context: Context,
        enemyVectors: ArrayList<EnemyVector>,
        parentLayout: ViewGroup
    ) {
        mp -= 8
        hpReallyDecrease(40, context, parentLayout)
        val fenghuangImage = MyImageView(context)
        fenghuangImage.apply {
            layoutParams = ViewGroup.LayoutParams((grid.width).toInt(), (grid.height).toInt())
            x = grid.getX()
            y = grid.getY()
            setBackgroundResource(R.drawable.fenghuang)
            scaleX = 0f
            scaleY = 0f
        }
        parentLayout.addView(fenghuangImage)
        val scaleXAnim = ObjectAnimator.ofFloat(fenghuangImage, "scaleX", 0f, 8f)
        val scaleYAnim = ObjectAnimator.ofFloat(fenghuangImage, "scaleY", 0f, 8f)
        val set1 = AnimatorSet()
        val colorImage = MyImageView(context)
        colorImage.apply {
            layoutParams = ViewGroup.LayoutParams((parentLayout.width), (parentLayout.height))
            x = 0f
            y = 0f
            setBackgroundResource(R.drawable.fenghuangicon)
            alpha = 0f
        }
        val colorAnim = ObjectAnimator.ofFloat(colorImage, "alpha", 0f, 1f)
        parentLayout.addView(colorImage)
        set1.playTogether(scaleXAnim, scaleYAnim, colorAnim)
        set1.duration = 1500
        set1.interpolator = AccelerateInterpolator()
        set1.start()
        set1.addListener(
            onEnd = {
                parentLayout.removeView(fenghuangImage)
                parentLayout.removeView(colorImage)
                for (enemyVector: EnemyVector in enemyVectors) {
                    val fire = MyImageView(context)
                    fire.apply {
                        layoutParams = ViewGroup.LayoutParams(
                            (enemyVector.grid.width).toInt(),
                            (enemyVector.grid.height).toInt()
                        )
                        x = enemyVector.grid.getX()
                        y = enemyVector.grid.getY()
                        setBackgroundResource(R.drawable.fire)
                        scaleX = 2f
                        scaleY = 2f
                    }
                    parentLayout.addView(fire)
                    val scaleXAnim1 =
                        ObjectAnimator.ofFloat(fire, "scaleX", 2f, 1.2f, 2f, 1.2f, 2f, 1.2f)
                    val scaleYAnim1 =
                        ObjectAnimator.ofFloat(fire, "scaleY", 2f, 1.2f, 2f, 1.2f, 2f, 1.2f)
                    val set2 = AnimatorSet()
                    set2.playTogether(scaleXAnim1, scaleYAnim1)
                    set2.duration = 2000
                    set2.start()
                    set2.addListener(
                        onEnd = {
                            parentLayout.removeView(fire)
                        }
                    )
                }
            }
        )
    }

    //回血，时长500
    fun rest(context: Context, parentLayout: ViewGroup) {
        mp -= 1
        val fire = MyImageView(context)
        fire.apply {
            layoutParams = ViewGroup.LayoutParams((grid.width).toInt(), (grid.height).toInt())
            x = grid.getX()
            y = grid.getY()
            setBackgroundResource(R.drawable.rest)
            scaleX = 0.8f
            scaleY = 0.8f
        }
        parentLayout.addView(fire)
        bringToFront()
        hpIncrease((0.2 * attack).toInt(), context, parentLayout)
        val scaleXAnim1 = ObjectAnimator.ofFloat(fire, "scaleX", 0.8f, 2.5f)
        val scaleYAnim1 = ObjectAnimator.ofFloat(fire, "scaleY", 0.8f, 2.5f)
        val set2 = AnimatorSet()
        set2.playTogether(scaleXAnim1, scaleYAnim1)
        set2.duration = 500
        set2.start()
        set2.addListener(
            onEnd = {
                parentLayout.removeView(fire)
            }
        )
    }

    //
    fun bite(
        enemyVector: EnemyVector,
        damage: Int,
        context: Context,
        parentLayout: ViewGroup,
        enemies: ArrayList<EnemyVector>,
        myGridView: MyGridView
    ) {
        val destinationGrid = enemyVector.grid
        val time: Long = 150
        val translateX0 = ObjectAnimator.ofFloat(
            heroImage, "x", heroImage.x,
            destinationGrid.getX()
        )
        translateX0.duration = time
        val translateY0 = ObjectAnimator.ofFloat(
            heroImage, "y", heroImage.y,
            destinationGrid.getY()
        )
        translateY0.duration = time
        val translateX1 = ObjectAnimator.ofFloat(
            hpBar, "x", hpBar.x,
            destinationGrid.getX() + destinationGrid.width / 6
        )
        translateX1.duration = time
        val translateY1 = ObjectAnimator.ofFloat(
            hpBar, "y", hpBar.y,
            destinationGrid.getY() + destinationGrid.height / 16
        )
        translateY1.duration = time
        val translateX2 = ObjectAnimator.ofFloat(
            hpValue, "x", hpValue.x,
            destinationGrid.getX() + destinationGrid.width / 6
        )
        translateX2.duration = time
        val translateY2 = ObjectAnimator.ofFloat(
            hpValue, "y", hpValue.y,
            destinationGrid.getY() + destinationGrid.height / 16
        )
        translateY2.duration = time
        val translateX3 = ObjectAnimator.ofFloat(
            hpText, "x", hpText.x,
            destinationGrid.getX()
        )
        translateX3.duration = time
        val translateY3 = ObjectAnimator.ofFloat(
            hpText, "y", hpText.y,
            destinationGrid.getY()
        )
        translateY3.duration = time
        val set = AnimatorSet()
        set.play(translateX0).with(translateY0).with(translateX1).with(translateY1)
            .with(translateX2).with(translateY2).with(translateX3)
            .with(translateY3)
        set.start()
        val grids = LinkedList<Grid>()
        val grid = enemyVector.grid
        for (index1 in grid.gridX - 1..grid.gridX + 1) {
            for (index2 in grid.gridY - 1..grid.gridY + 1) {
                for (enemyVector0: EnemyVector in enemies) {
                    if (index1 >= 1 && index2 >= 1 && index1 <= 7 && index2 <= 5 &&
                        Grid(myGridView, index1, index2) != enemyVector0.grid &&
                        Grid(myGridView, index1, index2) != enemyVector.grid
                    ) {
                        grids.add(Grid(myGridView, index1, index2))
                    }
                }
            }
        }

        translateX0.addUpdateListener {
            if (heroImage.collideWithHardly(enemyVector.enemyImage)) {
                set.pause()
                if (grids.contains(Grid(myGridView, grid.gridX - 1, grid.gridY))) {
                    this.grid = Grid(myGridView, grid.gridX - 1, grid.gridY)
                    attackEffect1(grid, context, parentLayout)
                } else {
                    val random = Random().nextInt(grids.size)
                    this.grid = grids[random]
                    attackEffect1(grid, context, parentLayout)
                }
                translateSet(this.grid, time).start()
            }
        }
        mp -= 2
        enemyVector.hpDecrease(damage, context, parentLayout)
    }

    /**
     * 时长1000
     */
    fun shout(context: Context, parentLayout: ViewGroup) {
        attack += 10
        mp -= 2
        val fire = MyImageView(context)
        fire.apply {
            layoutParams = ViewGroup.LayoutParams((grid.width).toInt(), (grid.height).toInt())
            x = grid.getX()
            y = grid.getY()
            setBackgroundResource(R.drawable.shouteffect)
            scaleX = 0.8f
            scaleY = 0.8f
        }
        parentLayout.addView(fire)
        bringToFront()
        val scaleXAnim1 = ObjectAnimator.ofFloat(fire, "scaleX", 0.8f, 2f)
        val scaleYAnim1 = ObjectAnimator.ofFloat(fire, "scaleY", 0.8f, 2f)
        val alphaTo0 = ObjectAnimator.ofFloat(fire, "alpha", 1f, 0f)
        alphaTo0.duration = 300
        val set2 = AnimatorSet()
        set2.playTogether(scaleXAnim1, scaleYAnim1)
        set2.duration = 700
        set2.start()
        set2.addListener(
            onEnd = {
                alphaTo0.addListener(
                    onEnd = {
                        parentLayout.removeView(fire)
                    }
                )
                alphaTo0.start()
            }
        )
        textEffect(this.grid, context, parentLayout, "attack+10", Color.RED)
    }

    fun think(enemies: ArrayList<EnemyVector>, context: Context, parentLayout: ViewGroup) {
        mp -= 2
        val fire = MyImageView(context)
        fire.apply {
            layoutParams = ViewGroup.LayoutParams((grid.width).toInt(), (grid.height).toInt())
            x = grid.getX()
            y = grid.getY()
            setBackgroundResource(R.drawable.shouteffect)
            scaleX = 0.8f
            scaleY = 0.8f
        }
        val scaleXAnim2 = ObjectAnimator.ofFloat(fire, "scaleX", 0.8f, 2f)
        val scaleYAnim2 = ObjectAnimator.ofFloat(fire, "scaleY", 0.8f, 2f)
        val alphaTo0X = ObjectAnimator.ofFloat(fire, "alpha", 1f, 0f)
        alphaTo0X.duration = 300
        val set3 = AnimatorSet()
        set3.playTogether(scaleXAnim2, scaleYAnim2)
        set3.duration = 700
        set3.start()
        set3.addListener(
            onEnd = {
                alphaTo0X.addListener(
                    onEnd = {
                        parentLayout.removeView(fire)
                    }
                )
                alphaTo0X.start()
            }
        )
        parentLayout.addView(fire)
        bringToFront()
        val scaleXAnim1 = ObjectAnimator.ofFloat(fire, "scaleX", 0.8f, 2f)
        val scaleYAnim1 = ObjectAnimator.ofFloat(fire, "scaleY", 0.8f, 2f)
        val alphaTo0 = ObjectAnimator.ofFloat(fire, "alpha", 1f, 0f)
        alphaTo0.duration = 300
        val set2 = AnimatorSet()
        set2.playTogether(scaleXAnim1, scaleYAnim1)
        set2.duration = 700
        set2.start()
        set2.addListener(
            onEnd = {
                alphaTo0.addListener(
                    onEnd = {
                        parentLayout.removeView(fire)
                    }
                )
                alphaTo0.start()
            }
        )
        var defenceTmp = 0
        for (enemyVector: EnemyVector in enemies) {
            defence += 5
            defenceTmp += 5
        }
        textEffect(this.grid, context, parentLayout, "defence+${defenceTmp}", Color.GREEN)
    }

    /**
     * 雷霆打击
     */
    @SuppressLint("Recycle")
    fun thunderStrike(enemyVector: EnemyVector, context: Context, parentLayout: ViewGroup) {
        val fire = MyImageView(context)
        fire.apply {
            layoutParams = ViewGroup.LayoutParams(
                (enemyVector.grid.width * 2).toInt(),
                (enemyVector.grid.height * 3 / 2).toInt()
            )
            x = enemyVector.grid.getX() - enemyVector.grid.width / 2
            y = enemyVector.grid.getY() - enemyVector.grid.height / 2
            setBackgroundResource(R.drawable.thunder_xuanfeng)
            (background as AnimationDrawable).start()
            scaleX = 0.8f
            scaleY = 0.8f
        }
        parentLayout.addView(fire)
        bringToFront()
        val scaleXAnim = ObjectAnimator.ofFloat(fire, "scaleX", 0.8f, 1.5f, 1.2f, 1.5f, 1.2f)
        val scaleYAnim = ObjectAnimator.ofFloat(fire, "scaleY", 0.8f, 1.5f, 1.2f, 1.5f, 1.2f)
        val scaleAnim = AnimatorSet()
        scaleAnim.duration = 1500
        scaleAnim.playTogether(scaleXAnim, scaleYAnim)
        scaleAnim.start()
        val grid = enemyVector.grid
        mp -= 6
        val translateX0A = ObjectAnimator.ofFloat(
            heroImage, "x", grid.getX() - grid.width / 2,
            grid.getX(), grid.getX() + grid.width / 2
        )
        val translateY0A = ObjectAnimator.ofFloat(
            heroImage, "y", grid.getY() - grid.height,
            grid.getY(), grid.getY() - grid.height
        )
        val translateX1A = ObjectAnimator.ofFloat(
            hpBar, "x", grid.getX() - grid.width / 2 + grid.width / 6,
            grid.getX() + grid.width / 6, grid.getX() + grid.width / 2 + grid.width / 6
        )
        val translateY1A = ObjectAnimator.ofFloat(
            hpBar, "y", grid.getY() - grid.height + grid.height / 16,
            grid.getY() + grid.height / 16, grid.getY() - grid.height + grid.height / 16
        )
        val translateX2A = ObjectAnimator.ofFloat(
            hpValue, "x", grid.getX() - grid.width / 2 + grid.width / 6,
            grid.getX() + grid.width / 6, grid.getX() + grid.width / 2 + grid.width / 6
        )
        val translateY2A = ObjectAnimator.ofFloat(
            hpValue, "y", grid.getY() - grid.height + grid.height / 16,
            grid.getY() + grid.height / 16, grid.getY() - grid.height + grid.height / 16
        )
        val translateX3A = ObjectAnimator.ofFloat(
            hpText, "x", grid.getX() - grid.width / 2,
            grid.getX(), grid.getX() + grid.width / 2
        )
        val translateY3A = ObjectAnimator.ofFloat(
            hpText, "y", grid.getY() - grid.height,
            grid.getY(), grid.getY() - grid.height
        )
        val leftStrike = AnimatorSet()
        leftStrike.playTogether(
            translateX0A, translateX1A, translateX2A, translateY0A, translateY1A,
            translateY2A, translateX3A, translateY3A
        )
        leftStrike.duration = 150
        leftStrike.addListener(
            onStart = {
                heroImage.setBackgroundResource(R.drawable.leftthunder)
            }
        )
        val translateX0B = ObjectAnimator.ofFloat(
            heroImage, "x", grid.getX() + grid.width / 2,
            grid.getX(), grid.getX() - grid.width / 2
        )
        val translateY0B = ObjectAnimator.ofFloat(
            heroImage, "y", grid.getY() - grid.height,
            grid.getY(), grid.getY() - grid.height
        )
        val translateX1B = ObjectAnimator.ofFloat(
            hpBar, "x", grid.getX() + grid.width / 2 + grid.width / 6,
            grid.getX() + grid.width / 6, grid.getX() - grid.width / 2 + grid.width / 6
        )
        val translateY1B = ObjectAnimator.ofFloat(
            hpBar, "y", grid.getY() - grid.height + grid.height / 16,
            grid.getY() + grid.height / 16, grid.getY() - grid.height + grid.height / 16
        )
        val translateX2B = ObjectAnimator.ofFloat(
            hpValue, "x", grid.getX() + grid.width / 2 + grid.width / 6,
            grid.getX() + grid.width / 6, grid.getX() - grid.width / 2 + grid.width / 6
        )
        val translateY2B = ObjectAnimator.ofFloat(
            hpValue, "y", grid.getY() - grid.height + grid.height / 16,
            grid.getY() + grid.height / 16, grid.getY() - grid.height + grid.height / 16
        )
        val translateX3B = ObjectAnimator.ofFloat(
            hpText, "x", grid.getX() + grid.width / 2,
            grid.getX(), grid.getX() - grid.width / 2
        )
        val translateY3B = ObjectAnimator.ofFloat(
            hpText, "y", grid.getY() - grid.height,
            grid.getY(), grid.getY() - grid.height
        )
        val rightStrike = AnimatorSet()
        rightStrike.playTogether(
            translateX0B, translateX1B, translateX2B, translateY0B, translateY1B,
            translateY2B, translateX3B, translateY3B
        )
        rightStrike.duration = 150
        rightStrike.addListener(
            onEnd = {
                enemyVector.hpDecrease((attack * 0.4 + 15).toInt(), context, parentLayout)
            }
        )
        rightStrike.addListener(
            onStart = {
                heroImage.setBackgroundResource(R.drawable.rightthunder)
            }
        )
        val set1 = AnimatorSet()
        set1.playSequentially(leftStrike, rightStrike)
        val set2 = AnimatorSet()
        set2.playSequentially(leftStrike, rightStrike)
        val set3 = AnimatorSet()
        set3.playSequentially(leftStrike, rightStrike)
        val set4 = AnimatorSet()
        set4.playSequentially(leftStrike, rightStrike)
        val set5 = AnimatorSet()
        set5.playSequentially(leftStrike, rightStrike)
        set1.addListener(
            onEnd = {
                enemyVector.hpDecrease((0.4 * attack + 15).toInt(), context, parentLayout)
                set2.start()
            }
        )
        set2.addListener(
            onEnd = {
                enemyVector.hpDecrease((0.4 * attack + 15).toInt(), context, parentLayout)
                set3.start()
            }
        )
        set3.addListener(
            onEnd = {
                enemyVector.hpDecrease((0.4 * attack + 15).toInt(), context, parentLayout)
                set4.start()
            }
        )
        set4.addListener(
            onEnd = {
                enemyVector.hpDecrease((0.4 * attack + 15).toInt(), context, parentLayout)
                set5.start()
            }
        )
        set5.addListener(
            onEnd = {
                enemyVector.hpDecrease((0.4 * attack + 15).toInt(), context, parentLayout)
                parentLayout.removeView(fire)
                heroImage.setBackgroundResource(R.drawable.anim_wolf)
                (heroImage.background as AnimationDrawable).start()
                translateSet(this.grid, 200).start()
            }
        )
        set1.start()
        set1.interpolator = AccelerateInterpolator()
    }

    /**
     * 喝药
     */
    fun energyStrengthen(grid: Grid, context: Context, parentLayout: ViewGroup) {
        val enegy = MyImageView(context)
        enegy.apply {
            layoutParams =
                ViewGroup.LayoutParams((grid.width).toInt(), (grid.height * 7 / 5).toInt())
            x = grid.getX()
            y = grid.getY() - grid.height / 5
            setBackgroundResource(R.drawable.nengliang)
            alpha = 0f
        }
        parentLayout.addView(enegy)
        val alphaTo1 = ObjectAnimator.ofFloat(enegy, "alpha", 0f, 1f)
        alphaTo1.duration = 300
        val alphaTo0 = ObjectAnimator.ofFloat(enegy, "alpha", 1f, 0f)
        alphaTo0.duration = 500
        val set = AnimatorSet()
        set.playSequentially(alphaTo1, alphaTo0)
        set.start()
        mp -= 1
        hpIncrease(20, context, parentLayout)
    }

    /**
     *大力投石
     */
    fun throwStone(
        context: Context,
        parentLayout: ViewGroup,
        enemyVector: EnemyVector,
        grid: Grid,
        damage: Int
    ) {
        val stone = MyImageView(context)
        stone.apply {
            layoutParams =
                ViewGroup.LayoutParams((grid.width * 0.5).toInt(), (grid.width * 0.5).toInt())
            setImageResource(R.drawable.throne)
            x = x() + width() / 2 - layoutParams.width.toFloat() / 2
            y = y() + height() / 2 - layoutParams.height.toFloat() / 2
        }
        parentLayout.addView(stone)
        //translate动画，出现动画，消失动画
        val translateX = ObjectAnimator.ofFloat(
            stone,
            "x",
            stone.x,
            grid.getX() + grid.width / 2 - grid.width / 8
        )
        translateX.duration = 200
        val translateY = ObjectAnimator.ofFloat(
            stone,
            "y",
            stone.y,
            grid.getY() + grid.height / 2 - grid.width / 6
        )
        translateY.duration = 200
        val translate = AnimatorSet()
        translate.play(translateX).with(translateY)
        val alphaTo0 = ObjectAnimator.ofFloat(stone, "alpha", 1.0f, 0.0f)
        alphaTo0.duration = 200
        val alphaTo1 = ObjectAnimator.ofFloat(stone, "alpha", 0.0f, 1.0f)
        alphaTo1.duration = 400
        val set = AnimatorSet()
        set.play(alphaTo1).before(translate)
        val set1 = AnimatorSet()
        set1.play(alphaTo0).after(set)
        translate.interpolator = AccelerateInterpolator()
        set1.start()
        mp -= 2

        set1.addListener(
            //放完了就移除
            onEnd = {
                parentLayout.removeView(stone)
            }
        )

        translate.addListener(
            onEnd = {
                enemyVector.hpDecrease(damage, context, parentLayout)
            }
        )
    }

    /**
     * 水影旋
     */
    fun waveSpin(context: Context, parentLayout: ViewGroup, enemyVector: EnemyVector, damage: Int) {
        val grid = enemyVector.grid
        val wave = MyImageView(context)
        wave.apply {
            layoutParams =
                ViewGroup.LayoutParams((grid.width).toInt(), (grid.height * 7 / 5).toInt())
            x = grid.getX()
            y = grid.getY() - grid.height / 5
            setBackgroundResource(R.drawable.anim_wavespin)
            id = R.id.id_waveSpin
            alpha = 0f
        }
        parentLayout.addView(wave)
        val alphaTo1 = ObjectAnimator.ofFloat(wave, "alpha", 0f, 1f)
        alphaTo1.duration = 200
        val anim = (wave.background as AnimationDrawable)
        alphaTo1.start()
        alphaTo1.addListener(
            onEnd = {
                anim.start()
            }
        )
        enemyVector.hpDecrease(damage, context, parentLayout)
        mp -= 2

    }

    /**
     * 法阵
     */
    fun circleOfDeath(
        context: Context,
        enemyVectors: ArrayList<EnemyVector>,
        parentLayout: ViewGroup
    ) {
        mp -= 4
        hpReallyDecrease(20, context, parentLayout)
        val circleOfDeathImage = MyImageView(context)
        circleOfDeathImage.apply {
            layoutParams = ViewGroup.LayoutParams((grid.width).toInt(), (grid.height).toInt())
            x = grid.getX()
            y = grid.getY()
            setBackgroundResource(R.drawable.fazhen)
            scaleX = 0f
            scaleY = 0f
        }
        parentLayout.addView(circleOfDeathImage)
        val scaleXAnim = ObjectAnimator.ofFloat(circleOfDeathImage, "scaleX", 0f, 5f)
        val scaleYAnim = ObjectAnimator.ofFloat(circleOfDeathImage, "scaleY", 0f, 5f)
        val set1 = AnimatorSet()
        val colorImage = MyImageView(context)
        colorImage.apply {
            layoutParams = ViewGroup.LayoutParams((parentLayout.width), (parentLayout.height))
            x = 0f
            y = 0f
            setBackgroundResource(R.drawable.fazhen2)
            alpha = 0f
        }

        val colorAnim = ObjectAnimator.ofFloat(colorImage, "alpha", 0f, 1f)
        parentLayout.addView(colorImage)
        set1.playTogether(scaleXAnim, scaleYAnim, colorAnim)
        set1.duration = 1500
        set1.interpolator = AccelerateInterpolator()
        set1.start()
        set1.addListener(
            onEnd = {
                parentLayout.removeView(circleOfDeathImage)
                parentLayout.removeView(colorImage)
                for (enemyVector: EnemyVector in enemyVectors) {
                    val fazhen_diren = MyImageView(context)
                    fazhen_diren.apply {
                        layoutParams = ViewGroup.LayoutParams(
                            (enemyVector.grid.width).toInt(),
                            (enemyVector.grid.height).toInt()
                        )
                        x = enemyVector.grid.getX()
                        y = enemyVector.grid.getY()
                        setBackgroundResource(R.drawable.fazhen1)
                        scaleX = 2f
                        scaleY = 2f
                    }
                    parentLayout.addView(fazhen_diren)
                    val scaleXAnim1 =
                        ObjectAnimator.ofFloat(fazhen_diren, "scaleX", 2f, 1.2f, 2f, 1.2f, 2f, 1.2f)
                    val scaleYAnim1 =
                        ObjectAnimator.ofFloat(fazhen_diren, "scaleY", 2f, 1.2f, 2f, 1.2f, 2f, 1.2f)
                    val set2 = AnimatorSet()
                    set2.playTogether(scaleXAnim1, scaleYAnim1)
                    set2.duration = 2000
                    set2.start()
                    set2.addListener(
                        onEnd = {
                            parentLayout.removeView(fazhen_diren)
                        }
                    )
                }
            }
        )

    }

    //以下函数是获取与vector绑定的heroImage的x,y,width,height
    fun x(): Float {
        return heroImage.x
    }

    fun y(): Float {
        return heroImage.y
    }

    fun width(): Float {
        return heroImage.width.toFloat()
    }

    fun height(): Float {
        return heroImage.height.toFloat()
    }

    fun bringToFront() {
        heroImage.bringToFront()
        hpBar.bringToFront()
        hpValue.bringToFront()
        hpText.bringToFront()
    }

    /**
     * 文本效果：攻击/防御+-
     */
    fun textEffect(
        grid: Grid,
        context: Context,
        parentLayout: ViewGroup,
        string: String,
        color: Int
    ) {
        val textView = TextView(context)
        textView.apply {
            x = grid.getX()
            y = grid.getY()
            width = grid.width.toInt()
            height = grid.height.toInt()
            gravity = Gravity.CENTER
            textSize = grid.height / 16
            setTextColor(color)
            text = string
        }
        parentLayout.addView(textView)
        textView.bringToFront()
        val anim = ObjectAnimator.ofFloat(textView, "y", textView.y, textView.y - grid.height / 4)
        anim.addListener(
            onEnd = {
                parentLayout.removeView(textView)
            }
        )
        anim.duration = 1000
        anim.start()
    }
}