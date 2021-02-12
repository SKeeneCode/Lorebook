package org.ksoftware.lorebook.richtext

class Indent(var level: Int = 0) {

    var width: Double = 15.0

    init {
        if (level < 0) level = 0
    }

    fun increase() : Indent = Indent(level + 1)
    fun decrease() : Indent = Indent(level - 1)

    override fun toString(): String {
        return "Indent(level=$level, width=$width)"
    }


}