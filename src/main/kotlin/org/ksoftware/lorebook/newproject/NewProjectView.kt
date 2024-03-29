package org.ksoftware.lorebook.newproject

import ch.micheljung.fxwindow.FxStage
import com.pixelduke.control.skin.FXSkins
import javafx.scene.Scene
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
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
                    val newStage = Stage()
                    val fxStage = FxStage.configure(newStage).withSceneFactory {
                            parent -> Scene(parent)
                    }.apply()
                    fxStage.stage.apply {
                        aboutToBeShown = true
                        view.properties["tornadofx.scene"] = scene
                        JMetro(scene, Style.LIGHT)
                        //scene.stylesheets.add(FXSkins.getStylesheetURL());
                        FX.applyStylesheetsTo(scene)
                        titleProperty().bind(view.titleProperty)
                        hookGlobalShortcuts()
                        view.onBeforeShow()
                        view.muteDocking = false
                        fxStage.setContent(view.root)
                        newStage.show()
                        view.configureFxStage(fxStage)
                        aboutToBeShown = false
                    }
                }
            }
        }
    }
}
