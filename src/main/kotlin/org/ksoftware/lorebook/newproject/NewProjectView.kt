package org.ksoftware.lorebook.newproject

import ch.micheljung.fxwindow.FxStage
import javafx.scene.Scene
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import org.ksoftware.lorebook.main.ProjectWorkspace
import tornadofx.*
import java.io.File

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
                            directoryChooser.initialDirectory = File("/")
                            val folder = directoryChooser.showDialog(primaryStage)
                            folder?.let { newProjectViewModel.newProjectSaveDirectory.value = folder.toString()  }
                        }
                    }
                }
            }
            button("Open") {
                action {
                    close()
                    // Customize initialization of a ProjectWorkspace to ensure compatibility with FxStage
                    val view = find(ProjectWorkspace::class, Scope())
                    view.muteDocking = true
                    val myScene = Scene(view.root)
                    val newStage = Stage()
                    newStage.scene = myScene
                    val fxStage = FxStage.configure(newStage).apply()
                    fxStage.stage.apply {
                        aboutToBeShown = true
                        view.properties["tornadofx.scene"] = scene
                        FX.applyStylesheetsTo(scene)
                        titleProperty().bind(view.titleProperty)
                        hookGlobalShortcuts()
                        view.onBeforeShow()
                        view.muteDocking = false
                        show()
                        view.configureFxStage(fxStage)
                        aboutToBeShown = false
                    }
                }
            }
        }
    }
}
