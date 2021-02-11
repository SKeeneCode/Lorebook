package org.ksoftware.lorebook.richtext

import javafx.scene.Node
import org.fxmisc.richtext.TextExt
import java.util.*

class TextSegment(val segmentText: String) : AbstractSegment(segmentText) {

    override fun createNode(style: String) : Node {
        val textNode = TextExt(segmentText)
        if (style.isNotEmpty()) textNode.styleProperty().value = style
        return textNode
    }

    override fun charAt(index: Int): Char {
        return segmentText[index]
    }

    override fun getText(): String {
        return segmentText
    }

    override fun length(): Int {
        return segmentText.length
    }

    override fun subSequence(start: Int, end: Int): Optional<AbstractSegment> {
        return if (start == 0 && end == length()) Optional.of(this) else Optional.of(
            TextSegment(segmentText.substring(start, end))
        )
    }

    override fun join(nextSeg: AbstractSegment): Optional<AbstractSegment> {
        return if (nextSeg is TextSegment) {
            Optional.of(TextSegment(segmentText + nextSeg.getText()))
        } else Optional.empty()
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + segmentText.hashCode()
        return result
    }


}