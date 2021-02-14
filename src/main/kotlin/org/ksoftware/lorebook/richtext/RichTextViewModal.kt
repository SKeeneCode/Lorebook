package org.ksoftware.lorebook.richtext

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Label
import javafx.scene.text.Font
import tornadofx.ViewModel
import java.util.*

class RichTextViewModal : ViewModel() {

    val fontFamily = SimpleObjectProperty(Optional.empty<String>())
    val fontName = SimpleObjectProperty(Optional.empty<String>())
    val fontSize = SimpleObjectProperty(Optional.empty<Double>())
    val bold = SimpleObjectProperty(Optional.empty<Boolean>())
    val italic = SimpleObjectProperty(Optional.empty<Boolean>())
    val underline = SimpleStringProperty(null)
    val strikethrough = SimpleStringProperty(null)
    val alignment = SimpleObjectProperty<TextAlignment>(null)
    val indent = SimpleIntegerProperty(0)
    val increaseIndentTrigger = SimpleBooleanProperty(false)
    val decreaseIndentTrigger = SimpleBooleanProperty(false)
    val updateTextTrigger = SimpleBooleanProperty(false)
    val updateLabelTrigger = SimpleBooleanProperty(false)
    val updateParagraphTrigger = SimpleBooleanProperty(false)

    fun updateViewModalWithStyle(style: TextStyle) {
        if (style.fontSize.isPresent) fontSize.value = Optional.of(style.fontSize.get()) else fontSize.value = Optional.empty()
        if (style.fontName.isPresent) fontName.value = Optional.of(style.fontName.get()) else fontName.value = Optional.empty()
        if (style.fontFamily.isPresent) fontFamily.value = Optional.of(style.fontFamily.get()) else fontFamily.value = Optional.empty()
        if (style.bold.isPresent && style.bold.get()) bold.value = Optional.of(true) else bold.value = Optional.empty()
        if (style.italic.isPresent && style.italic.get()) italic.value = Optional.of(true) else italic.value = Optional.empty()
    }

    fun updateViewModalWithParagraphStyle(style: ParStyle) {
        if (style.indent.isPresent) indent.value = style.indent.get().level else indent.value = 0
        if (style.alignment.isPresent) alignment.value = style.alignment.get() else alignment.value = null
    }

    fun updateFontSelectionText(label: Label) {
        var size = Font.getDefault().size
        if (fontSize.value.isPresent) { size = if (fontSize.value.get() > 32.0) 32.0 else fontSize.value.get() }
        if (fontName.value.isPresent) {
            label.font = Font(fontName.value.get(), size)
            println(Font.getFontNames(label.font.family))
            label.text = fontName.value.get()
        } else {
            label.text = ""
        }
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
        if (italic.value.isPresent) style = style.updateItalic(italic.value.get())
        if (fontFamily.value.isPresent) style = style.updateFontFamily(fontFamily.value.get())
        if (fontName.value.isPresent) style = style.updateFontName(fontName.value.get())
        if (fontSize.value.isPresent) style = style.updateFontSize(fontSize.value.get())
        return style
    }

    fun triggerLabelChange() {
        updateLabelTrigger.value = !updateLabelTrigger.value
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

}