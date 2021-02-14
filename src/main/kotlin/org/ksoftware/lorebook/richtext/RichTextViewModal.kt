package org.ksoftware.lorebook.richtext

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ViewModel
import java.util.*

class RichTextViewModal : ViewModel() {

    val fontFamily = SimpleStringProperty(null)
    val fontName = SimpleStringProperty(null)
    val fontSize = SimpleStringProperty(null)
    val bold = SimpleObjectProperty(Optional.empty<Boolean>())
    val italic = SimpleStringProperty(null)
    val underline = SimpleStringProperty(null)
    val strikethrough = SimpleStringProperty(null)
    val alignment = SimpleObjectProperty<TextAlignment>(null)
    val indent = SimpleIntegerProperty(0)
    val showFontSizeText = SimpleBooleanProperty(true)
    val increaseIndentTrigger = SimpleBooleanProperty(false)
    val decreaseIndentTrigger = SimpleBooleanProperty(false)
    val updateTextTrigger = SimpleBooleanProperty(false)
    val updateFontTrigger = SimpleBooleanProperty(false)
    val updateParagraphTrigger = SimpleBooleanProperty(false)

    fun updateViewModalWithStyle(style: TextStyle) {
        if (style.fontSize.isPresent) fontSize.value = style.fontSize.get().toString() else fontSize.value = null
        if (style.fontName.isPresent) fontName.value = style.fontName.get() else fontName.value = null
        if (style.fontFamily.isPresent) fontFamily.value = style.fontFamily.get() else fontFamily.value = null
        if (style.bold.isPresent && style.bold.get()) bold.value = Optional.of(true) else bold.value = Optional.empty()
        if (style.italic.isPresent) italic.value = style.italic.get().toString() else italic.value = null
    }

    fun updateViewModalWithParagraphStyle(style: ParStyle) {
        if (style.indent.isPresent) indent.value = style.indent.get().level else indent.value = 0
        if (style.alignment.isPresent) alignment.value = style.alignment.get() else alignment.value = null
    }

    fun createParagraphStyle() : ParStyle {
        var style = ParStyle.EMPTY
        if (indent.value > 0) style = style.updateIndent(Indent(indent.value))
        if (alignment.value != null) style = style.updateAlignment(alignment.value)
        return style
    }

    fun createTextStyle() : TextStyle {
        var style = TextStyle.EMPTY
        if (bold.value.isPresent) style = style.updateBold(bold.value.get())
        if (!italic.value.isNullOrBlank()) style = style.updateItalic(italic.value.toBoolean())
        return style
    }

    fun createFontStyle() : TextStyle {
        var style = TextStyle.EMPTY
        if (!fontFamily.value.isNullOrBlank()) style = style.updateFontFamily(fontFamily.value)
        if (!fontName.value.isNullOrBlank()) style = style.updateFontName(fontName.value)
        if (!fontSize.value.isNullOrBlank()) style = style.updateFontSize(fontSize.value.toDouble())
        return style
    }

    fun triggerParagraphChange() {
        updateParagraphTrigger.value = !updateParagraphTrigger.value
    }

    fun triggerIndentIncrease() {
        indent.value++
        increaseIndentTrigger.value = !increaseIndentTrigger.value
    }

    fun triggerIndentDecrease() {
        indent.value--
        decreaseIndentTrigger.value = !decreaseIndentTrigger.value
    }

    fun triggerTextChange() {
        updateTextTrigger.value = !updateTextTrigger.value
    }

    fun triggerFontChange() {
        updateFontTrigger.value = !updateFontTrigger.value
    }
}