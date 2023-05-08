package com.example.lonelywolf.grid

/**
 * 游戏：LonelyWolf
 * 作者：宋宇轩，左天伦，周柏均
 * 版本：1.2.0
 * 版权所有，侵权必究
 * 网格类：约束所有坐标
 */
open class Grid(myGridView: MyGridView, gridX: Int, gridY: Int) {

    var gridX: Int = 0
    var gridY: Int = 0
    var myGridView: MyGridView
    private var x = 0.0f
    private var y = 0.0f
    val row = 5
    val column = 7
    var width: Float
    var height: Float

    fun getX(): Float {
        return this.x
    }

    fun getY(): Float {
        return this.y
    }

    init {
        this.gridX = gridX
        this.gridY = gridY
        this.myGridView = myGridView
        x = myGridView.x + myGridView.width * (gridX - 1) / column
        y = myGridView.y + myGridView.height * (gridY - 1) / row
        width = (myGridView.width / column).toFloat()
        height = (myGridView.height / row).toFloat()
    }

    fun contains(x: Float, y: Float): Boolean {
        if (this.x < x && x < this.x + myGridView.width / column && this.y < y && y < this.y + myGridView.height / row) {
            return true
        }
        return false
    }

    /**
     * @param radius 半径
     * 在半径范围内包含
     */
    fun radiusContain(radius: Int, grid: Grid): Boolean {
        if (gridX - radius <= grid.gridX && grid.gridX <= gridX + radius &&
            gridY - radius <= grid.gridY && grid.gridY <= gridY + radius
        ) {
            return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is Grid) {
            if (this.gridX == other.gridX && this.gridY == other.gridY) {
                return true
            }
        }
        return false
    }
}