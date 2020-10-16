package org.ksoftware.lorebook.richtext

import org.fxmisc.richtext.model.Codec
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

class ParStyle {

    private val alignment = Optional.empty<TextAlignment>()

    companion object {

    val EMPTY = ParStyle()

    val CODEC: Codec<ParStyle> = object : Codec<ParStyle> {
        override fun getName(): String {
            TODO("Not yet implemented")
        }

        override fun encode(os: DataOutputStream?, t: ParStyle?) {
            TODO("Not yet implemented")
        }

        override fun decode(`is`: DataInputStream?): ParStyle {
            TODO("Not yet implemented")
        }
    }
    }

    fun toCss(): String? {
        val sb = StringBuilder()
        alignment.ifPresent {
            val cssAlignment = when (it) {
                TextAlignment.LEFT -> "left"
                TextAlignment.CENTER -> "center"
                TextAlignment.RIGHT -> "right"
                TextAlignment.JUSTIFY -> "justify"
            }
            sb.append("-fx-text-alignment: $cssAlignment;")
        }
        return sb.toString()
    }
}