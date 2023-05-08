package com.example.lonelywolf.grid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 * 画出网格的自定义控件
 */
class MyGridView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private val mPaint = Paint()

    init {

        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 10F

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas, 5, 7)
        invalidate()
    }

    /**
     * 画gridX*gridY的网格
     */
    private fun drawGrid(canvas: Canvas, row: Int, column: Int) {
        for (index in 1 until row) {
            canvas.drawLine(
                x,
                (height / row * index).toFloat(),
                width.toFloat(),
                (height / row * index).toFloat(),
                mPaint
            )
        }
        for (index in 1 until column) {
            canvas.drawLine(
                (width / column * index).toFloat(),
                y,
                (width / column * index).toFloat(),
                height.toFloat(),
                mPaint
            )
        }
    }
}