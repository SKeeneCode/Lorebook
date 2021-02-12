package org.ksoftware.lorebook.richtext

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import java.util.function.IntFunction

class BulletFactory(val area: StyledSegmentTextArea) : IntFunction<Node> {

    override fun apply(value: Int): Node {
        val parStyle = area.getParagraph(value).paragraphStyle
        return if (parStyle.isIndented()) createBullet(parStyle.indent.get()) else Label("")
    }

    private fun createBullet(indent: Indent): Node {
        val result: Node = when (indent.level) {
            1 -> {
                val circle = Circle(2.5)
                circle.fill = Color.BLACK
                circle.stroke = Color.BLACK
                circle
            }
            2 -> {
                val circle = Circle(2.5)
                circle.fill = Color.WHITE
                circle.stroke = Color.BLACK
                circle
            }
            3 -> {
                val r = Rectangle(5.0, 5.0)
                r.fill = Color.BLACK
                r.stroke = Color.BLACK
                r
            }
            else -> {
                val r = Rectangle(5.0, 5.0)
                r.fill = Color.WHITE
                r.stroke = Color.BLACK
                r
            }
        }
        val bullet = Label(" ", result)
        bullet.padding = Insets(0.0, 0.0, 0.0, indent.level * indent.width)
        bullet.contentDisplay = ContentDisplay.LEFT
        return bullet
    }
}