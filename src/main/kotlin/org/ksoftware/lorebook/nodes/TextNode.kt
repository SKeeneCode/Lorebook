package org.ksoftware.lorebook.nodes

import org.fxmisc.flowless.VirtualizedScrollPane
import org.ksoftware.lorebook.richtext.RichTextViewModal
import org.ksoftware.lorebook.richtext.StyledSegmentTextArea
import tornadofx.paddingAll
import tornadofx.*
import java.util.stream.Collectors

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
                val selection = selection
                // Ensures that rich text controls are only selected when text selection all share a style
                if (selection.length != 0) {
                    val styles = getStyleSpans(selection)
                    if (styles.styleStream().map { it.fontName.get() }.distinct().count() > 1) {
                        textViewModal.fontName.value = null
                    } else {
                        val textStyle = styles.styleStream().findFirst()
                        if (textStyle.isPresent) textViewModal.fontName.value = textStyle.get().fontName.orElse("")
                    }
                    if (styles.styleStream().map { it.fontSize.get() }.distinct().count() > 1) {
                        textViewModal.fontSize.value = null
                    } else {
                        val textStyle = styles.styleStream().findFirst()
                        if (textStyle.isPresent) textViewModal.fontSize.value = textStyle.get().fontSize.orElse(0.0).toString()
                    }
                    if (styles.styleStream().map { it.bold.orElse(false) }.distinct().count() > 1) {
                        textViewModal.bold.value = null
                    } else {
                        textViewModal.bold.value = styles.styleStream().map { it.bold.orElse(false) }.anyMatch { it == true }.toString()
                    }
                    if (styles.styleStream().map { it.italic.orElse(false) }.distinct().count() > 1) {
                        textViewModal.italic.value = null
                    } else {
                        textViewModal.italic.value = styles.styleStream().map { it.italic.orElse(false) }.anyMatch { it == true }.toString()
                    }
                } else {
                    textViewModal.updateViewModalWithStyle(style)
                }
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