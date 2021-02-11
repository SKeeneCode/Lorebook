package org.ksoftware.lorebook.richtext

import javafx.scene.text.Font
import java.util.*

class TextStyle(
    var fontFamily: Optional<String> = Optional.empty(),
    var fontName: Optional<String> = Optional.empty(),
    var fontSize: Optional<Double> = Optional.empty(),
    var bold: Optional<Boolean> = Optional.empty(),
    var italic: Optional<Boolean> = Optional.empty()
) {

    companion object {

        val EMPTY = TextStyle()

        fun fontFamily(fontFamily: String): TextStyle = EMPTY.updateFontFamily(fontFamily)
        fun fontName(fontName: String): TextStyle = EMPTY.updateFontName(fontName)
        fun fontSize(fontSize: Double): TextStyle = EMPTY.updateFontSize(fontSize)
        fun bold(bold: Boolean): TextStyle = EMPTY.updateBold(bold)
        fun italic(italic: Boolean): TextStyle = EMPTY.updateItalic(italic)

    }

    fun toCss(): String {
        val sb = StringBuilder()
        if (fontFamily.isPresent) {
            sb.append("-fx-font-family: '${fontFamily.get()}';")
        } else {
           // sb.append("-fx-font-family: ${Font.getDefault().family};")
        }

        if (fontSize.isPresent) {
            sb.append("-fx-font-size: ${fontSize.get()};")
        } else {
          //  sb.append("-fx-font-size: ${Font.getDefault().size};")
        }

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
        if (mixin.fontFamily.isPresent) mixin.fontFamily else fontFamily,
        if (mixin.fontName.isPresent) mixin.fontName else fontName,
        if (mixin.fontSize.isPresent) mixin.fontSize else fontSize,
        if (mixin.bold.isPresent) mixin.bold else bold,
        if (mixin.italic.isPresent) mixin.italic else italic
    )

    fun updateFontFamily(family: String) : TextStyle = TextStyle(Optional.of(family), fontName, fontSize, bold, italic)
    fun updateFontName(name: String) : TextStyle = TextStyle(fontFamily, Optional.of(name), fontSize, bold, italic)
    fun updateFontSize(size: Double) : TextStyle = TextStyle(fontFamily, fontName, Optional.of(size), bold, italic)
    fun updateBold(bold: Boolean): TextStyle = TextStyle(fontFamily, fontName, fontSize, Optional.of(bold), italic)
    fun updateItalic(italic: Boolean): TextStyle = TextStyle(fontFamily, fontName, fontSize, bold, Optional.of(italic))

    override fun toString(): String {
        return "TextStyle(fontFamily=$fontFamily, fontName=$fontName, fontSize=$fontSize, bold=$bold, italic=$italic)"
    }


}