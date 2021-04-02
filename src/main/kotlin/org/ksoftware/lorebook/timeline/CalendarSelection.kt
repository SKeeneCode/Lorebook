package org.ksoftware.lorebook.timeline

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import javafx.scene.text.FontWeight
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.styles.Styles
import tornadofx.*

class CalendarSelection : View() {

    private val eraViewModal: EraViewModal by inject()
    private val monthViewModal: MonthViewModal by inject()
    private val dayViewModal: DayViewModal by inject()
    private val calendarViewModal: CalendarViewModal by inject()

    private val projectViewModal: ProjectViewModel by inject()
    private val calendarWizardViewModal: CalendarWizardViewModal by inject()

    private val calendarWizardController: CalendarWizardController by inject()

    override val root = squeezebox(multiselect = false) {
        addClass(Styles.baseBlueBG)
        fillHeight = false
        prefWidth = 130.0
        fold("Calendars") {
            addClass(Styles.blueTitledPane)
            contentDisplay = ContentDisplay.RIGHT
            graphic = Button().apply {
                visibleWhen(this@fold.expandedProperty())
                paddingAll = 0.0
                background = null
                graphic = MaterialIconView(MaterialIcon.ADD).apply {
                    fill = Styles.darkGreen
                    glyphSize = 20.0
                }
                action {
                    val newCalendar = CalendarModal()
                    projectViewModal.calendars.value.add(newCalendar)
                    calendarViewModal.rebind { item = newCalendar }
                    calendarWizardController.showEditor(Editor.CalendarEditor)
                    eraViewModal.item = null
                    monthViewModal.item = null
                    dayViewModal.item = null
                }
            }
            isExpanded = true
            isCollapsible = false
            scrollpane {
                addClass(Styles.blueScrollbar)
                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
                maxHeight = 340.0
                vbox {
                    paddingAll = 0
                    heightProperty().onChange {
                        vbarPolicy = if (it >= 340) {
                            ScrollPane.ScrollBarPolicy.ALWAYS
                        } else {
                            ScrollPane.ScrollBarPolicy.NEVER
                        }
                    }
                    bindChildren(projectViewModal.calendars) { calendarModal ->
                        val node = HBox(4.0)
                        node.addClass(Styles.calaendarCell)
                        node.paddingAll = 4.0
                        val icon = MaterialIconView(MaterialIcon.PUBLIC).apply {
                            fill = Styles.sepia
                            glyphSize = 16.0
                        }
                        val label = Label().apply {
                            bind(calendarModal.nameProperty)
                            prefWidth = 64.0
                            calendarViewModal.itemProperty.onChange {
                                if (it == calendarModal) {
                                    style {
                                        fontWeight = FontWeight.BOLD
                                    }
                                } else {
                                    style {
                                        fontWeight = FontWeight.NORMAL
                                    }
                                }
                            }
                        }
                        val arrow = MaterialIconView(MaterialIcon.KEYBOARD_ARROW_RIGHT).apply {
                            fill = Styles.jet
                            glyphSize = 16.0
                            visibleWhen {
                                calendarViewModal.itemProperty.isEqualTo(calendarModal)
                            }
                        }
                        node.children.addAll(icon, label, arrow)
                        node.onLeftClick {
                            calendarViewModal.rebind { item = calendarModal }
                            calendarWizardController.showEditor(Editor.CalendarEditor)
                            eraViewModal.item = null
                            monthViewModal.item = null
                            dayViewModal.item = null
                        }
                        node
                    }
                }
            }
        }
    }
}