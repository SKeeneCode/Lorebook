package org.ksoftware.lorebook.richtext

import javafx.scene.Node
import javafx.scene.control.Label

class IndentSegment(indentStr: Any) : AbstractSegment(indentStr) {
    override fun createNode(style: String): Node {
        val item = Label(data.toString())
        if (style.isNotEmpty()) item.styleClass.add(style)
        return item
    }

    override fun getText(): String {
        return ""
    }
}