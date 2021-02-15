package org.ksoftware.lorebook.utilities

import javafx.scene.paint.Color


// https://stackoverflow.com/a/13030061/3317592
fun getContrastColor(color: Color?): Color {
    if (color == null) return Color.DARKGREY
    val y = (299 * color.red + 587 * color.green + 114 * color.blue) / 1000
    return if (y >= 0.5) Color.BLACK else Color.WHITE
}

fun getRandomColor(): Color {
    return Color.color(Math.random(), Math.random(), Math.random())
}