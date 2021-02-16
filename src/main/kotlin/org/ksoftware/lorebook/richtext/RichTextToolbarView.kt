package org.ksoftware.lorebook.richtext

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.controlsfx.dialog.FontSelectorDialog
import org.ksoftware.lorebook.controls.TextSizePicker
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.main.ProjectWorkspaceController
import org.ksoftware.lorebook.styles.Styles
import tornadofx.*
import java.util.*

/**
 * Contains a range of rich text controls such as bold, italic buttons. Communicates with the text area via a
 * ToolbarViewModal.
 */
class RichTextToolbarView : View() {

    private val projectController: ProjectWorkspaceController by inject()
    private val projectViewModel: ProjectViewModel by inject()
    private val toolbarViewModal: ToolbarViewModal by inject()

    override val root = hbox {
        val fontSelector = FontSelectorDialog(Font.font(toolbarViewModal.fontFamily.value.orElse(Font.getDefault().family)))
        val alignmentToggleGroup = ToggleGroup()
        separator(Orientation.VERTICAL)
        button {
            addClass(Styles.hoverPopup)
            addClass(Styles.toolbarButton)
            background = null
            graphic = MaterialIconView(MaterialIcon.NOTE_ADD).apply {
                glyphSize = 26
                fill = Color.DARKGREEN
            }
            action {
                projectController.dockNewPage(projectViewModel.projectWorkspace)
            }
        }
        togglebutton(ToggleGroup()) {
            addClass(Styles.hoverPopup)
            addClass(Styles.toolbarButton)
            isSelected = false
            background = null
            graphic = MaterialIconView(MaterialIcon.ADD_BOX).apply {
                glyphSize = 26
                fill = Color.DARKGREEN
            }
            action { }
        }
        separator(Orientation.VERTICAL)
        label {
            style {
                backgroundColor += Color.WHITE
            }
            alignment = Pos.CENTER
            minWidth = Region.USE_PREF_SIZE
            prefWidth = 120.0
            maxWidth = Region.USE_PREF_SIZE
            minHeight = Region.USE_PREF_SIZE
            prefHeight = 32.0
            maxHeight = Region.USE_PREF_SIZE

            onLeftClick {
                val font = fontSelector.showAndWait()
                if (font.isPresent) {
                    toolbarViewModal.fontSize.value = Optional.of(font.get().size)
                    toolbarViewModal.fontName.value = Optional.of(font.get().name)
                    toolbarViewModal.fontFamily.value = Optional.of(font.get().family)
                    when (font.get().style) {
                        "Bold" -> toolbarViewModal.bold.value = Optional.of(true)
                        "Italic" -> toolbarViewModal.italic.value = Optional.of(true)
                        "Bold Italic" -> {
                            toolbarViewModal.bold.value = Optional.of(true)
                            toolbarViewModal.italic.value = Optional.of(true)
                        }
                        "Regular" -> {
                            toolbarViewModal.bold.value = Optional.of(false)
                            toolbarViewModal.italic.value = Optional.of(false)
                        }
                    }
                    toolbarViewModal.triggerTextChange()
                    toolbarViewModal.updateFontSelectionText(this)
                }
            }

            toolbarViewModal.updateLabelTrigger.onChange {
                toolbarViewModal.updateFontSelectionText(this)
            }


        }
        add<TextSizePicker>()
        togglebutton(ToggleGroup()) {
            addClass(Styles.hoverPopup)
            addClass(Styles.toolbarButton)
            selectedProperty().onChange { toolbarViewModal.bold.value = Optional.of(it) }
            toolbarViewModal.bold.onChange { if (it != null) isSelected = it.orElse(false) }
            isSelected = false
            background = null
            graphic = MaterialIconView(MaterialIcon.FORMAT_BOLD).apply { glyphSize = 24 }
            action {
                toolbarViewModal.triggerTextChange()
            }
        }
        togglebutton(ToggleGroup()) {
            addClass(Styles.hoverPopup)
            addClass(Styles.toolbarButton)
            selectedProperty().onChange { toolbarViewModal.italic.value = Optional.of(it) }
            toolbarViewModal.italic.onChange { if (it != null) isSelected = it.orElse(false) }
            isSelected = false
            background = null
            graphic = MaterialIconView(MaterialIcon.FORMAT_ITALIC).apply { glyphSize = 24 }
            action {
                toolbarViewModal.triggerTextChange()
            }
        }
        togglebutton(ToggleGroup()) {
            addClass(Styles.hoverPopup)
            addClass(Styles.toolbarButton)
            isSelected = false
            background = null
            graphic = MaterialIconView(MaterialIcon.FORMAT_UNDERLINED).apply { glyphSize = 24 }
            action { }
        }
        togglebutton(ToggleGroup()) {
            addClass(Styles.hoverPopup)
            addClass(Styles.toolbarButton)
            isSelected = false
            background = null
            graphic = MaterialIconView(MaterialIcon.FORMAT_STRIKETHROUGH).apply { glyphSize = 24 }
            action { }
        }
        separator(Orientation.VERTICAL)
        togglebutton(alignmentToggleGroup) {
            addClass(Styles.hoverPopup)
            addClass(Styles.toolbarButton)
            isSelected = false
            background = null
            graphic = MaterialIconView(MaterialIcon.FORMAT_ALIGN_LEFT).apply { glyphSize = 24 }
            toolbarViewModal.alignment.onChange { if (it == TextAlignment.LEFT) isSelected = true }
            action {
                toolbarViewModal.alignment.value = TextAlignment.LEFT
                toolbarViewModal.triggerParagraphChange()
            }
        }
        togglebutton(alignmentToggleGroup) {
            addClass(Styles.hoverPopup)
            addClass(Styles.toolbarButton)
            isSelected = false
            background = null
            graphic = MaterialIconView(MaterialIcon.FORMAT_ALIGN_CENTER).apply { glyphSize = 24 }
            toolbarViewModal.alignment.onChange { if (it == TextAlignment.CENTER) isSelected = true }
            action {
                toolbarViewModal.alignment.value = TextAlignment.CENTER
                toolbarViewModal.triggerParagraphChange()
            }
        }
        togglebutton(alignmentToggleGroup) {
            addClass(Styles.hoverPopup)
            addClass(Styles.toolbarButton)
            isSelected = false
            background = null
            graphic = MaterialIconView(MaterialIcon.FORMAT_ALIGN_RIGHT).apply { glyphSize = 24 }
            toolbarViewModal.alignment.onChange { if (it == TextAlignment.RIGHT) isSelected = true }
            action {
                toolbarViewModal.alignment.value = TextAlignment.RIGHT
                toolbarViewModal.triggerParagraphChange()
            }
        }
        togglebutton(alignmentToggleGroup) {
            addClass(Styles.hoverPopup)
            addClass(Styles.toolbarButton)
            isSelected = false
            background = null
            graphic = MaterialIconView(MaterialIcon.FORMAT_ALIGN_JUSTIFY).apply { glyphSize = 24 }
            toolbarViewModal.alignment.onChange { if (it == TextAlignment.JUSTIFY) isSelected = true }
            action {
                toolbarViewModal.alignment.value = TextAlignment.JUSTIFY
                toolbarViewModal.triggerParagraphChange()
            }
        }
        separator(Orientation.VERTICAL)
        button {
            addClass(Styles.hoverPopup)
            addClass(Styles.toolbarButton)
            background = null
            graphic = MaterialIconView(MaterialIcon.FORMAT_INDENT_INCREASE).apply { glyphSize = 24 }
            action {
                toolbarViewModal.triggerIndentIncrease()
            }
        }
        button {
            addClass(Styles.hoverPopup)
            addClass(Styles.toolbarButton)
            background = null
            graphic = MaterialIconView(MaterialIcon.FORMAT_INDENT_DECREASE).apply { glyphSize = 24 }
            action {
                toolbarViewModal.triggerIndentDecrease()
            }
        }
        separator(Orientation.VERTICAL)
        label(projectViewModel.taskMessage)
    }

}
