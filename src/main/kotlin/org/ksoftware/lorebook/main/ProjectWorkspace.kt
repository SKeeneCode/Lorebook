package org.ksoftware.lorebook.main

import ch.micheljung.fxwindow.FxStage
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.MenuBar
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.controlsfx.dialog.FontSelectorDialog
import org.ksoftware.lorebook.controls.TextSizePicker
import org.ksoftware.lorebook.newproject.NewProjectView
import org.ksoftware.lorebook.nodes.TextController
import org.ksoftware.lorebook.richtext.TextAlignment
import org.ksoftware.lorebook.richtext.ToolbarViewModal
import org.ksoftware.lorebook.styles.Styles
import tornadofx.*
import java.util.*

/**
 * Workspace implementation used for a projects main window.
 */
class ProjectWorkspace : Workspace("Lorebook", NavigationMode.Tabs) {

    private val projectWorkspaceController: ProjectWorkspaceController by inject()
    private val projectViewModel: ProjectViewModel by inject()
    private val toolbarViewModal: ToolbarViewModal by inject()
    private val textController: TextController by inject(FX.defaultScope)

    override fun onDock() {
        val stage = FxStage.configure(currentStage).apply()
        currentStage?.width = 1200.0
        currentStage?.height = 800.0
        saveButton.removeFromParent()
        refreshButton.removeFromParent()
        deleteButton.removeFromParent()
        createButton.removeFromParent()
        showHeadingLabel = false

        header.items.find { it.hasClass("spacer") }?.removeFromParent()

        val headerMenu = HBox(1.0)
        val menu = MenuBar().apply {
            background = null
            menu("File")
            menu("Edit")
            menu("View")
            menu("Help")
        }
        headerMenu.children.addAll(menu)
        header.parent.getChildList()?.add(0, headerMenu)

        stage.nonCaptionNodes.addAll(menu)
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
                val fontSelector =
                    FontSelectorDialog(Font.font(toolbarViewModal.fontFamily.value.orElse(Font.getDefault().family)))
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
            }

            hbox(2.0) {
                val alignmentToggleGroup = ToggleGroup()
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
            }
            hbox(2.0) {
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