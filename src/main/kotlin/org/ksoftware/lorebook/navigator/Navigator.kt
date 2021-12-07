package org.ksoftware.lorebook.navigator

import javafx.geometry.Pos
import javafx.scene.paint.Color
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.main.ProjectWorkspaceController
import org.ksoftware.lorebook.newproject.NewProjectView
import tornadofx.*

/**
 * The navigator contains navigation options for the user, such as recently visited pages.
 */
class Navigator : View() {

    private val projectController: ProjectWorkspaceController by inject()
    private val projectViewModel: ProjectViewModel by inject()

    override val root = vbox {

        add<Bookmarks>()

        prefWidth = 200.0
        button("load pages") {
            action {
                projectController.loadProject()
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
        button("add page above") {
            action {
                projectController.dockNewPage(Pos.TOP_CENTER)
            }
        }
        button("add page right") {
            action {
                projectController.dockNewPage(Pos.CENTER_RIGHT)
            }
        }
        button("add page left") {
            action {
                projectController.dockNewPage(Pos.CENTER_LEFT)
            }
        }
        button("add page below") {
            action {
                projectController.dockNewPage(Pos.BOTTOM_CENTER)
            }
        }
        button("add page Center") {
            action {
                projectController.dockNewPage(Pos.CENTER)
            }
        }

        button("show focused") {
            action {
                workspace.focusedTabPane.value.apply {
                    style {
                        backgroundColor += Color.BLACK
                    }
                }
            }
        }
    }

}