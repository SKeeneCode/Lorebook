package org.ksoftware.lorebook.richtext

import javafx.scene.Node
import javafx.scene.control.Label

class LabelSegment(segmentData: Any) : AbstractSegment(segmentData) {

   override fun createNode(style: String): Node {
        val item = Label(data.toString())
       item.style = "-fx-border-width: 1; -fx-border-style: solid; -fx-border-color: lightgrey; -fx-padding: 0 2 0 2; -fx-font-weight: normal; -fx-font-size: 10px;"
        if (style.isNotEmpty()) item.styleClass.add(style)
       item.userData = data
        return item
    }
}