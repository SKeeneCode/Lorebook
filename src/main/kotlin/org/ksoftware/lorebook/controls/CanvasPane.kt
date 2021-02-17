package org.ksoftware.lorebook.controls

import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane


class CanvasPane(width: Double = 0.0, height: Double = 0.0) : Pane() {

    val canvas: Canvas = Canvas(width, height)

    override fun layoutChildren() {
        super.layoutChildren()
        val x = snappedLeftInset()
        val y = snappedTopInset()
        val w = snapSizeX(width) - x - snappedRightInset() - 13.0
        val h = snapSizeY(height) - y - snappedBottomInset() - 13.0
        canvas.layoutX = x
        canvas.layoutY = y
        canvas.width = w
        canvas.height = h
    }

    init {
        children.add(canvas)
    }
}