package com.example.lonelywolf.characterControl

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.example.lonelywolf.R
import com.example.lonelywolf.base.MyImageView
import com.example.lonelywolf.grid.Grid

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 * 绘制图片和血条的工厂类
 */
class MyImageViewFactory {

    companion object {
        val enemyIds = arrayOf(R.id.id_enemy0, R.id.id_enemy1)

        /**
         * @param resource 图片资源的id
         * @param grid 目标网格
         * @param parentLayout 父控件
         * @param context 环境
         * 生成英雄（无动画）
         */
        fun generateHero(
            resource: Int, characterParams: CharacterParams, grid: Grid,
            parentLayout: ViewGroup, context: Context
        ): HeroVector {
            val heroImage = MyImageView(context)
            heroImage.apply {
                id = R.id.id_hero
                layoutParams = ViewGroup.LayoutParams(grid.width.toInt(), grid.height.toInt())
                setBackgroundResource(resource)
                x = grid.getX()
                y = grid.getY()
            }
            parentLayout.addView(heroImage)
            val hpBar = MyImageView(context)
            hpBar.apply {
                id = R.id.id_hpBar
                layoutParams =
                    ViewGroup.LayoutParams((grid.width * 2 / 3).toInt(), (grid.height / 12).toInt())
                setBackgroundResource(R.drawable.hpbar)
                x = grid.getX() + grid.width / 6
                y = grid.getY() + grid.height / 16
            }
            parentLayout.addView(hpBar)
            val hpValue = MyImageView(context)
            hpValue.apply {
                id = R.id.id_hpValue
                layoutParams =
                    ViewGroup.LayoutParams((grid.width * 2 / 3).toInt(), (grid.height / 12).toInt())
                setBackgroundResource(R.drawable.hpvalue)
                x = grid.getX() + grid.width / 6
                y = grid.getY() + grid.height / 16
            }
            parentLayout.addView(hpValue)
            val hpText = TextView(context)
            hpText.apply {
                width = (grid.width).toInt()
                height = (grid.height).toInt()
                x = grid.getX()
                y = grid.getY()
                text = characterParams.maxHp.toString()
                textSize = grid.height / 16
                gravity = Gravity.CENTER_HORIZONTAL
            }
            parentLayout.addView(hpText)
            return HeroVector(heroImage, hpBar, hpText, hpValue, characterParams, grid)
        }

        /**
         * @param resource 动画资源的id
         * @param grid 目标网格
         * @param parentLayout 父控件
         * @param context 环境
         * 生成英雄（动画）
         */
        fun generateHeroAnim(
            resource: Int,
            characterParams: CharacterParams,
            grid: Grid,
            parentLayout: ViewGroup,
            context: Context
        ): HeroVector {
            val heroImage = MyImageView(context)
            heroImage.apply {
                id = R.id.id_hero
                layoutParams = ViewGroup.LayoutParams(grid.width.toInt(), grid.height.toInt())
                setBackgroundResource(resource)
                x = grid.getX()
                y = grid.getY()
            }
            (heroImage.background as AnimationDrawable).start()
            parentLayout.addView(heroImage)
            val hpBar = MyImageView(context)
            hpBar.apply {
                id = R.id.id_hpBar
                layoutParams =
                    ViewGroup.LayoutParams((grid.width * 2 / 3).toInt(), (grid.height / 12).toInt())
                setBackgroundResource(R.drawable.hpbar)
                x = grid.getX() + grid.width / 6
                y = grid.getY() + grid.height / 16
            }
            parentLayout.addView(hpBar)
            val hpValue = MyImageView(context)
            hpValue.apply {
                id = R.id.id_hpValue
                layoutParams =
                    ViewGroup.LayoutParams((grid.width * 2 / 3).toInt(), (grid.height / 12).toInt())
                setBackgroundResource(R.drawable.hpvalue)
                x = grid.getX() + grid.width / 6
                y = grid.getY() + grid.height / 16
            }
            parentLayout.addView(hpValue)
            val hpText = TextView(context)
            hpText.apply {
                width = (grid.width).toInt()
                height = (grid.height).toInt()
                x = grid.getX()
                y = grid.getY()
                text = characterParams.maxHp.toString()
                textSize = grid.height / 16
                gravity = Gravity.CENTER_HORIZONTAL
            }
            parentLayout.addView(hpText)
            return HeroVector(heroImage, hpBar, hpText, hpValue, characterParams, grid)
        }

        /**
         * @param resource 图片资源的id
         * @param grid 目标网格
         * @param parentLayout 父控件
         * @param context 环境
         * 生成敌人（无动画）
         */
        fun generateEnemy(
            resource: Int,
            characterParams: CharacterParams,
            grid: Grid,
            parentLayout: ViewGroup,
            context: Context,
            index: Int
        ): EnemyVector {
            val enemyImage = MyImageView(context)
            enemyImage.apply {
                id = enemyIds[index]
                layoutParams = ViewGroup.LayoutParams(grid.width.toInt(), grid.height.toInt())
                setBackgroundResource(resource)
                x = grid.getX()
                y = grid.getY()
            }
            parentLayout.addView(enemyImage)
            val hpBar = MyImageView(context)
            hpBar.apply {
                layoutParams =
                    ViewGroup.LayoutParams((grid.width * 2 / 3).toInt(), (grid.height / 12).toInt())
                setBackgroundResource(R.drawable.hpbar)
                x = grid.getX() + grid.width / 6
                y = grid.getY() + grid.height / 16
            }
            parentLayout.addView(hpBar)
            val hpValue = MyImageView(context)
            hpValue.apply {
                layoutParams =
                    ViewGroup.LayoutParams((grid.width * 2 / 3).toInt(), (grid.height / 12).toInt())
                setBackgroundResource(R.drawable.hpvalue)
                x = grid.getX() + grid.width / 6
                y = grid.getY() + grid.height / 16
            }
            parentLayout.addView(hpValue)
            val hpText = TextView(context)
            hpText.apply {
                width = (grid.width).toInt()
                height = (grid.height).toInt()
                x = grid.getX()
                y = grid.getY()
                text = characterParams.maxHp.toString()
                gravity = Gravity.CENTER_HORIZONTAL
            }
            parentLayout.addView(hpText)
            return EnemyVector(enemyImage, hpBar, hpValue, hpText, characterParams, grid, index)
        }
    }

}