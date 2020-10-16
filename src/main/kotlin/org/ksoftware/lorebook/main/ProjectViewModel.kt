package org.ksoftware.lorebook.main

import javafx.collections.FXCollections
import org.ksoftware.lorebook.pages.PageModel
import tornadofx.*

/**
 * View model for a ProjectModel. When instantiated will bind to the provided ProjectModel or create a new
 * one if one isn't provided.
 */
class ProjectViewModel(model: ProjectModel = ProjectModel()) : ItemViewModel<ProjectModel>() {

    init {
        item = model
    }

    val dockedPages = bind(ProjectModel::dockedPages)
    val pages = bind(ProjectModel::pages)
    private var pagesBacking = listOf<PageModel>()

    init {
        // copies the list to its backing list after initial binding
        pagesBacking = pages.value.map { it.copy() }
    }

    override fun onCommit() {
        pagesBacking = pages.value.map { it.copy() }
    }

    fun rollBackPages() {
        pages.value = pagesBacking.map { it.copy() }.asObservable()
        super.commit(pages) {}
    }

}