package org.ksoftware.lorebook.nodes

import javafx.scene.Parent
import org.fxmisc.flowless.VirtualizedScrollPane
import org.ksoftware.lorebook.MyView
import org.ksoftware.lorebook.richtext.IndentSegment
import org.ksoftware.lorebook.richtext.LabelSegment
import org.ksoftware.lorebook.richtext.StyledSegmentTextArea
import tornadofx.paddingAll
import tornadofx.pane

class TextNode : TransformableNode() {

    init {
        with(root) {
            paddingAll = 10
            val area = StyledSegmentTextArea().apply {
                prefWidth = 300.0
                prefHeight = 200.0

                indent = true
                replaceText(0, 0, "This demo shows how to use a custom object with RichTextFX editor to indent the first line of a paragraph.\n\n");
                appendText("The first line of this second paragraph should be indented. It is using a Label containing a Tab to do this.");
                appendText("\n\nPressing Enter twice to start a new paragraph will result in the first line to be indented.\n\n");
                appendText("This is all achieved in StyledSegmentTextArea which extends GenericStyledArea using AbstractSegments: see IndentSegment and TextSegment.");

            }
            add(area)

        }
    }
}