package org.ksoftware.lorebook.timeline

import javafx.beans.property.SimpleObjectProperty
import tornadofx.ViewModel

class CalendarWizardViewModal : ViewModel() {

    val editorToShow = SimpleObjectProperty<Editor>(null)

}