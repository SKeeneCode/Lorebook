package org.ksoftware.lorebook.richtext

import org.fxmisc.richtext.model.Codec
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

class TextStyle(
        var bold: Optional<Boolean> = Optional.empty(),
        var italic: Optional<Boolean> = Optional.empty()
) {

    companion object {

        val EMPTY = TextStyle()

        fun bold(bold: Boolean): TextStyle = EMPTY.updateBold(bold)
        fun italic(italic: Boolean): TextStyle = EMPTY.updateItalic(italic)


        val CODEC: Codec<TextStyle> = object : Codec<TextStyle> {
            override fun getName(): String = "text-style"

            override fun encode(os: DataOutputStream?, t: TextStyle?) {
                TODO("Not yet implemented")
            }

            override fun decode(`is`: DataInputStream?): TextStyle {
                TODO("Not yet implemented")
            }
        }
    }

    fun toCss(): String? {
        val sb = StringBuilder()
        if (bold.isPresent) {
            if (bold.get()) {
                sb.append("-fx-font-weight: bold;")
            } else {
                sb.append("-fx-font-weight: normal;")
            }
        }

        if (italic.isPresent) {
            if (italic.get()) {
                sb.append("-fx-font-style: italic;")
            } else {
                sb.append("-fx-font-style: normal;")
            }
        }
        return sb.toString()
    }

    fun updateWith(mixin: TextStyle): TextStyle = TextStyle(
            if (mixin.bold.isPresent) mixin.bold else bold,
            if (mixin.italic.isPresent) mixin.italic else italic
    )

    fun updateBold(bold: Boolean): TextStyle = TextStyle(Optional.of(bold), italic)
    fun updateItalic(italic: Boolean): TextStyle = TextStyle(bold, Optional.of(italic))

}