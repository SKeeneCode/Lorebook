package org.ksoftware.lorebook.controls

import javafx.scene.Parent
import javafx.scene.control.Slider
import javafx.scene.input.MouseButton
import javafx.stage.Popup
import org.ksoftware.lorebook.richtext.RichTextViewModal
import tornadofx.*
import kotlin.math.roundToInt

class TextSizePicker : View() {

    private val popupController: PopupController by inject(FX.defaultScope)
    private val textViewModal: RichTextViewModal by inject(FX.defaultScope)

    private val popup = Popup()
    private val slider = Slider(8.0, 64.0, 8.0)

    init {
        popup.isAutoFix = true
        popup.isAutoHide = true
        popup.isHideOnEscape = true

        slider.majorTickUnit = 16.0
        slider.valueProperty().onChange {
            slider.value = it.roundToInt().toDouble()
            textViewModal.fontSize.value = it.roundToInt().toString()
            if (popup.isShowing) textViewModal.triggerFontChange()
        }

        textViewModal.fontSize.onChange {
            if (it.isNullOrEmpty())  {
                root.text = ""
            } else {
                slider.value = it.toDouble()
            }
        }

        popup.content.add(slider)
    }

    override val root = textfield {
        prefHeight = 32.0
        prefWidth = 32.0
        background = null

        bind(slider.valueProperty())

        filterInput { change ->
            change.controlNewText.isInt() &&
            change.controlNewText.toInt() in 6..64
        }

        onLeftClick {
                popupController.showPopup(popup, this)
        }


    }
}