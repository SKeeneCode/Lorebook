package org.ksoftware.lorebook.newproject

import javafx.stage.DirectoryChooser
import org.ksoftware.lorebook.main.ProjectWorkspace
import tornadofx.*

/**
 * View class containing the controls allowing a user to create a new project.
 */
class NewProjectView : View("New Project") {

    private val newProjectViewModel: NewProjectViewModel by inject()

    override val root = vbox {
        form {
            fieldset("New Project") {
                field("Project Name:") {
                    textfield(newProjectViewModel.newProjectName)
                }
                field("Project Folder:") {
                    textfield(newProjectViewModel.newProjectSaveDirectory)
                    button("Find") {
                        action {
                            val directoryChooser = DirectoryChooser()
                            val folder = directoryChooser.showDialog(primaryStage)
                            newProjectViewModel.newProjectSaveDirectory.value = folder.toString()
                        }
                    }
                }
            }
            button("Open") {
                action {
                    close()
                    find(ProjectWorkspace::class, Scope()).openWindow()
                }
            }
        }
    }
}
