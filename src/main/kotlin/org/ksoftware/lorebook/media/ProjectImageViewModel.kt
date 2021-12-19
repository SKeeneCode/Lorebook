package org.ksoftware.lorebook.media

import tornadofx.ItemViewModel

class ProjectImageViewModel(model: ProjectImageModel = ProjectImageModel()) : ItemViewModel<ProjectImageModel>() {

    init {
        item = model
    }

    val id = bind(ProjectImageModel::idProperty)
    val title = bind(ProjectImageModel::titleProperty, autocommit = true)
    val caption = bind(ProjectImageModel::captionProperty, autocommit = true)
    val fileURI = bind(ProjectImageModel::fileURIProperty, autocommit = true)
    val image = bind(ProjectImageModel::imageProperty, autocommit = true)
    val imageTags = bind(ProjectImageModel::imageTagsProperty, autocommit = true)
}