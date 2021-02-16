package org.ksoftware.lorebook.controls

import javafx.geometry.Pos
import javafx.scene.control.Slider
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.stage.Popup
import org.ksoftware.lorebook.richtext.ToolbarViewModal
import tornadofx.*
import java.util.*
import kotlin.math.roundToInt

/**
 * The TextSizePicker allows the user to control font size of the rich text implementation.
 */
class TextSizePicker : View() {

    private val popupController: PopupController by inject(FX.defaultScope)
    private val toolbarViewModal: ToolbarViewModal by inject()

    private val popup = Popup()
    private val slider = Slider(8.0, 72.0, 8.0)

    init {
        println(toolbarViewModal)
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
                toolbarViewModal.fontSize.value = Optional.of(it.roundToInt().toDouble())
                if (popup.isShowing) {
                    toolbarViewModal.triggerTextChange()
                }
            }
            toolbarViewModal.fontSize.onChange {
                if (it != null) {
                    if (it.isPresent) {
                        value = it.get()
                        root.style {
                            backgroundColor += Color.WHITE
                            textFill = Color.BLACK
                        }
                        toolbarViewModal.triggerLabelChange()
                    } else {
                        root.style {
                            backgroundColor += Color.WHITE
                            textFill = Color.WHITE
                        }
                    }
                }
            }
        }
        val popupContent = vbox {
            alignment = Pos.CENTER
            polygon(0, 0, 10, 10, -10, 10) {
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
                    change.controlNewText.toInt() in 1..72
        }

        onLeftClick {
            popupController.showPopup(popup, this)
        }
    }
}