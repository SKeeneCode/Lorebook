package org.ksoftware.lorebook.nodes

import javafx.scene.layout.Priority
import javafx.scene.text.Font
import org.fxmisc.flowless.VirtualizedScrollPane
import org.ksoftware.lorebook.richtext.IndentSegment
import org.ksoftware.lorebook.richtext.RichTextViewModal
import org.ksoftware.lorebook.richtext.StyledSegmentTextArea
import tornadofx.paddingAll
import tornadofx.*

class TextNode : TransformableNode() {

    private val textController: TextController by inject(FX.defaultScope)
    private val textViewModal: RichTextViewModal by inject(FX.defaultScope)
    val area = VirtualizedScrollPane(StyledSegmentTextArea())

    init {
        with(area) {
            prefWidthProperty().bind(root.widthProperty().minus(root.paddingAllProperty.multiply(2)))
            prefHeightProperty().bind(root.heightProperty().minus(root.paddingAllProperty.multiply(2)))
        }

        with(area.content) {
            caretPositionProperty().addListener { _, _, _ ->
                val style = getParagraph(currentParagraph).getStyleAtPosition(caretColumn)
                textViewModal.updateViewModalWithStyle(style)
            }
            textViewModal.updateTextTrigger.onChange {
                textController.updateStyleInSelection(this, textViewModal.createTextStyle())
            }
            textViewModal.updateFontTrigger.onChange {
                textController.updateStyleInSelection(this, textViewModal.createFontStyle())
            }
        }
    }

    init {
        with(root) {
            paddingAll = 10.0
            add(area)
        }
    }

}