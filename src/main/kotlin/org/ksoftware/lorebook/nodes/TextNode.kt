package org.ksoftware.lorebook.nodes

import org.fxmisc.flowless.VirtualizedScrollPane
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.richtext.*
import org.ksoftware.lorebook.utilities.hasDifferentValues
import tornadofx.paddingAll
import tornadofx.*
import java.util.*
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
                textController.updateParagraphStyleInSelection(this) { textViewModal.createParagraphStyle() }
                requestFocus()
            }
            textViewModal.increaseIndentTrigger.onChange {
                textController.updateParagraphStyleInSelection(this) { textViewModal.createParagraphStyle().increaseIndent() }
                requestFocus()
            }
            textViewModal.decreaseIndentTrigger.onChange {
                textController.updateParagraphStyleInSelection(this) { textViewModal.createParagraphStyle().decreaseIndent() }
                requestFocus()
            }
            textViewModal.updateTextTrigger.onChange {
                val style = textViewModal.createTextStyle()
                textController.updateStyleInSelection(this, style)
                textInsertionStyle = style
                requestFocus()
            }
        }

        with(area.content) {
            onLeftClick {
                    projectViewModal.currentRichText.value = this
//                    val style = getParagraph(currentParagraph).getStyleAtPosition(caretColumn)
//                    val parStyle = getParagraph(currentParagraph).paragraphStyle
//                    if (selection.length == 0) {
//                        textViewModal.updateViewModalWithStyle(style)
//                        textViewModal.updateViewModalWithParagraphStyle(parStyle)
//                    }
           }
            caretPositionProperty().addListener { _, _, _ ->
                val style = getParagraph(currentParagraph).getStyleAtPosition(caretColumn)
                val parStyle = getParagraph(currentParagraph).paragraphStyle
                // If we are just moving the caret and not selecting anything we can immediately update the toolbar
                if (selection.length == 0) {
                    textViewModal.updateViewModalWithStyle(style)
                    textViewModal.updateViewModalWithParagraphStyle(parStyle)
                } else {
                    // otherwise we need to examine the range of styles in our selection to know what buttons to select
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
                        textViewModal.bold.value = Optional.empty()
                    } else {
                        // if any of the bold styles are true they all must be and set the view modal accordingly
                        textViewModal.bold.value = Optional.of(bolds.any { it })
                    }

                    if (italics.hasDifferentValues()) {
                        textViewModal.italic.value = Optional.empty()
                    } else {
                        textViewModal.italic.value = Optional.of(italics.any { it })
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