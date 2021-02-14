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
                println(style)
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
                    val fontFamilies = styles.filter { it.fontFamily.isPresent }.map { it.fontFamily.get() }
                    val fontSizes = styles.filter { it.fontSize.isPresent }.map { it.fontSize.get() }
                    val bolds = styles.map { it.bold.orElse(false) }
                    val italics = styles.map { it.italic.orElse(false) }


                    if (fontNames.hasDifferentValues()) {
                        textViewModal.fontName.value = Optional.empty()
                    } else {
                        fontNames.firstOrNull()?.let { textViewModal.fontName.value = Optional.of(it) }
                    }

                    if (fontFamilies.hasDifferentValues()) {
                        textViewModal.fontFamily.value = Optional.empty()
                    } else {
                        fontFamilies.firstOrNull()?.let { textViewModal.fontFamily.value = Optional.of(it) }
                    }

                    if (fontSizes.hasDifferentValues()) {
                        textViewModal.fontSize.value = Optional.empty()
                    } else {
                        fontSizes.firstOrNull()?.let { textViewModal.fontSize.value = Optional.of(it)}
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

                println(textViewModal.fontName)
                println(textViewModal.fontFamily)
                textViewModal.triggerLabelChange()
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