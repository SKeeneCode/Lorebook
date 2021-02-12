package org.ksoftware.lorebook.richtext

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.text.Font
import tornadofx.ViewModel

class RichTextViewModal : ViewModel() {

    val fontFamily = SimpleStringProperty(null)
    val fontName = SimpleStringProperty(null)
    val fontSize = SimpleStringProperty(null)
    val bold = SimpleStringProperty(null)
    val italic = SimpleStringProperty(null)
    val underline = SimpleStringProperty(null)
    val strikethrough = SimpleStringProperty(null)
    val alignment = SimpleStringProperty(null)
    val indent = SimpleIntegerProperty(0)
    val showFontSizeText = SimpleBooleanProperty(true)
    val increaseIndentTrigger = SimpleBooleanProperty(false)
    val decreaseIndentTrigger = SimpleBooleanProperty(false)
    val updateTextTrigger = SimpleBooleanProperty(false)
    val updateFontTrigger = SimpleBooleanProperty(false)

    fun updateViewModalWithStyle(style: TextStyle) {
        if (style.fontSize.isPresent) fontSize.value = style.fontSize.get().toString() else fontSize.value = null
        if (style.fontName.isPresent) fontName.value = style.fontName.get() else fontName.value = null
        if (style.fontFamily.isPresent) fontFamily.value = style.fontFamily.get() else fontFamily.value = null
        if (style.bold.isPresent) bold.value = style.bold.get().toString() else bold.value = null
        if (style.italic.isPresent) italic.value = style.italic.get().toString() else italic.value = null
    }

    fun createTextStyle() : TextStyle {
        var style = TextStyle.EMPTY
        if (!bold.value.isNullOrBlank()) style = style.updateBold(bold.value.toBoolean())
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

    fun triggerIndentIncrease() {
        increaseIndentTrigger.value = !increaseIndentTrigger.value
    }

    fun triggerIndentDecrease() {
        decreaseIndentTrigger.value = !decreaseIndentTrigger.value
    }

    fun triggerTextChange() {
        updateTextTrigger.value = !updateTextTrigger.value
    }

    fun triggerFontChange() {
        updateFontTrigger.value = !updateFontTrigger.value
    }
}