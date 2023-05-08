package com.example.lonelywolf.base

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 */
open class MyImageView(context: Context, attrs: AttributeSet?) :
    AppCompatImageView(context, attrs) {

    constructor(context: Context) : this(context, null)

    /**
     * @param view 与哪个ImageView碰撞
     * 如果碰撞，返回true，反之返回false
     */
    fun collideWith(view: ImageView): Boolean {
        if (x > view.x + view.width - 1) {
            return false
        } else if (x + width < view.x + 1) {
            return false
        } else if (y + height < view.y + 1) {
            return false
        } else if (y > view.y + view.height - 1) {
            return false
        }
        return true
    }

    /**
     * @param view 与哪个ImageView碰撞
     * 该函数是collideWith的进阶版，使碰撞的判断条件难度加大
     */
    fun collideWithHardly(view: ImageView): Boolean {
        if (x > view.x + view.width * 3 / 4) {
            return false
        } else if (x + width < view.x + view.width / 4) {
            return false
        } else if (y + height < view.y + view.height / 4) {
            return false
        } else if (y > view.y + view.height * 3 / 4) {
            return false
        }
        return true
    }

    fun setWidth(width: Int) {
        layoutParams.width = width
    }

    fun setHeight(height: Int) {
        layoutParams.height = height
    }
}