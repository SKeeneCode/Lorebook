package org.ksoftware.lorebook.timeline

import javafx.beans.value.ObservableValue
import javafx.scene.layout.Priority
import org.ksoftware.lorebook.controls.SpinnerLongValueFactory
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.styles.Styles
import tornadofx.*

class CalendarWizardEditor : View() {

    private val calendarViewModal: CalendarViewModal by inject()

    private val projectViewModal: ProjectViewModel by inject()
    private val calendarWizardViewModal: CalendarWizardViewModal by inject()

    private val calendarWizardController: CalendarWizardController by inject()

    private val eraEditor = find(EraEditor::class)
    private val monthEditor = find(MonthEditor::class)
    private val dayEditor = find(DayEditor::class)
    private val calendarEditor = find(CalendarEditor::class)

    override val root = pane {
        addClass(Styles.parchmentBG, Styles.parchmentShadow)
        hgrow = Priority.ALWAYS
        pane {
            visibleWhen {
                calendarViewModal.itemProperty.isNull.and(projectViewModal.calendars.value.isEmpty())
            }
            imageview("images/calendarArrow.png", true)
            label("This Project doesn't have\n" +
                    "any calendars! Press the\n" +
                    "'+' to create one.") {
                prefWidth = 200.0
                isWrapText = true
                layoutX = 50.0
                layoutY = 100.0
            }
        }
        add(calendarEditor)
        calendarEditor.root.visibleWhen {
            calendarWizardViewModal.editorToShow.isEqualTo(Editor.CalendarEditor)
        }
        add(eraEditor)
        eraEditor.root.visibleWhen {
            calendarWizardViewModal.editorToShow.isEqualTo(Editor.EraEditor)
        }
        add(monthEditor)
        monthEditor.root.visibleWhen {
            calendarWizardViewModal.editorToShow.isEqualTo(Editor.MonthEditor)
        }
        add(dayEditor)
        dayEditor.root.visibleWhen {
            calendarWizardViewModal.editorToShow.isEqualTo(Editor.DayEditor)
        }

    }
}

class CalendarEditor : View() {
    private val viewModal: CalendarViewModal by inject()
    override val root = form {
        fieldset {
            field("Name") {
                textfield(viewModal.name) { }
            }
        }
    }
}

class EraEditor : View() {
    private val viewModal: EraViewModal by inject()
    override val root = form {
        fieldset {
            wrapWidth = 280.0
            field("Era name:") {
                textfield(viewModal.name)
            }
            field("Start year:") {
                spinner<Long> {
                    valueFactory = SpinnerLongValueFactory()
                    isEditable = true
                    editor.filterInput { change ->
                        change.controlNewText.isLong() &&
                        change.controlNewText.toLong() in 0..Long.MAX_VALUE
                    }
                    bind(viewModal.startYear as ObservableValue<Long>)
                }
            }
            field("Era length:") {
                spinner<Long> {
                    valueFactory = SpinnerLongValueFactory()
                    isEditable = true
                    editor.filterInput { change ->
                        change.controlNewText.isLong() &&
                        change.controlNewText.toLong() in 0..Long.MAX_VALUE
                    }
                    bind(viewModal.durationInYears as ObservableValue<Long>)
                }
                label("years.")
            }
            field("End year:") {
                val labelBinding = stringBinding(viewModal.durationInYears, viewModal.startYear) {
                    (viewModal.durationInYears.value + viewModal.startYear.value).toString()
                }
                label(labelBinding)
            }
            field("Has leap years:") {
                checkbox(property = viewModal.hasLeapYears)
            }
        }
    }
}

class MonthEditor : View() {
    private val viewModal: MonthViewModal by inject()
    override val root = form {
        fieldset {
            wrapWidth = 280.0
            field("Month name:") {
                textfield(viewModal.name)
            }
            field("Month short name:") {
                textfield(viewModal.shortName)
            }
            field("Month length:") {
                spinner(
                    0,
                    Int.MAX_VALUE,
                    30,
                    1,
                    true,
                    viewModal.durationInDays,
                    true) {
                    editor.filterInput { change ->
                        change.controlNewText.isInt() &&
                                change.controlNewText.toInt() in 0..Int.MAX_VALUE
                    }
                }
                label("days.")
            }
            field("Month length during leap year:") {
                spinner(
                    0,
                    Int.MAX_VALUE,
                    30,
                    1,
                    true,
                    viewModal.durationInDaysDuringLeapYear,
                    true) {
                    editor.filterInput { change ->
                        change.controlNewText.isInt() &&
                                change.controlNewText.toInt() in 0..Int.MAX_VALUE
                    }
                }
                label("days.")
            }
        }
    }
}

class DayEditor : View() {
    private val viewModal: DayViewModal by inject()
    override val root = form {
        fieldset {
            wrapWidth = 280.0
            field("Day name") {
                textfield(viewModal.name) { }
            }
            field("Day short name:") {
                textfield(viewModal.shortName)
            }
            field("Day length:") {
                spinner(
                    0,
                    Int.MAX_VALUE,
                    24,
                    1,
                    true,
                    viewModal.durationInHours,
                    true) {
                    editor.filterInput { change ->
                        change.controlNewText.isInt() &&
                                change.controlNewText.toInt() in 0..Int.MAX_VALUE
                    }
                }
                label("hours.")
            }
        }
    }
}

enum class Editor {
    CalendarEditor,
    EraEditor,
    MonthEditor,
    DayEditor
}