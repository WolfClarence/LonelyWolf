package com.example.lonelywolf.characterControl

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.addListener
import com.example.lonelywolf.R
import com.example.lonelywolf.base.MyImageView
import com.example.lonelywolf.grid.Grid
import com.example.lonelywolf.grid.MyGridView
import java.util.*
import kotlin.collections.ArrayList

/**
 * @param enemyImage
 * @param hpBar
 * @param hpValue
 * @param grid 与哪个网格绑定
 * @param index 一个编号，用于id的储存
 * 敌人容器类，封装了以上所有变量
 */
class EnemyVector(
    var enemyImage: MyImageView,
    var hpBar: MyImageView,
    var hpValue: MyImageView,
    var hpText: TextView,
    characterParams: CharacterParams,
    grid: Grid,
    index: Int
) {

    var grid: Grid
    var index: Int
    var hpValueRelative = 1f
    var dead = false
    var maxHp: Int
    var hp: Int
    var mp = 2
    var speed = 20
    var hurt = false
    var attack: Int
    var defence: Int

    init {
        this.grid = grid
        this.index = index
        this.maxHp = characterParams.maxHp
        hp = maxHp
        attack = characterParams.attack
        defence = characterParams.defence
    }

    /**
     * 获取整个容器移动的动画
     */
    private fun translateSet(destinationGrid: Grid, time: Long): AnimatorSet {
        val translateX0 = ObjectAnimator.ofFloat(
            enemyImage, "x", enemyImage.x,
            destinationGrid.getX()
        )
        translateX0.duration = time
        val translateY0 = ObjectAnimator.ofFloat(
            enemyImage, "y", enemyImage.y,
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
        set.duration = 200
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
        scaleAnimation.duration = 100
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
        if (hp - (damage * relativeCoefficient).toInt() > 0) {
            hp -= (damage * relativeCoefficient).toInt()
            scaleAnimation.start()
            hpValueRelative -= damageRelative
            hpText.text = hp.toString()
        } else {
            val scaleAnimation1 = ObjectAnimator.ofFloat(
                hpValue, "scaleX", hpValueRelative,
                0f
            )
            scaleAnimation1.duration = 100
            scaleAnimation1.start()
            hpValueRelative = -1f
            dead = true
            hp = 0
            hpText.text = hp.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    fun hpReallyDecrease(damage: Int, context: Context, parentLayout: ViewGroup) {
        //相对系数
        val damageRelative = damage.toFloat() / maxHp.toFloat()
        val scaleAnimation = ObjectAnimator.ofFloat(
            hpValue, "scaleX", hpValueRelative,
            hpValueRelative - damageRelative
        )
        scaleAnimation.duration = 100
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
        if (hp - damage > 0) {
            hp -= damage
            scaleAnimation.start()
            hpValueRelative -= damageRelative
            hpText.text = hp.toString()
        } else {
            val scaleAnimation1 = ObjectAnimator.ofFloat(
                hpValue, "scaleX", hpValueRelative,
                0f
            )
            scaleAnimation1.duration = 100
            scaleAnimation1.start()
            hpValueRelative = -1f
            dead = true
            hp = 0
            hpText.text = hp.toString()
        }
    }

    /**
     * @param parentLayout 由哪个父控件移除
     * 使敌人消失
     */
    fun disappear(parentLayout: ViewGroup) {
        val alphaTo0A = ObjectAnimator.ofFloat(hpValue, "alpha", 1.0f, 0.0f)
        alphaTo0A.duration = 200
        val alphaTo0B = ObjectAnimator.ofFloat(hpBar, "alpha", 1.0f, 0.0f)
        alphaTo0B.duration = 200
        val alphaTo0C = ObjectAnimator.ofFloat(enemyImage, "alpha", 1.0f, 0.0f)
        alphaTo0C.duration = 200
        val set = AnimatorSet()
        set.play(alphaTo0A).with(alphaTo0B).with(alphaTo0C)
        set.start()
        set.addListener(
            onEnd = {
                parentLayout.removeView(hpValue)
                parentLayout.removeView(hpBar)
                parentLayout.removeView(enemyImage)
                parentLayout.removeView(hpText)
                hpValueRelative = 1f
            }
        )
    }

    /**
     * @param heroVector 攻击的对象
     * @param damage 造成的伤害
     * @param kingShieldOn 射的敌人是否有君王盾
     * enemy的技能：咬
     * 时长600
     */
    fun bite(
        heroVector: HeroVector,
        damage: Int,
        kingShieldOn: Boolean,
        context: Context,
        parentLayout: ViewGroup
    ) {
        val destinationGrid = heroVector.grid
        val time: Long = 150
        val translateX0 = ObjectAnimator.ofFloat(
            enemyImage, "x", enemyImage.x,
            destinationGrid.getX()
        )
        translateX0.duration = time
        val translateY0 = ObjectAnimator.ofFloat(
            enemyImage, "y", enemyImage.y,
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
        translateX0.addUpdateListener {
            if (enemyImage.collideWithHardly(heroVector.heroImage)) {
                set.pause()
                translateSet(this.grid, time).start()
            }
        }
        if (!kingShieldOn) {
            heroVector.hpDecrease(damage, context, parentLayout)
        } else {
            heroVector.hpIncrease((heroVector.attack * 0.1 + 5).toInt(), context, parentLayout)
        }
    }

    /**
     * 向着英雄移动的智能算法
     */
    fun moveToHero(
        heroVector: HeroVector,
        gridView: MyGridView,
        enemyVectors: ArrayList<EnemyVector>
    ): Boolean {

        if (heroVector.grid.gridX < grid.gridX && heroVector.grid.gridY < grid.gridY) {
            val gridTmp = Grid(gridView, grid.gridX - 1, grid.gridY - 1)
            for (enemyVector: EnemyVector in enemyVectors) {
                if (enemyVector.grid == gridTmp) {
                    return false
                }
            }
            if (gridTmp == heroVector.grid) {
                return false
            }
            moveTo(gridTmp)
            this.grid = gridTmp
            return true
        }
        if (heroVector.grid.gridX > grid.gridX && heroVector.grid.gridY < grid.gridY) {
            val gridTmp = Grid(gridView, grid.gridX + 1, grid.gridY - 1)
            for (enemyVector: EnemyVector in enemyVectors) {
                if (enemyVector.grid == gridTmp) {
                    return false
                }
            }
            if (gridTmp == heroVector.grid) {
                return false
            }
            moveTo(gridTmp)
            this.grid = gridTmp
            return true
        }
        if (heroVector.grid.gridX > grid.gridX && heroVector.grid.gridY > grid.gridY) {
            val gridTmp = Grid(gridView, grid.gridX + 1, grid.gridY + 1)
            for (enemyVector: EnemyVector in enemyVectors) {
                if (enemyVector.grid == gridTmp) {
                    return false
                }
            }
            if (gridTmp == heroVector.grid) {
                return false
            }
            moveTo(gridTmp)
            this.grid = gridTmp
            return true
        }
        if (heroVector.grid.gridX < grid.gridX && heroVector.grid.gridY > grid.gridY) {
            val gridTmp = Grid(gridView, grid.gridX - 1, grid.gridY + 1)
            for (enemyVector: EnemyVector in enemyVectors) {
                if (enemyVector.grid == gridTmp) {
                    return false
                }
            }
            if (gridTmp == heroVector.grid) {
                return false
            }
            moveTo(gridTmp)
            this.grid = gridTmp
            return true
        }
        if (heroVector.grid.gridX == grid.gridX && heroVector.grid.gridY < grid.gridY) {
            val gridTmp = Grid(gridView, grid.gridX, grid.gridY - 1)
            for (enemyVector: EnemyVector in enemyVectors) {
                if (enemyVector.grid == gridTmp) {
                    return false
                }
            }
            if (gridTmp == heroVector.grid) {
                return false
            }
            moveTo(gridTmp)
            this.grid = gridTmp
            return true
        }
        if (heroVector.grid.gridX == grid.gridX && heroVector.grid.gridY > grid.gridY) {
            val gridTmp = Grid(gridView, grid.gridX, grid.gridY + 1)
            for (enemyVector: EnemyVector in enemyVectors) {
                if (enemyVector.grid == gridTmp) {
                    return false
                }
            }
            if (gridTmp == heroVector.grid) {
                return false
            }
            moveTo(gridTmp)
            this.grid = gridTmp
            return true
        }
        if (heroVector.grid.gridX > grid.gridX && heroVector.grid.gridY == grid.gridY) {
            val gridTmp = Grid(gridView, grid.gridX + 1, grid.gridY)
            for (enemyVector: EnemyVector in enemyVectors) {
                if (enemyVector.grid == gridTmp) {
                    return false
                }
            }
            if (gridTmp == heroVector.grid) {
                return false
            }
            moveTo(gridTmp)
            this.grid = gridTmp
            return true
        }
        if (heroVector.grid.gridX < grid.gridX && heroVector.grid.gridY == grid.gridY) {
            val gridTmp = Grid(gridView, grid.gridX - 1, grid.gridY)
            for (enemyVector: EnemyVector in enemyVectors) {
                if (enemyVector.grid == gridTmp) {
                    return false
                }
            }
            if (gridTmp == heroVector.grid) {
                return false
            }
            moveTo(gridTmp)
            this.grid = gridTmp
            return true
        }
        return false
    }

    fun shot(
        resource: Int,
        context: Context,
        parentLayout: ViewGroup,
        heroVector: HeroVector,
        grid: Grid,
        damage: Int,
        kingShieldOn: Boolean
    ) {
        //bullet初始化
        val bullet = MyImageView(context)
        bullet.apply {
            layoutParams =
                ViewGroup.LayoutParams((grid.width / 4).toInt(), (grid.width / 3).toInt())
            setImageResource(resource)
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
                if (!kingShieldOn) {
                    heroVector.hpDecrease(damage, context, parentLayout)
                } else {
                    heroVector.hpIncrease((0.2 * heroVector.attack).toInt(), context, parentLayout)
                }
            }
        )
    }

    /**
     * 扔石头，时长1000
     */
    fun throne(
        context: Context, parentLayout: ViewGroup, heroVector: HeroVector, grid: Grid,
        damage: Int, kingShieldOn: Boolean
    ) {
        //bullet初始化
        val bullet = MyImageView(context)
        bullet.apply {
            layoutParams =
                ViewGroup.LayoutParams((grid.width / 2).toInt(), (grid.width * 2 / 3).toInt())
            setImageResource(R.drawable.throne)
            x = x() + width() / 2 - layoutParams.width.toFloat() / 2
            y = y() + height() / 2 - layoutParams.height.toFloat() / 2
        }
        parentLayout.addView(bullet)
        //translate动画，出现动画，消失动画
        val translateX = ObjectAnimator.ofFloat(
            bullet, "x", bullet.x,
            grid.getX() + grid.width / 2 - grid.width / 4
        )
        translateX.duration = 400
        val translateY = ObjectAnimator.ofFloat(
            bullet, "y", bullet.y,
            bullet.y - grid.height, grid.getY() + grid.height / 2 - grid.width / 3
        )
        translateY.duration = 400
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

        set1.addListener(
            //放完了就移除
            onEnd = {
                parentLayout.removeView(bullet)
            }
        )


        translate.addListener(
            onEnd = {
                if (!kingShieldOn) {
                    heroVector.hpDecrease(damage, context, parentLayout)
                } else {
                    heroVector.hpIncrease(
                        (heroVector.attack * 0.1 + 5).toInt(),
                        context,
                        parentLayout
                    )
                }
            }
        )
    }

    /**
     * 扑倒 时长1100
     */
    @SuppressLint("Recycle")
    fun pounce(
        grid: Grid,
        context: Context,
        parentLayout: ViewGroup,
        myGridView: MyGridView,
        heroVector: HeroVector,
        kingShieldOn: Boolean
    ) {
        enemyImage.x = grid.getX()
        enemyImage.y = grid.getY() - grid.height / 2 * 5
        hpBar.x = grid.getX() + grid.width / 6
        hpBar.y = grid.getY() + grid.height / 16 - grid.height / 2 * 5
        hpValue.x = grid.getX() + grid.width / 6
        hpValue.y = grid.getY() + grid.height / 16 - grid.height / 2 * 5
        hpText.x = grid.getX()
        hpText.y = grid.getY() - grid.height / 2
        val translateY0 = ObjectAnimator.ofFloat(
            enemyImage, "y", enemyImage.y,
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
        val alphaTo1C = ObjectAnimator.ofFloat(enemyImage, "alpha", 0.0f, 1.0f)
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
            layoutParams = ViewGroup.LayoutParams((grid.width).toInt(), (grid.height).toInt())
            x = grid.getX()
            y = grid.getY()
            setBackgroundResource(R.drawable.dilie)
            scaleX = 0f
            scaleY = 0f
        }
        var effectAdded = false
        parentLayout.addView(effect)
        heroVector.heroImage.bringToFront()
        heroVector.hpBar.bringToFront()
        heroVector.hpValue.bringToFront()
        heroVector.hpText.bringToFront()
        val alphaTo0 = ObjectAnimator.ofFloat(effect, "alpha", 1f, 0f)
        val scaleXAnim = ObjectAnimator.ofFloat(effect, "scaleX", 0f, 10f)
        val scaleYAnim = ObjectAnimator.ofFloat(effect, "scaleY", 0f, 10f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleXAnim, scaleYAnim)
        animatorSet.duration = 500
        alphaTo0.duration = 500
        alphaTo0.addListener(
            onEnd = {
                parentLayout.removeView(effect)
            }
        )
        animatorSet.addListener(
            onEnd = {
                alphaTo0.start()
            }
        )
        translateY0.addUpdateListener {
            if (System.currentTimeMillis() - timeTmp > 100 && !effectAdded) {
                animatorSet.start()
                effectAdded = true
            }
        }

        set.addListener(
            onEnd = {
                val grids = LinkedList<Grid>()
                for (index1 in grid.gridX - 1..grid.gridX + 1) {
                    for (index2 in grid.gridY - 1..grid.gridY + 1) {
                        if (index1 >= 1 && index2 >= 1 && index1 <= 7 && index2 <= 5 &&
                            Grid(myGridView, index1, index2) != heroVector.grid
                        ) {
                            grids.add(Grid(myGridView, index1, index2))
                        }
                    }
                }
                if (grids.contains(Grid(myGridView, grid.gridX + 1, grid.gridY))) {
                    val translateSet =
                        translateSet(Grid(myGridView, grid.gridX + 1, grid.gridY), 300)
                    translateSet.addListener(
                        onEnd = {
                            bite(heroVector, attack, false, context, parentLayout)
                        }
                    )
                    translateSet.start()
                    this.grid = Grid(myGridView, grid.gridX + 1, grid.gridY)
                    attackEffect1(grid, context, parentLayout)
                } else {
                    val random = Random().nextInt(grids.size)
                    val translateSet = translateSet(grids[random], 300)
                    translateSet.start()
                    this.grid = grids[random]
                    attackEffect1(grid, context, parentLayout)
                }
            }
        )
        if (!kingShieldOn) {
            heroVector.hpDecrease(2 * attack, context, parentLayout)
        } else {
            heroVector.hpIncrease((heroVector.attack * 0.1 + 5).toInt(), context, parentLayout)
        }
    }

    fun jump(time: Long) {
        val translateY0A = ObjectAnimator.ofFloat(
            enemyImage, "y", enemyImage.y,
            enemyImage.y - grid.height
        )
        val translateY1A = ObjectAnimator.ofFloat(
            hpBar, "y", hpBar.y,
            hpBar.y - grid.height
        )
        val translateY2A = ObjectAnimator.ofFloat(
            hpValue, "y", hpValue.y,
            hpValue.y - grid.height
        )
        val translateY3A = ObjectAnimator.ofFloat(
            hpText, "y", hpText.y,
            hpText.y - grid.height
        )
        val alphaTo0A = ObjectAnimator.ofFloat(hpValue, "alpha", 1.0f, 0.0f)
        val alphaTo0B = ObjectAnimator.ofFloat(hpBar, "alpha", 1.0f, 0.0f)
        val alphaTo0C = ObjectAnimator.ofFloat(enemyImage, "alpha", 1.0f, 0.0f)
        val alphaTo0D = ObjectAnimator.ofFloat(hpText, "alpha", 1.0f, 0.0f)
        val setA = AnimatorSet()
        setA.playTogether(
            translateY0A,
            translateY1A,
            translateY1A,
            translateY2A,
            translateY3A,
            alphaTo0A,
            alphaTo0B,
            alphaTo0C,
            alphaTo0D
        )
        setA.duration = time
        setA.start()
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

    fun clownStrike(
        heroVector: HeroVector,
        myGridView: MyGridView,
        context: Context,
        parentLayout: ViewGroup,
        grid: Grid,
        damage: Int,
        kingShieldOn: Boolean
    ) {
        val grid1 = Grid(myGridView, heroVector.grid.gridX + 1, heroVector.grid.gridY)
        val grid2 = Grid(myGridView, heroVector.grid.gridX, heroVector.grid.gridY - 1)
        val grid3 = Grid(myGridView, heroVector.grid.gridX - 1, heroVector.grid.gridY)
        val grid4 = Grid(myGridView, heroVector.grid.gridX, heroVector.grid.gridY + 1)
        val time: Long = 2000
        val translateX0 = ObjectAnimator.ofFloat(
            enemyImage,
            "x",
            enemyImage.x,
            grid1.getX(),
            grid2.getX(),
            grid3.getX(),
            grid4.getX(),
            grid1.getX(),
            grid2.getX(),
            grid3.getX(),
            grid4.getX(),
            grid1.getX(),
            grid2.getX(),
            grid3.getX(),
            grid4.getX(),
            grid1.getX(),
            grid2.getX(),
            grid3.getX(),
            grid4.getX(),
            grid1.getX(),
            enemyImage.x
        )
        translateX0.duration = time
        val translateY0 = ObjectAnimator.ofFloat(
            enemyImage,
            "y",
            enemyImage.y,
            grid1.getY(),
            grid2.getY(),
            grid3.getY(),
            grid4.getY(),
            grid1.getY(),
            grid2.getY(),
            grid3.getY(),
            grid4.getY(),
            grid1.getY(),
            grid2.getY(),
            grid3.getY(),
            grid4.getY(),
            grid1.getY(),
            grid2.getY(),
            grid3.getY(),
            grid4.getY(),
            grid1.getY(),
            enemyImage.y
        )
        translateY0.duration = time
        val translateX1 = ObjectAnimator.ofFloat(
            hpBar,
            "x",
            hpBar.x,
            grid1.getX() + grid1.width / 6,
            grid2.getX() + grid2.width / 6,
            grid3.getX() + grid3.width / 6,
            grid4.getX() + grid4.width / 6,
            grid1.getX() + grid1.width / 6,
            grid2.getX() + grid2.width / 6,
            grid3.getX() + grid3.width / 6,
            grid4.getX() + grid4.width / 6,
            grid1.getX() + grid1.width / 6,
            grid2.getX() + grid2.width / 6,
            grid3.getX() + grid3.width / 6,
            grid4.getX() + grid4.width / 6,
            grid1.getX() + grid1.width / 6,
            grid2.getX() + grid2.width / 6,
            grid3.getX() + grid3.width / 6,
            grid4.getX() + grid4.width / 6,
            grid1.getX() + grid1.width / 6,
            hpBar.x
        )
        translateX1.duration = time
        val translateY1 = ObjectAnimator.ofFloat(
            hpBar,
            "y",
            hpBar.y,
            grid1.getY() + grid1.height / 16,
            grid2.getY() + grid2.height / 16,
            grid3.getY() + grid3.height / 16,
            grid4.getY() + grid4.height / 16,
            grid1.getY() + grid1.height / 16,
            grid2.getY() + grid2.height / 16,
            grid3.getY() + grid3.height / 16,
            grid4.getY() + grid4.height / 16,
            grid1.getY() + grid1.height / 16,
            grid2.getY() + grid2.height / 16,
            grid3.getY() + grid3.height / 16,
            grid4.getY() + grid4.height / 16,
            grid1.getY() + grid1.height / 16,
            grid2.getY() + grid2.height / 16,
            grid3.getY() + grid3.height / 16,
            grid4.getY() + grid4.height / 16,
            grid1.getY() + grid1.height / 16,
            hpBar.y
        )
        translateY1.duration = time
        val translateX2 = ObjectAnimator.ofFloat(
            hpValue,
            "x",
            hpValue.x,
            grid1.getX() + grid1.width / 6,
            grid2.getX() + grid2.width / 6,
            grid3.getX() + grid3.width / 6,
            grid4.getX() + grid4.width / 6,
            grid1.getX() + grid1.width / 6,
            grid2.getX() + grid2.width / 6,
            grid3.getX() + grid3.width / 6,
            grid4.getX() + grid4.width / 6,
            grid1.getX() + grid1.width / 6,
            grid2.getX() + grid2.width / 6,
            grid3.getX() + grid3.width / 6,
            grid4.getX() + grid4.width / 6,
            grid1.getX() + grid1.width / 6,
            grid2.getX() + grid2.width / 6,
            grid3.getX() + grid3.width / 6,
            grid4.getX() + grid4.width / 6,
            grid1.getX() + grid1.width / 6,
            hpValue.x
        )
        translateX2.duration = time
        val translateY2 = ObjectAnimator.ofFloat(
            hpValue,
            "y",
            hpValue.y,
            grid1.getY() + grid1.height / 16,
            grid2.getY() + grid2.height / 16,
            grid3.getY() + grid3.height / 16,
            grid4.getY() + grid4.height / 16,
            grid1.getY() + grid1.height / 16,
            grid2.getY() + grid2.height / 16,
            grid3.getY() + grid3.height / 16,
            grid4.getY() + grid4.height / 16,
            grid1.getY() + grid1.height / 16,
            grid2.getY() + grid2.height / 16,
            grid3.getY() + grid3.height / 16,
            grid4.getY() + grid4.height / 16,
            grid1.getY() + grid1.height / 16,
            grid2.getY() + grid2.height / 16,
            grid3.getY() + grid3.height / 16,
            grid4.getY() + grid4.height / 16,
            grid1.getY() + grid1.height / 16,
            hpValue.y
        )
        translateY2.duration = time
        val translateX3 = ObjectAnimator.ofFloat(
            hpText,
            "x",
            hpText.x,
            grid1.getX(),
            grid2.getX(),
            grid3.getX(),
            grid4.getX(),
            grid1.getX(),
            grid2.getX(),
            grid3.getX(),
            grid4.getX(),
            grid1.getX(),
            grid2.getX(),
            grid3.getX(),
            grid4.getX(),
            grid1.getX(),
            grid2.getX(),
            grid3.getX(),
            grid4.getX(),
            grid1.getX(),
            hpText.x,
        )
        translateX3.duration = time
        val translateY3 = ObjectAnimator.ofFloat(
            hpText,
            "y",
            hpText.y,
            grid1.getY(),
            grid2.getY(),
            grid3.getY(),
            grid4.getY(),
            grid1.getY(),
            grid2.getY(),
            grid3.getY(),
            grid4.getY(),
            grid1.getY(),
            grid2.getY(),
            grid3.getY(),
            grid4.getY(),
            grid1.getY(),
            grid2.getY(),
            grid3.getY(),
            grid4.getY(),
            grid1.getY(),
            hpText.y
        )
        translateY3.duration = time
        val set = AnimatorSet()
        set.play(translateX0).with(translateY0).with(translateX1).with(translateY1)
            .with(translateX2).with(translateY2).with(translateX3)
            .with(translateY3)
        val time1 = System.currentTimeMillis()
        val shots = arrayOf(false, false, false, false)
        translateX0.addUpdateListener {
            if (System.currentTimeMillis() - time1 > 400 && !shots[0]) {
                if (kingShieldOn) {
                    shot(R.drawable.faqiu, context, parentLayout, heroVector, grid, damage, true)
                } else {
                    shot(R.drawable.faqiu, context, parentLayout, heroVector, grid, damage, false)
                }
                shots[0] = true
            }
            if (System.currentTimeMillis() - time1 > 800 && !shots[1]) {
                shot(R.drawable.faqiu, context, parentLayout, heroVector, grid, damage, false)
                shots[1] = true
            }
            if (System.currentTimeMillis() - time1 > 1200 && !shots[2]) {
                shot(R.drawable.faqiu, context, parentLayout, heroVector, grid, damage, false)
                shots[2] = true
            }
            if (System.currentTimeMillis() - time1 > 1600 && !shots[3]) {
                shot(R.drawable.faqiu, context, parentLayout, heroVector, grid, damage, false)
                shots[3] = true
            }
        }
        set.interpolator = AccelerateInterpolator()
        set.start()
    }

    fun sacrifice(
        enemyVector: EnemyVector,
        grid: Grid,
        attack: Int,
        damage: Int,
        context: Context,
        parentLayout: ViewGroup
    ) {
        //bullet初始化
        val bullet = MyImageView(context)
        bullet.apply {
            layoutParams =
                ViewGroup.LayoutParams((grid.width / 4).toInt(), (grid.width / 3).toInt())
            setImageResource(R.drawable.sacrifice)
            x = x() + width() / 2 - layoutParams.width.toFloat() / 2
            y = y() + height() / 2 - layoutParams.height.toFloat() / 2
        }
        parentLayout.addView(bullet)
        //translate动画，出现动画，消失动画
        val translateX = ObjectAnimator.ofFloat(
            bullet,
            "x",
            bullet.x,
            grid.getX() + grid.width / 2 - enemyVector.grid.width / 8
        )
        translateX.duration = 200
        val translateY = ObjectAnimator.ofFloat(
            bullet,
            "y",
            bullet.y,
            grid.getY() + grid.height / 2 - enemyVector.grid.width / 6
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
                hpReallyDecrease(damage, context, parentLayout)
                enemyVector.attack += attack
                val textView = TextView(context)
                textView.apply {
                    x = grid.getX()
                    y = grid.getY()
                    width = grid.width.toInt()
                    height = grid.height.toInt()
                    gravity = Gravity.CENTER
                    textSize = grid.height / 16
                    setTextColor(Color.RED)
                    text = "attack+${attack}"
                }
                parentLayout.addView(textView)
                val anim =
                    ObjectAnimator.ofFloat(textView, "y", textView.y, textView.y - grid.height / 4)
                anim.addListener(
                    onEnd = {
                        parentLayout.removeView(textView)
                    }
                )
                anim.duration = 1000
                anim.start()
            }
        )

    }

    //暴风雪：雪龙的技能，伤害为attack
    fun snowStorm(
        heroVector: HeroVector,
        context: Context,
        parentLayout: ViewGroup,
        kingShieldOn: Boolean,
        damage: Int
    ) {
        val colorImage = MyImageView(context)
        colorImage.apply {
            layoutParams = ViewGroup.LayoutParams((parentLayout.width), (parentLayout.height))
            x = 0f
            y = 0f
            setBackgroundResource(R.drawable.storm)
            alpha = 0f
        }
        parentLayout.addView(colorImage)
        val colorAnim = ObjectAnimator.ofFloat(colorImage, "alpha", 0f, 1f)
        colorAnim.duration = 2000
        colorAnim.addListener(
            onEnd = {
                parentLayout.removeView(colorImage)
                if (kingShieldOn) {
                    heroVector.hpDecrease((damage * 0.6).toInt(), context, parentLayout)
                } else {
                    heroVector.hpDecrease(damage, context, parentLayout)
                }
                heroVector.frozenIndex += 1
                if (heroVector.frozenIndex == 3) {
                    heroVector.frozenIndex = 0
                    heroVector.frozen = true
                    val ice = MyImageView(context)
                    ice.apply {
                        layoutParams = ViewGroup.LayoutParams(
                            (heroVector.grid.width * 3).toInt(),
                            (heroVector.grid.height * 3).toInt()
                        )
                        x = heroVector.grid.getX() - heroVector.grid.width
                        y = heroVector.grid.getY() - heroVector.grid.height * 2
                        setBackgroundResource(R.drawable.ice)
                        id = R.id.id_ice
                        alpha = 0.5f
                    }
                    parentLayout.addView(ice)
                }
            }
        )
        colorAnim.start()
    }

    //龙之吐息
    fun dragonMagic(
        heroVector: HeroVector,
        context: Context,
        parentLayout: ViewGroup,
        myGridView: MyGridView
    ) {
        val magic = MyImageView(context)
        magic.apply {
            layoutParams = ViewGroup.LayoutParams(
                (heroVector.grid.width).toInt(),
                (heroVector.grid.height).toInt()
            )
            x = Grid(myGridView, 7, 3).getX()
            y = Grid(myGridView, 7, 3).getY()
            setBackgroundResource(R.drawable.snowlight)
            scaleX = 0f
            scaleY = 0f
        }
        parentLayout.addView(magic)
        val scaleXAnim = ObjectAnimator.ofFloat(magic, "scaleX", 0f, 1f)
        val scaleYAnim = ObjectAnimator.ofFloat(magic, "scaleY", 0f, 1f, 5f)
        val set1 = AnimatorSet()
        set1.playTogether(scaleXAnim, scaleYAnim)
        set1.duration = 1000
        set1.start()
        set1.addListener(
            onEnd = {
                val translateAnim = ObjectAnimator.ofFloat(magic, "x", magic.x, 0f)
                translateAnim.duration = 1000
                translateAnim.interpolator = AccelerateInterpolator()
                translateAnim.addListener(
                    onEnd = {
                        heroVector.hpDecrease(2 * attack, context, parentLayout)
                        val alphaTo0 = ObjectAnimator.ofFloat(magic, "alpha", 1f, 0f)
                        alphaTo0.duration = 500
                        alphaTo0.addListener(
                            onEnd = {
                                parentLayout.removeView(magic)
                            }
                        )
                        alphaTo0.start()
                    }
                )
                translateAnim.start()
            }
        )
    }

    //以下函数是获取与vector绑定的enemyImage的x,y,width,height
    fun x(): Float {
        return enemyImage.x
    }

    fun y(): Float {
        return enemyImage.y
    }

    fun width(): Int {
        return enemyImage.width
    }

    fun height(): Int {
        return enemyImage.height
    }
}