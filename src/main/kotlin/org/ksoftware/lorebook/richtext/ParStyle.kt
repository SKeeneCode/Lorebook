package org.ksoftware.lorebook.richtext

import javafx.scene.paint.Color
import org.fxmisc.richtext.model.Codec
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

class ParStyle(
    val alignment : Optional<TextAlignment> = Optional.empty(),
    val indent : Optional<Indent> = Optional.empty()
) {

    companion object {

        val EMPTY = ParStyle()

        val CODEC: Codec<ParStyle> = object : Codec<ParStyle> {

            private val OPT_ALIGNMENT_CODEC : Codec<Optional<TextAlignment>>  =
                    Codec.optionalCodec(Codec.enumCodec(TextAlignment::class.java))
            private val OPT_COLOR_CODEC : Codec<Optional<Color>> =
                Codec.optionalCodec(Codec.COLOR_CODEC)

            override fun getName(): String = "par-style"

            override fun encode(os: DataOutputStream, t: ParStyle) {
                OPT_ALIGNMENT_CODEC.encode(os, t.alignment)
                val indent = if (t.indent.isPresent) t.indent.get().level else 0
                os.writeInt(indent)
            }

            override fun decode(inputStream: DataInputStream): ParStyle =
                ParStyle(
                    OPT_ALIGNMENT_CODEC.decode(inputStream),
                    Optional.of(Indent(inputStream.readInt()))
                )
        }

    }

    fun updateAlignment(alignment: TextAlignment) : ParStyle = ParStyle(Optional.of(alignment), indent)
    fun updateIndent(indent: Indent) : ParStyle = ParStyle(alignment, Optional.of(indent))
    fun increaseIndent(): ParStyle {
        return if (isIndented()) { updateIndent(indent.get().increase()) } else updateIndent(Indent())
    }
    fun decreaseIndent(): ParStyle {
        return if (isIndented()) { updateIndent(indent.get().decrease()) } else this
    }
    fun isIndented() : Boolean = indent.isPresent && indent.get().level > 0

     fun toCss(): String {
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

    fun updateWith(mixin: ParStyle) = ParStyle(
         if (mixin.alignment.isPresent) mixin.alignment else alignment,
         if (mixin.indent.isPresent) mixin.indent else indent
    )

    override fun equals(other: Any?): Boolean {
        return if (other is ParStyle) {
            alignment == other.alignment &&
            indent == other.indent
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = alignment.hashCode()
        result = 31 * result + indent.hashCode()
        return result
    }

    override fun toString(): String {
        return "ParStyle(alignment=$alignment, indent=$indent)"
    }


}