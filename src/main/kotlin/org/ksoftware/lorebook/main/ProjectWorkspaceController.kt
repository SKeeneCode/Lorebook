package org.ksoftware.lorebook.main

import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.ksoftware.lorebook.actions.SaveProjectAction
import org.ksoftware.lorebook.pages.PageModel
import org.ksoftware.lorebook.pages.PageView
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.richtext.ToolbarViewModal
import tornadofx.Controller
import tornadofx.UIComponent
import tornadofx.Workspace
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * Controller for the project workspace.
 */
class ProjectWorkspaceController : Controller(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.JavaFx
    private val projectViewModel: ProjectViewModel by inject()
    private val toolbarViewModal: ToolbarViewModal by inject()
    private val saveProjectActor: SendChannel<SaveProjectAction> = createSaveActor()


    /**
     * Creates a new page and docks it in the provided workspace
     */
    fun dockNewPage(workspace: Workspace) {
        val newPage = PageModel()
        projectViewModel.pages.value.add(newPage)
        dockPageView(newPage, workspace)
    }

    /**
     * Docks the PageView associated with the provided PageModel in the workspace.
     * Checks the cache of PageViews to see if the pages view is in there.
     * If not, creates a new page view in a new scope and docks it.
     */
    fun dockPageView(page: PageModel, workspace: Workspace) {
        launch {
            workspace.dockInNewScope<PageView>(PageViewModel(page), projectViewModel, toolbarViewModal)
        }
    }

    // --------------------------------------- //
    //                 SAVING                  //
    // --------------------------------------- //

    fun saveProject(stage: Stage) {
            saveProjectActor.offer(SaveProjectAction(projectViewModel.item, stage))
    }

    /**
     * Creates an actor that processes one save action at a time. Will block any further save actions until the
     * current one has finished processing.
     */
    private fun createSaveActor() = this.actor<SaveProjectAction> {
        for (saveAction in channel) {
            val projectSaveFolder = askUserForProjectSaveFolder(saveAction.stage)
            projectSaveFolder?.let { saveProjectInLocation(saveAction.project, it) }
        }
    }

    private suspend fun saveProjectInLocation(projectModel: ProjectModel, saveLocation: File) {
        projectModel.save(saveLocation, projectViewModel.taskMessage)
    }

    private fun askUserForProjectSaveFolder(stage: Stage) : File? {
        val directoryChooser = DirectoryChooser()
        directoryChooser.initialDirectory = File("/")
        return directoryChooser.showDialog(stage)
    }

    // --------------------------------------- //
    //                 OVERLAY                  //
    // --------------------------------------- //

    /**
     * Causes the overlay to appear with this UIComponent centered.
     */
    fun openOverlayWith(uiComponent: UIComponent) {
        projectViewModel.overlayNode.value = uiComponent
    }

    /**
     * Removes the UIComponent from the overlay and closes the overlay.
     */
    fun closeOverlay() {
        projectViewModel.overlayNode.value = null
    }


}
