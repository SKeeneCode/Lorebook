package org.ksoftware.lorebook.nodes

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import org.fxmisc.flowless.VirtualizedScrollPane
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.richtext.*
import org.ksoftware.lorebook.utilities.hasDifferentValues
import tornadofx.*
import java.util.*
import kotlin.streams.toList

class TextNode : TransformableNode() {

    private val textController: TextController by inject(FX.defaultScope)
    private val toolbarViewModal: ToolbarViewModal by inject()
    private val projectViewModal: ProjectViewModel by inject()
    val area = VirtualizedScrollPane(StyledSegmentTextArea())

    init {
        with(area) {
            addEventFilter(KeyEvent.KEY_PRESSED) {
                if (it.code == KeyCode.B && it.isControlDown) {
                    toolbarViewModal.toggleBold()
                    toolbarViewModal.triggerTextChange()
                }
                if (it.code == KeyCode.I && it.isControlDown) {
                    toolbarViewModal.toggleItalic()
                    toolbarViewModal.triggerTextChange()
                }
            }

            prefWidthProperty().bind(root.widthProperty().minus(root.paddingAllProperty.multiply(2)))
            prefHeightProperty().bind(root.heightProperty().minus(root.paddingAllProperty.multiply(2)))
        }

        with(area.content) {
            toolbarViewModal.updateParagraphTrigger.onChange {
                if (this != projectViewModal.currentRichText.value) return@onChange
                textController.updateParagraphStyleInSelection(this) { parStyle -> parStyle.updateWith(toolbarViewModal.createParagraphStyle()) }
                requestFocus()
            }
            toolbarViewModal.increaseIndentTrigger.onChange {
                if (this != projectViewModal.currentRichText.value) return@onChange
                textController.updateParagraphStyleInSelection(this) { parStyle -> parStyle.increaseIndent() }
                requestFocus()
            }
            toolbarViewModal.decreaseIndentTrigger.onChange {
                if (this != projectViewModal.currentRichText.value) return@onChange
                textController.updateParagraphStyleInSelection(this) { parStyle -> parStyle.decreaseIndent() }
                requestFocus()
            }
            toolbarViewModal.updateTextTrigger.onChange {
                if (this != projectViewModal.currentRichText.value) return@onChange
                val style = toolbarViewModal.createTextStyle()
                textController.updateStyleInSelection(this, style)
                textInsertionStyle = style
                requestFocus()
            }
        }

        with(area.content) {
            onLeftClick {
                toolbarViewModal.triggerLabelChange()
                    val style = getParagraph(currentParagraph).getStyleAtPosition(caretColumn)
                    val parStyle = getParagraph(currentParagraph).paragraphStyle
                    if (selection.length == 0) {
                        toolbarViewModal.updateViewModalWithStyle(style)
                        toolbarViewModal.updateViewModalWithParagraphStyle(parStyle)
                        toolbarViewModal.triggerLabelChange()
                    }
           }
            focusedProperty().onChange {
                if (it) projectViewModal.currentRichText.value = this
            }
            caretPositionProperty().addListener { _, _, _ ->
                val style = getParagraph(currentParagraph).getStyleAtPosition(caretColumn)
                textInsertionStyle = style
                val parStyle = getParagraph(currentParagraph).paragraphStyle
                // If we are just moving the caret and not selecting anything we can immediately update the toolbar
                if (selection.length == 0) {
                    toolbarViewModal.updateViewModalWithStyle(style)
                    toolbarViewModal.updateViewModalWithParagraphStyle(parStyle)
                } else {
                    // otherwise we need to examine the range of styles in our selection to know what buttons to select
                    val styles = getStyleSpans(selection).styleStream().toList()
                    val fontNames = styles.filter { it.fontName.isPresent }.map { it.fontName.get() }
                    val fontFamilies = styles.filter { it.fontFamily.isPresent }.map { it.fontFamily.get() }
                    val fontSizes = styles.filter { it.fontSize.isPresent }.map { it.fontSize.get() }
                    val bolds = styles.map { it.bold.orElse(false) }
                    val italics = styles.map { it.italic.orElse(false) }

                    if (fontNames.hasDifferentValues()) {
                        toolbarViewModal.fontName.value = Optional.empty()
                    } else {
                        fontNames.firstOrNull()?.let { toolbarViewModal.fontName.value = Optional.of(it) }
                    }

                    if (fontFamilies.hasDifferentValues()) {
                        toolbarViewModal.fontFamily.value = Optional.empty()
                    } else {
                        fontFamilies.firstOrNull()?.let { toolbarViewModal.fontFamily.value = Optional.of(it) }
                    }

                    if (fontSizes.hasDifferentValues()) {
                        toolbarViewModal.fontSize.value = Optional.empty()
                    } else {
                        fontSizes.firstOrNull()?.let { toolbarViewModal.fontSize.value = Optional.of(it)}
                    }

                    if (bolds.hasDifferentValues()) {
                        toolbarViewModal.bold.value = Optional.empty()
                    } else {
                        // if any of the bold styles are true they all must be and set the view modal accordingly
                        toolbarViewModal.bold.value = Optional.of(bolds.any { it })
                    }

                    if (italics.hasDifferentValues()) {
                        toolbarViewModal.italic.value = Optional.empty()
                    } else {
                        toolbarViewModal.italic.value = Optional.of(italics.any { it })
                    }
                }
                toolbarViewModal.triggerLabelChange()
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