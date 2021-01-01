package org.ksoftware.lorebook.main

import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.javafx.JavaFx
import org.ksoftware.lorebook.actions.SaveProjectAction
import org.ksoftware.lorebook.pages.PageView
import org.ksoftware.lorebook.pages.PageModel
import org.ksoftware.lorebook.pages.PageViewModel
import tornadofx.*
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * Controller for the project workspace.
 */
class ProjectWorkspaceController : Controller(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.JavaFx
    private val projectViewModel: ProjectViewModel by inject()
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
            val cache = projectViewModel.pageViewCache.value
            val pageToDock = cache[page.idProperty.get()]
            if (pageToDock != null) {
                workspace.dock(pageToDock)
            } else {
                workspace.dockInNewScope<PageView>(PageViewModel(page), projectViewModel)
              //  cache[page.idProperty.get()] = workspace.dockedComponent as PageView
            }
        }
    }

    // --------------------------------------- //
    //                 SAVING                  //
    // --------------------------------------- //

    fun saveProject(stage: Stage) {
        launch {
            saveProjectActor.offer(SaveProjectAction(projectViewModel.item, stage))
        }
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


}
