package org.ksoftware.lorebook.nodes

import org.fxmisc.flowless.VirtualizedScrollPane
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.richtext.Indent
import org.ksoftware.lorebook.richtext.ParStyle
import org.ksoftware.lorebook.richtext.RichTextViewModal
import org.ksoftware.lorebook.richtext.StyledSegmentTextArea
import org.ksoftware.lorebook.utilities.allSame
import org.ksoftware.lorebook.utilities.hasDifferentValues
import tornadofx.paddingAll
import tornadofx.*
import java.util.stream.Collectors
import kotlin.streams.toList

class TextNode : TransformableNode() {

    private val textController: TextController by inject(FX.defaultScope)
    private val textViewModal: RichTextViewModal by inject(FX.defaultScope)
    private val projectViewModal: ProjectViewModel by inject()
    val area = VirtualizedScrollPane(StyledSegmentTextArea())

    init {
        with(area) {
            prefWidthProperty().bind(root.widthProperty().minus(root.paddingAllProperty.multiply(2)))
            prefHeightProperty().bind(root.heightProperty().minus(root.paddingAllProperty.multiply(2)))
        }

        with(area.content) {
            textViewModal.updateParagraphTrigger.onChange {
                textController.updateParagraphStyleInSelection(projectViewModal.currentRichText.value) { textViewModal.createParagraphStyle() }
                requestFocus()
            }
            textViewModal.increaseIndentTrigger.onChange {
                textController.updateParagraphStyleInSelection(projectViewModal.currentRichText.value) { textViewModal.createParagraphStyle().increaseIndent() }
                requestFocus()
            }
            textViewModal.decreaseIndentTrigger.onChange {
                textController.updateParagraphStyleInSelection(projectViewModal.currentRichText.value) { textViewModal.createParagraphStyle().decreaseIndent() }
                requestFocus()
            }
            textViewModal.updateTextTrigger.onChange {
                val style = textViewModal.createTextStyle()
                textController.updateStyleInSelection(projectViewModal.currentRichText.value, style)
                requestFocus()
            }
            textViewModal.updateFontTrigger.onChange {
                textController.updateStyleInSelection(projectViewModal.currentRichText.value, textViewModal.createFontStyle())
                requestFocus()
            }
        }

        with(area.content) {
            onLeftClick {
                    projectViewModal.currentRichText.value = this
                    val style = getParagraph(currentParagraph).getStyleAtPosition(caretColumn)
                    val parStyle = getParagraph(currentParagraph).paragraphStyle
                    if (selection.length == 0) {
                        textViewModal.updateViewModalWithStyle(style)
                        textViewModal.updateViewModalWithParagraphStyle(parStyle)
                    }
           }
            caretPositionProperty().addListener { _, _, _ ->
                println("change")
                val style = getParagraph(currentParagraph).getStyleAtPosition(caretColumn)
                val parStyle = getParagraph(currentParagraph).paragraphStyle
                // Ensures that rich text controls are only selected when text selection all share a style
                if (selection.length == 0) {
                    textViewModal.updateViewModalWithStyle(style)
                    textViewModal.updateViewModalWithParagraphStyle(parStyle)
                } else {
                    val styles = getStyleSpans(selection).styleStream().toList()
                    val fontNames = styles.filter { it.fontName.isPresent }.map { it.fontName.get() }
                    val fontSizes = styles.filter { it.fontSize.isPresent }.map { it.fontSize.get() }
                    val bolds = styles.map { it.bold.orElse(false) }
                    val italics = styles.map { it.italic.orElse(false) }


                    if (fontNames.hasDifferentValues()) {
                        textViewModal.fontName.value = null
                    } else {
                        fontNames.firstOrNull()?.let { textViewModal.fontName.value = it }
                    }

                    if (fontSizes.hasDifferentValues()) {
                        if (fontNames.hasDifferentValues()) {
                            textViewModal.showFontSizeText.value = true
                            textViewModal.fontSize.value = null
                        } else {
                            textViewModal.showFontSizeText.value = false
                            textViewModal.fontSize.value = fontSizes.minOrNull().toString()
                        }
                    } else {
                        textViewModal.showFontSizeText.value = true
                        fontSizes.firstOrNull()?.let { textViewModal.fontSize.value = it.toString()}
                    }

                    if (bolds.hasDifferentValues()) {
                        textViewModal.bold.value = null
                    } else {
                        println(bolds.toString())
                        textViewModal.bold.value = bolds.any { it }.toString()
                    }

                    if (italics.hasDifferentValues()) {
                        textViewModal.italic.value = null
                    } else {
                        textViewModal.italic.value = italics.any { it }.toString()
                    }

                }
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