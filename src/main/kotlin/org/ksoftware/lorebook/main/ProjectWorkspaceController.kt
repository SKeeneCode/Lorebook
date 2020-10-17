package org.ksoftware.lorebook.main

import org.ksoftware.lorebook.pages.PageView
import org.ksoftware.lorebook.pages.PageModel
import org.ksoftware.lorebook.pages.PageViewModel
import tornadofx.*

/**
 * Controller for the project workspace. This controller should always be injected from the same scope
 * so that it remains a singleton.
 */
class ProjectWorkspaceController : Controller() {

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
   // workspace.dockInNewScope<Page>(PageViewModel(page))
}