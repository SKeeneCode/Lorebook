package org.ksoftware.lorebook.main

import tornadofx.*

/**
 * View model for a ProjectModel. When instantiated will bind to the provided ProjectModel or create a new
 * one if one isn't provided.
 */
class ProjectViewModel(model: ProjectModel = ProjectModel()) : ItemViewModel<ProjectModel>() {

    init {
        item = model
    }

    val pages = bind(ProjectModel::pages)
    val dockedPages = bind(ProjectModel::dockedPages)

}