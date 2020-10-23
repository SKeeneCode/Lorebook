package org.ksoftware.lorebook.tagflow

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.text.Font
import org.ksoftware.lorebook.tags.TagViewModel
import org.ksoftware.lorebook.utilities.getContrastColor
import tornadofx.*

class TagFlowCell : View() {


    private val tagViewModel: TagViewModel by inject()
    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val editing = SimpleBooleanProperty(false)
    private val item = tagViewModel.item


    override val root = hbox {
        alignment = Pos.CENTER
        style {
            backgroundColor += item.colorProperty.value
            backgroundRadius += box(12.px)
        }
        spacing = 4.0
        paddingHorizontal = 16
        paddingVertical = 8
        prefHeight = 30.0
        onDoubleClick {
            editing.value = true
        }
        button {
            paddingAll = 0
            background = null
            graphic = FontAwesomeIconView(FontAwesomeIcon.MINUS_CIRCLE).apply {
                glyphSize = 18
                fill = getContrastColor(item.colorProperty.value)
            }
            onHover {
                cursor = Cursor.HAND
            }
            removeWhen((this@hbox.hoverProperty().not()).and(editing.not()))
            action {
                tagFlowViewModel.deleteFunction.operate(item)
            }
        }
        textfield(tagViewModel.name) {
            prefWidth = tagViewModel.name.value.length.toDouble() * 8
            removeWhen(editing.not())
            whenVisible {
                requestFocus()
            }
            action {
                editing.value = false
            }
        }
        label(item.nameProperty) {
            removeWhen(editing)
            textFill = getContrastColor(item.colorProperty.value)
            font = Font.font(12.0)
        }
    }
}