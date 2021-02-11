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
                stackpane {
                    val fontSelector = FontSelectorDialog(Font.font(textViewModal.fontFamily.value))
                    alignment = Pos.CENTER
                    prefHeight = 32.0
                    prefWidth = 120.0
                    style {
                        backgroundColor += Color.WHITE
                    }
                    val textLabel = label(Font.getDefault().name)
                    textViewModal.fontFamily.onChange {
                        if (!it.isNullOrEmpty()) {
                            textLabel.font = Font(it, textViewModal.fontSize.value?.toDouble() ?: 12.0)
                        } else {
                            textLabel.font = Font.getDefault()
                        }
                    }
                    textViewModal.fontName.onChange {
                        if (!it.isNullOrEmpty()) {
                            textLabel.text = textViewModal.fontName.value
                        } else {
                            textLabel.text = Font.getDefault().name
                        }
                    }
                    onLeftClick {
                        val font = fontSelector.showAndWait()
                        if (font.isPresent) {
                            textViewModal.fontSize.value = font.get().size.toString()
                            textViewModal.fontName.value = font.get().name
                            textViewModal.fontFamily.value = font.get().family
                            textViewModal.triggerFontChange()
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
                togglebutton(ToggleGroup()) {
                    addClass(Styles.hoverPopup)
                    addClass(Styles.toolbarButton)
                    isSelected = false
                    background = null
                    graphic = MaterialIconView(MaterialIcon.FORMAT_ALIGN_LEFT).apply { glyphSize = 24 }
                    action { }
                }
                togglebutton(ToggleGroup()) {
                    addClass(Styles.hoverPopup)
                    addClass(Styles.toolbarButton)
                    isSelected = false
                    background = null
                    graphic = MaterialIconView(MaterialIcon.FORMAT_ALIGN_CENTER).apply { glyphSize = 24 }
                    action { }
                }
                togglebutton(ToggleGroup()) {
                    addClass(Styles.hoverPopup)
                    addClass(Styles.toolbarButton)
                    isSelected = false
                    background = null
                    graphic = MaterialIconView(MaterialIcon.FORMAT_ALIGN_RIGHT).apply { glyphSize = 24 }
                    action { }
                }
                togglebutton(ToggleGroup()) {
                    addClass(Styles.hoverPopup)
                    addClass(Styles.toolbarButton)
                    isSelected = false
                    background = null
                    graphic = MaterialIconView(MaterialIcon.FORMAT_ALIGN_JUSTIFY).apply { glyphSize = 24 }
                    action { }
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