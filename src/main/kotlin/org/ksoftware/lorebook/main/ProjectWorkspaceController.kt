package org.ksoftware.lorebook.main

import javafx.stage.DirectoryChooser
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import org.ksoftware.lorebook.pages.PageView
import org.ksoftware.lorebook.pages.PageModel
import org.ksoftware.lorebook.pages.PageViewModel
import tornadofx.*
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * Controller for the project workspace. This controller should always be injected from the same scope
 * so that it remains a singleton.
 */
class ProjectWorkspaceController : Controller(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.JavaFx
    private val projectViewModel: ProjectViewModel by inject(FX.defaultScope)

    /**
     * Creates a new page and docks it in the provided workspace
     */
    fun dockNewPage(workspace: Workspace) {
        val newPage = PageModel()
        dockPageView(newPage, workspace)
        projectViewModel.pages.value.add(newPage)
    }

    /**
     * Docks the PageView associated with the provided PageModel in the workspace.
     * Checks the cache of PageViews to see if the pages view is in there.
     * If not, creates a new page view in a new scope.
     */
    fun dockPageView(page: PageModel, workspace: Workspace) {
        val cache = projectViewModel.pageViewCache.value
        val pageToDock = cache[page.idProperty.get()]
        if (pageToDock != null) {
            workspace.dock(pageToDock)
        } else {
            workspace.dockInNewScope<PageView>(PageViewModel(page))
            cache[page.idProperty.get()] = workspace.dockedComponent as PageView
        }
    }

    fun saveProject() {
        val projectSaveFolder = askUserForProjectSaveFolder()
        launch {
            projectViewModel.taskMessage.value = "Saving Project"
            val jobs: List<Job> = projectViewModel.pages.value.map {
                launch {
                    it.save(projectSaveFolder, projectViewModel.taskMessage)
                }
            }
            jobs.joinAll()
            projectViewModel.taskMessage.value = "Finished Saving"
        }
    }

    private fun askUserForProjectSaveFolder() : File {
        val directoryChooser = DirectoryChooser()
        return directoryChooser.showDialog(primaryStage)
    }
}
