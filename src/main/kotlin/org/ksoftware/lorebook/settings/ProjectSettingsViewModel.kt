package org.ksoftware.lorebook.settings

import tornadofx.ItemViewModel

class ProjectSettingsViewModel(model: ProjectSettingsModel = ProjectSettingsModel()) : ItemViewModel<ProjectSettingsModel>() {

    init {
        item = model
    }

    val allowRightDrawerOpen = bind(ProjectSettingsModel::allowRightDrawerOpen, autocommit = true)

}