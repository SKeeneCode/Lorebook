package org.ksoftware.lorebook.settings

import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

class ProjectSettingsModel {

    private val allowRightDrawerOpenProperty = SimpleBooleanProperty(true)
    var allowRightDrawerOpen by allowRightDrawerOpenProperty

}