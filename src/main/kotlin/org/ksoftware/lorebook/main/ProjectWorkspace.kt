package org.ksoftware.lorebook.main

import ch.micheljung.fxwindow.FxStage
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.scene.control.MenuBar
import javafx.scene.layout.*
import javafx.scene.paint.Color
import org.ksoftware.lorebook.newproject.NewProjectView
import org.ksoftware.lorebook.richtext.RichTextToolbarView
import org.ksoftware.lorebook.richtext.ToolbarViewModal
import org.ksoftware.lorebook.styles.Styles
import tornadofx.*
import java.util.*

/**
 * Workspace implementation used for a projects main window. Customised to include a richtext toolbar and overlay.
 */
class ProjectWorkspace : Workspace("Lorebook", NavigationMode.Tabs) {

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

    override fun onDock() {
        println("I am being docked")
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


    init {
        with(header) {
            style {
                background = null
                spacing = 0.px
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
            add<RichTextToolbarView>()
        }
    }

    init {
        with(leftDrawer) {
            item("all pages", expanded = true) {
                vbox {
                    prefWidth = 200.0
                    button("add a page") {
                        action {
                            projectController.dockNewPage(this@ProjectWorkspace)
                        }
                    }
                    listview(projectViewModel.pages) {
                        cellFormat {
                            text = this.item.toString()
                            this.onDoubleClick {
                                projectController.dockPageView(this.item, this@ProjectWorkspace)
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
                            currentStage?.let { projectController.saveProject(it) }
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