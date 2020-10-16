package org.ksoftware.lorebook.main

import org.ksoftware.lorebook.pages.Page
import org.ksoftware.lorebook.pages.PageModel
import org.ksoftware.lorebook.pages.PageViewModel
import tornadofx.*

/**
 * Controller for the project workspace. This controller should always be injected from the same scope
 * so that it remains a singleton.
 */
class ProjectWorkspaceController : Controller() {

    private val projectViewModel: ProjectViewModel by inject(FX.defaultScope)

    fun dockNewPage(workspace: Workspace) {
        val newPage = PageModel()
        dockPage(newPage, workspace)
        projectViewModel.pages.value.add(newPage)
    }

    fun dockPage(page: PageModel, workspace: Workspace) {
        val dockedPages = projectViewModel.dockedPages.value
        var pageToDock = dockedPages[page.idProperty.get()]
        if (pageToDock != null) {
            workspace.dock(pageToDock)
        } else {
            val viewModel = PageViewModel(page)
            val newScope = Scope(viewModel)
            newScope.workspace = workspace
            pageToDock = find(Page::class, newScope)
            dockedPages[page.idProperty.get()] = pageToDock
            workspace.dock(pageToDock)
        }
    }

}