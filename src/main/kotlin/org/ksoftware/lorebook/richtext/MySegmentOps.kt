package org.ksoftware.lorebook.richtext

import org.fxmisc.richtext.model.StyledSegment
import org.fxmisc.richtext.model.TextOps
import java.util.*

class MySegmentOps : TextOps<AbstractSegment, TextStyle> {

    private val EMPTY: AbstractSegment = TextSegment("")

    override fun create(text: String): AbstractSegment {
        return if (text.isEmpty()) EMPTY else TextSegment(text)
    }

    override fun createEmptySeg(): AbstractSegment {
        return EMPTY
    }

    override fun charAt(seg: AbstractSegment, index: Int): Char {
        return seg.charAt(index)
    }

    override fun getText(seg: AbstractSegment): String {
        return seg.getText()
    }

    override fun length(seg: AbstractSegment): Int {
        return seg.length()
    }

    override fun subSequence(seg: AbstractSegment, start: Int): AbstractSegment {
        return subSequence(seg, start, seg.length())
    }

    override fun subSequence(seg: AbstractSegment, start: Int, end: Int): AbstractSegment {
        if (start == seg.length() || end == 0) return EMPTY
        val opt: Optional<AbstractSegment> = seg.subSequence(start, end)
        return opt.orElse(EMPTY)
    }

    override fun joinSeg(currentSeg: AbstractSegment, nextSeg: AbstractSegment): Optional<AbstractSegment> {
        return currentSeg.join(nextSeg)
    }
}