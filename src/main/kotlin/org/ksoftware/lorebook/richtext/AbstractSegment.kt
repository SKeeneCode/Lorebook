package org.ksoftware.lorebook.richtext

import javafx.scene.Node
import java.util.*

sealed class AbstractSegment(val data: Any? = null) {

    open fun getText() : String = "\ufffc"

    open fun length() : Int = 1

    open fun charAt(index: Int) : Char = getText()[index]

    open fun subSequence(start: Int, end: Int) : Optional<AbstractSegment> {
        if (start == 0) return Optional.of(this)
        return Optional.empty()
    }

    open fun join(nextSeg: AbstractSegment) : Optional<AbstractSegment> = Optional.empty()

    abstract fun createNode(style: String) : Node

    override fun equals(other: Any?): Boolean {
        if (other === this) return true else if (other is AbstractSegment && javaClass == other.javaClass) {
            return getText() == other.getText()
        }
        return false
    }

    override fun hashCode(): Int {
        return data?.hashCode() ?: 0
    }

}
