package org.ksoftware.lorebook.timeline

import org.ksoftware.lorebook.main.ProjectViewModel
import tornadofx.Controller

class CalendarWizardController : Controller() {

    private val projectViewModal: ProjectViewModel by inject()
    private val calendarWizardViewModal: CalendarWizardViewModal by inject()

    fun showEditor(editor: Editor?) {
        calendarWizardViewModal.editorToShow.value = editor
    }

}