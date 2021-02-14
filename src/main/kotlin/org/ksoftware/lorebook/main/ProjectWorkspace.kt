package org.ksoftware.lorebook.main

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import org.controlsfx.dialog.FontSelectorDialog
import org.ksoftware.lorebook.Styles
import org.ksoftware.lorebook.controls.TextSizePicker
import org.ksoftware.lorebook.newproject.NewProjectView
import org.ksoftware.lorebook.nodes.TextController
import org.ksoftware.lorebook.organiser.Organiser
import org.ksoftware.lorebook.richtext.RichTextViewModal
import org.ksoftware.lorebook.richtext.TextAlignment
import tornadofx.*

/**
 * Workspace implementation used for a projects main window.
 */
class ProjectWorkspace : Workspace("Lorebook", NavigationMode.Tabs) {

    private val projectWorkspaceController: ProjectWorkspaceController by inject()
    private val projectViewModel: ProjectViewModel by inject()
    private val textViewModal: RichTextViewModal by inject(FX.defaultScope)
    private val textController: TextController by inject(FX.defaultScope)

    override fun onDock() {
        super.onDock()
        currentStage?.width = 1200.0
        currentStage?.height = 800.0
        saveButton.removeFromParent()
        refreshButton.removeFromParent()
        deleteButton.removeFromParent()
        createButton.removeFromParent()
        showHeadingLabel = false

        header.items.find { it.hasClass("spacer") }?.removeFromParent()

        menubar {
            background = null
            menu("File")
            menu("Edit")
            menu("View")
            menu("Help")
        }
    }

    init {
        with(header) {
            style {
                spacing = 0.px
            }
            hbox(2.0) {
                togglebutton(ToggleGroup()) {
                    addClass(Styles.hoverPopup)
                    addClass(Styles.toolbarButton)
                    isSelected = false
                    background = null
                    graphic = MaterialIconView(MaterialIcon.HOME).apply {
                        glyphSize = 24
                    }
                    action { }
                }
                separator(Orientation.VERTICAL)
            }
            hbox(2.0) {
                togglebutton(ToggleGroup()) {
                    addClass(Styles.hoverPopup)
                    addClass(Styles.toolbarButton)
                    isSelected = false
                    background = null
                    graphic = MaterialIconView(MaterialIcon.NOTE_ADD).apply {
                        glyphSize = 26
                        fill = Color.DARKGREEN
                    }
                    action { }
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
            }
            hbox(2.0) {
                    val fontSelector = FontSelectorDialog(Font.font(textViewModal.fontFamily.value))
                     label(textViewModal.fontName) {
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
                                textViewModal.fontSize.value = font.get().size.toString()
                                textViewModal.fontName.value = font.get().name
                                textViewModal.fontFamily.value = font.get().family
                                when (font.get().style) {
                                     "Bold" -> textViewModal.bold.value = "true"
                                    "Italic" -> textViewModal.italic.value = "true"
                                    "Bold Italic" -> {
                                        textViewModal.bold.value = "true"
                                        textViewModal.italic.value = "true"
                                    }
                                    "Regular" -> {
                                        textViewModal.bold.value = null
                                        textViewModal.italic.value = null
                                    }
                                }
                                textViewModal.triggerTextChange()
                                textViewModal.triggerFontChange()
                            }
                        }

                        textViewModal.fontSize.onChange {
                            if (!it.isNullOrEmpty()) {
                                val size = if (it.toDouble() > 24.0) 24.0 else it.toDouble()
                                font = Font(textViewModal.fontName.value, size)
                            }
                        }
                    }
                add<TextSizePicker>()
                togglebutton(ToggleGroup()) {
                    addClass(Styles.hoverPopup)
                    addClass(Styles.toolbarButton)
                    selectedProperty().onChange { textViewModal.bold.value = it.toString() }
                    textViewModal.bold.onChange { isSelected = it.toBoolean() }
                    isSelected = false
                    background = null
                    graphic = MaterialIconView(MaterialIcon.FORMAT_BOLD).apply { glyphSize = 24 }
                    action {
                        textViewModal.triggerTextChange()
                    }
                }
                togglebutton(ToggleGroup()) {
                    addClass(Styles.hoverPopup)
                    addClass(Styles.toolbarButton)
                    selectedProperty().onChange { textViewModal.italic.value = it.toString() }
                    textViewModal.italic.onChange { isSelected = it.toBoolean() }
                    isSelected = false
                    background = null
                    graphic = MaterialIconView(MaterialIcon.FORMAT_ITALIC).apply { glyphSize = 24 }
                    action {
                        textViewModal.triggerTextChange()
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
            }

            hbox(2.0) {
                val alignmentToggleGroup = ToggleGroup()
                togglebutton(alignmentToggleGroup) {
                    addClass(Styles.hoverPopup)
                    addClass(Styles.toolbarButton)
                    isSelected = false
                    background = null
                    graphic = MaterialIconView(MaterialIcon.FORMAT_ALIGN_LEFT).apply { glyphSize = 24 }
                    textViewModal.alignment.onChange { if (it == TextAlignment.LEFT) isSelected = true }
                    action {
                        textViewModal.alignment.value = TextAlignment.LEFT
                        textViewModal.triggerParagraphChange()
                    }
                }
                togglebutton(alignmentToggleGroup) {
                    addClass(Styles.hoverPopup)
                    addClass(Styles.toolbarButton)
                    isSelected = false
                    background = null
                    graphic = MaterialIconView(MaterialIcon.FORMAT_ALIGN_CENTER).apply { glyphSize = 24 }
                    textViewModal.alignment.onChange { if (it == TextAlignment.CENTER) isSelected = true }
                    action {
                        textViewModal.alignment.value = TextAlignment.CENTER
                        textViewModal.triggerParagraphChange()
                    }
                }
                togglebutton(alignmentToggleGroup) {
                    addClass(Styles.hoverPopup)
                    addClass(Styles.toolbarButton)
                    isSelected = false
                    background = null
                    graphic = MaterialIconView(MaterialIcon.FORMAT_ALIGN_RIGHT).apply { glyphSize = 24 }
                    textViewModal.alignment.onChange { if (it == TextAlignment.RIGHT) isSelected = true }
                    action {
                        textViewModal.alignment.value = TextAlignment.RIGHT
                        textViewModal.triggerParagraphChange()
                    }
                }
                togglebutton(alignmentToggleGroup) {
                    addClass(Styles.hoverPopup)
                    addClass(Styles.toolbarButton)
                    isSelected = false
                    background = null
                    graphic = MaterialIconView(MaterialIcon.FORMAT_ALIGN_JUSTIFY).apply { glyphSize = 24 }
                    textViewModal.alignment.onChange { if (it == TextAlignment.JUSTIFY) isSelected = true }
                    action {
                        textViewModal.alignment.value = TextAlignment.JUSTIFY
                        textViewModal.triggerParagraphChange()
                    }
                }
                separator(Orientation.VERTICAL)
            }
            hbox(2.0) {
                button {
                    addClass(Styles.hoverPopup)
                    addClass(Styles.toolbarButton)
                    background = null
                    graphic = MaterialIconView(MaterialIcon.FORMAT_INDENT_INCREASE).apply { glyphSize = 24 }
                    action {
                        textViewModal.triggerIndentIncrease()
                    }
                }
                button {
                    addClass(Styles.hoverPopup)
                    addClass(Styles.toolbarButton)
                    background = null
                    graphic = MaterialIconView(MaterialIcon.FORMAT_INDENT_DECREASE).apply { glyphSize = 24 }
                    action {
                        textViewModal.triggerIndentDecrease()
                    }
                }
                separator(Orientation.VERTICAL)
            }
            hbox(2.0) {
                label(projectViewModel.taskMessage)
            }
            background = null
        }
    }

    init {
        with(leftDrawer) {
            item("all pages", expanded = true) {
                vbox {
                    prefWidth = 200.0
                    button("add a page") {
                        action {
                            projectWorkspaceController.dockNewPage(this@ProjectWorkspace)
                        }
                    }
                    listview(projectViewModel.pages) {
                        cellFormat {
                            text = this.item.toString()
                            this.onDoubleClick {
                                projectWorkspaceController.dockPageView(this.item, this@ProjectWorkspace)
                            }
                        }
                    }
                    button("print") {
                        action {
                            println(projectViewModel.pages.value.toString())
                        }
                    }
                    button("reset") {
                        enableWhen(projectViewModel.dirty)
                        action {
                            projectViewModel.rollback()
                            projectViewModel.rollBackPages()
                        }
                    }
                    button("save") {
                        enableWhen(projectViewModel.dirty)
                        action {
                            projectViewModel.commit()
                        }
                    }
                    button("save project") {
                        action {
                            currentStage?.let { projectWorkspaceController.saveProject(it) }
                        }
                    }
                    button("new project") {
                        action {
                            find(NewProjectView::class).openModal()
                        }
                    }
                }
            }
        }
    }
}