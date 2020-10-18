package org.ksoftware.lorebook.newproject

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

/**
 * This View-Model keeps track of the users desired settings when creating a new project.
 */
class NewProjectViewModel : ViewModel() {
    val newProjectName = SimpleStringProperty("myProject")
    val newProjectSaveDirectory = SimpleStringProperty("")
}