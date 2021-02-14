package org.ksoftware.lorebook.controls

import javafx.event.Event
import javafx.event.EventType
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Slider
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.stage.Popup
import org.ksoftware.lorebook.richtext.RichTextViewModal
import tornadofx.*
import kotlin.math.roundToInt

class TextSizePicker : View() {

    private val popupController: PopupController by inject(FX.defaultScope)
    private val textViewModal: RichTextViewModal by inject(FX.defaultScope)

    private val popup = Popup()
    private val slider = Slider(8.0, 72.0, 8.0)

    init {
        popup.isAutoFix = true
        popup.isAutoHide = true
        popup.isHideOnEscape = true
        popup.addEventFilter(KeyEvent.KEY_PRESSED) {
            when (it.code) {
                KeyCode.LEFT, KeyCode.KP_LEFT -> {
                    root.positionCaret(root.caretPosition - 1)
                    it.consume()
                }
                KeyCode.RIGHT, KeyCode.KP_RIGHT -> {
                    root.positionCaret(root.caretPosition + 1)
                    it.consume()
                }
            }
        }

        with(slider) {
            style {
                backgroundColor += Color.WHITE
                paddingAll = 4
            }
            majorTickUnit = 16.0
            valueProperty().onChange {
                value = it.roundToInt().toDouble()
                textViewModal.fontSize.value = it.roundToInt().toString()
                if (popup.isShowing) textViewModal.triggerTextChange()
            }

            textViewModal.fontSize.onChange {
                if (it.isNullOrEmpty())  {
                    root.text = ""
                } else {
                    value = it.toDouble()
                }
            }
        }


        val popupContent = vbox {
            alignment = Pos.CENTER
            polygon(0, 0, 10,10,-10,10) {
                fill = Color.WHITE
            }
            add(slider)
        }

        popup.content.add(popupContent)
    }

    override val root = textfield {
        prefHeight = 32.0
        prefWidth = 32.0
        background = null

        style {
            backgroundColor += Color.WHITE
        }

        bind(slider.valueProperty())


        filterInput { change ->
            change.controlNewText.isInt() &&
            change.controlNewText.toInt() in 8..72
        }

        onLeftClick {
                popupController.showPopup(popup, this)
        }


    }
}