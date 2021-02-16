package org.ksoftware.lorebook.navigator

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
        prefWidth = 200.0
        button("add a page") {
            action {
                projectController.dockNewPage(projectViewModel.projectWorkspace)
            }
        }
        listview(projectViewModel.pages) {
            cellFormat {
                text = this.item.toString()
                this.onDoubleClick {
                    projectController.dockPageView(this.item, projectViewModel.projectWorkspace)
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