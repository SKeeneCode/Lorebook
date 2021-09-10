package org.ksoftware.lorebook.main

import ch.micheljung.fxwindow.FxStage
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.control.MenuBar
import javafx.scene.control.Separator
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.controlsfx.dialog.FontSelectorDialog
import org.ksoftware.lorebook.navigator.Navigator
import org.ksoftware.lorebook.nodes.TextController
import org.ksoftware.lorebook.richtext.TextAlignment
import org.ksoftware.lorebook.richtext.TextSizePicker
import org.ksoftware.lorebook.richtext.ToolbarViewModal
import org.ksoftware.lorebook.styles.Styles
import org.ksoftware.lorebook.timeline.CalendarWizard
import tornadofx.*
import java.util.*

/**
 * Workspace implementation used for a projects main window. Customised to include a rich text toolbar and overlay.
 */
class ProjectWorkspace : Workspace("Lorebook", NavigationMode.Tabs) {

    private val textController: TextController by inject(FX.defaultScope)
    private val projectController: ProjectWorkspaceController by inject()
    private val projectViewModel: ProjectViewModel by inject()
    private val toolbarViewModal: ToolbarViewModal by inject()

    private val overlay = Pane().apply {
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS
        visibleWhen(projectViewModel.showOverlay)
        style {
            backgroundColor += Color.color(0.5,0.5,0.5,0.5)
        }
        onLeftClick {
            projectController.closeOverlay()
        }
    }
    private val sceneContainer = StackPane().apply {
        projectViewModel.overlayNode.addListener(ChangeListener { _, oldValue, newValue ->
            if (newValue != null) {
                add(newValue)
                projectViewModel.showOverlay.value = true
            } else {
                oldValue?.removeFromParent()
                projectViewModel.showOverlay.value = false
            }
        })
    }

    init {
        this.scope.workspace(this)
        projectController.connectToolbarViewModalToRichTextAreas()
    }

    init {
        with(header) {
            style {
                background = null
                spacing = 2.px
            }
            button {
                addClass(Styles.hoverPopup)
                addClass(Styles.toolbarButton)
                background = null
                graphic = MaterialIconView(MaterialIcon.HOME).apply {
                    glyphSize = 24
                }
                action { }
            }
            val fontSelector = FontSelectorDialog(Font.font(toolbarViewModal.fontFamily.value.orElse(Font.getDefault().family)))
            val alignmentToggleGroup = ToggleGroup()
            add(Separator())
            button {
                addClass(Styles.hoverPopup)
                addClass(Styles.toolbarButton)
                background = null
                graphic = MaterialIconView(MaterialIcon.NOTE_ADD).apply {
                    glyphSize = 26
                    fill = Color.DARKGREEN
                }
                action {
                    projectController.dockNewPage()
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
            add(Separator())
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

                disableWhen(projectViewModel.currentRichText.isNull)

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
                disableWhen(projectViewModel.currentRichText.isNull)
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
                disableWhen(projectViewModel.currentRichText.isNull)
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
                disableWhen(projectViewModel.currentRichText.isNull)
                action { }
            }
            togglebutton(ToggleGroup()) {
                addClass(Styles.hoverPopup)
                addClass(Styles.toolbarButton)
                isSelected = false
                background = null
                graphic = MaterialIconView(MaterialIcon.FORMAT_STRIKETHROUGH).apply { glyphSize = 24 }
                disableWhen(projectViewModel.currentRichText.isNull)
                action { }
            }
            add(Separator())
            togglebutton(alignmentToggleGroup) {
                addClass(Styles.hoverPopup)
                addClass(Styles.toolbarButton)
                isSelected = false
                background = null
                graphic = MaterialIconView(MaterialIcon.FORMAT_ALIGN_LEFT).apply { glyphSize = 24 }
                toolbarViewModal.alignment.onChange { if (it == TextAlignment.LEFT) isSelected = true }
                disableWhen(projectViewModel.currentRichText.isNull)
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
                disableWhen(projectViewModel.currentRichText.isNull)
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
                disableWhen(projectViewModel.currentRichText.isNull)
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
                disableWhen(projectViewModel.currentRichText.isNull)
                action {
                    toolbarViewModal.alignment.value = TextAlignment.JUSTIFY
                    toolbarViewModal.triggerParagraphChange()
                }
            }
            add(Separator())
            button {
                addClass(Styles.hoverPopup)
                addClass(Styles.toolbarButton)
                background = null
                graphic = MaterialIconView(MaterialIcon.FORMAT_INDENT_INCREASE).apply { glyphSize = 24 }
                disableWhen(projectViewModel.currentRichText.isNull)
                action {
                    toolbarViewModal.triggerIndentIncrease()
                }
            }
            button {
                addClass(Styles.hoverPopup)
                addClass(Styles.toolbarButton)
                background = null
                graphic = MaterialIconView(MaterialIcon.FORMAT_INDENT_DECREASE).apply { glyphSize = 24 }
                disableWhen(projectViewModel.currentRichText.isNull)
                action {
                    toolbarViewModal.triggerIndentDecrease()
                }
            }
            add(Separator())
            label(projectViewModel.taskMessage)
        }
    }

    init {
        with(leftDrawer) {
            item("Navigator", expanded = true) {
                add<Navigator>()
            }
        }
    }

    /**
     * Configures the look of the workspace as well as rearrange the scene so the workspace is wrapped in
     * a stackpane along with the overlay.
     */
    fun configureFxStage(fxStage: FxStage) {
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
            menu("Timeline") {
                item("New Calendar") {
                    action {
                        projectController.openOverlayWith(find(CalendarWizard::class))
                    }
                }
            }
            menu("Help")
        }
        headerMenu.children.addAll(menu)
        header.parent.getChildList()?.add(0, headerMenu)
        fxStage.nonCaptionNodes.addAll(menu)
        this.muteDocking = true
        sceneContainer.children.addAll(root,overlay)
        this.muteDocking = false
        fxStage.setContent(sceneContainer)
    }
}