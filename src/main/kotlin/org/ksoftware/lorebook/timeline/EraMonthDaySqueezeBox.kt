package org.ksoftware.lorebook.timeline

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import javafx.scene.text.FontWeight
import org.ksoftware.lorebook.styles.Styles
import tornadofx.*

class EraMonthDaySqueezeBox : View() {

    private val eraViewModal: EraViewModal by inject()
    private val monthViewModal: MonthViewModal by inject()
    private val dayViewModal: DayViewModal by inject()
    private val calendarViewModal: CalendarViewModal by inject()

    private val calendarWizardViewModal: CalendarWizardViewModal by inject()

    private val calendarWizardController: CalendarWizardController by inject()

    override val root = squeezebox(multiselect = false) {
        addClass(Styles.baseBlueBG)
        fillHeight = false
        prefWidth = 130.0
        removeWhen(calendarViewModal.itemProperty.isNull)
        fold("Eras") {
            addClass(Styles.blueTitledPane)
            isExpanded = false
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
                    val newEra = EraModal()
                    calendarViewModal.eras.add(newEra)
                    eraViewModal.rebind { item = newEra }
                    monthViewModal.item = null
                    dayViewModal.item = null
                    calendarWizardController.showEditor(Editor.EraEditor)
                }
            }
            expandedProperty().onChange { expanded ->
                if (expanded) {
                    eraViewModal.rebind {
                        item = calendarViewModal.eras.firstOrNull()
                        if (item != null) {
                            calendarWizardController.showEditor(Editor.EraEditor)
                        } else {
                            calendarWizardController.showEditor(Editor.CalendarEditor)
                        }
                    }
                } else {
                    calendarWizardController.showEditor(Editor.CalendarEditor)
                }
            }
            scrollpane {
                addClass(Styles.blueScrollbar)
                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
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
                    bindChildren(calendarViewModal.eras) { eraModal ->
                        val node = HBox(4.0)
                        node.addClass(Styles.calaendarCell)
                        node.paddingAll = 4.0
                        val icon = MaterialIconView(MaterialIcon.TIMELINE).apply {
                            fill = Styles.sepia
                            glyphSize = 16.0
                        }
                        val label = Label().apply {
                            bind(eraModal.nameProperty)
                            prefWidth = 64.0
                            eraViewModal.itemProperty.onChange {
                                if (it == eraModal) {
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
                                eraViewModal.itemProperty.isEqualTo(eraModal)
                            }
                        }
                        node.children.addAll(icon, label, arrow)
                        node.onLeftClick {
                            eraViewModal.rebind { item = eraModal }
                            monthViewModal.item = null
                            dayViewModal.item = null
                            calendarWizardController.showEditor(Editor.EraEditor)
                        }
                        node
                    }
                }
            }
        }
        fold("Months") {
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
                    val newMonth = MonthModal()
                    calendarViewModal.months.add(newMonth)
                    monthViewModal.rebind { item = newMonth }
                    eraViewModal.item = null
                    dayViewModal.item = null
                    calendarWizardController.showEditor(Editor.MonthEditor)
                }
            }
            expandedProperty().onChange { expanded ->
                if (expanded) {
                    monthViewModal.rebind {
                        item = calendarViewModal.months.firstOrNull()
                        if (item != null) {
                            calendarWizardController.showEditor(Editor.MonthEditor)
                        } else {
                            calendarWizardController.showEditor(Editor.CalendarEditor)
                        }
                    }
                } else {
                    calendarWizardController.showEditor(Editor.CalendarEditor)
                }
            }
            isExpanded = false
            scrollpane {
                addClass(Styles.blueScrollbar)
                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
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
                    bindChildren(calendarViewModal.months) { monthModal ->
                        val node = HBox(4.0)
                        node.addClass(Styles.calaendarCell)
                        node.paddingAll = 4.0
                        val icon = MaterialIconView(MaterialIcon.TODAY).apply {
                            fill = Styles.sepia
                            glyphSize = 16.0
                        }
                        val label = Label().apply {
                            bind(monthModal.nameProperty)
                            prefWidth = 64.0
                            monthViewModal.itemProperty.onChange {
                                if (it == monthModal) {
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
                                monthViewModal.itemProperty.isEqualTo(monthModal)
                            }
                        }
                        node.children.addAll(icon, label, arrow)
                        node.onLeftClick {
                            eraViewModal.item = null
                            monthViewModal.rebind { item = monthModal }
                            dayViewModal.item = null
                            calendarWizardController.showEditor(Editor.MonthEditor)
                        }
                        node
                    }
                }
            }
        }
        fold("Weekdays") {
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
                    val newDay = DayModal()
                    calendarViewModal.days.add(newDay)
                    eraViewModal.item = null
                    monthViewModal.item = null
                    dayViewModal.rebind { item = newDay }
                    calendarWizardController.showEditor(Editor.DayEditor)
                }
            }
            expandedProperty().onChange { expanded ->
                if (expanded) {
                    dayViewModal.rebind {
                        item = calendarViewModal.days.firstOrNull()
                        if (item != null) {
                            calendarWizardController.showEditor(Editor.DayEditor)
                        } else {
                            calendarWizardController.showEditor(Editor.CalendarEditor)
                        }
                    }
                } else {
                    calendarWizardController.showEditor(Editor.CalendarEditor)
                }
            }
            isExpanded = false
            scrollpane {
                addClass(Styles.blueScrollbar)
                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                maxHeight = 340.0
                vbox {
                    heightProperty().onChange {
                        vbarPolicy = if (it >= 340) {
                            ScrollPane.ScrollBarPolicy.ALWAYS
                        } else {
                            ScrollPane.ScrollBarPolicy.NEVER
                        }
                    }
                    paddingAll = 0
                    bindChildren(calendarViewModal.days) { dayModal ->
                        val node = HBox(4.0)
                        node.addClass(Styles.calaendarCell)
                        node.paddingAll = 4.0
                        val icon = MaterialIconView(MaterialIcon.SCHEDULE).apply {
                            fill = Styles.sepia
                            glyphSize = 16.0
                        }
                        val label = Label().apply {
                            bind(dayModal.nameProperty)
                            prefWidth = 64.0
                            dayViewModal.itemProperty.onChange {
                                if (it == dayModal) {
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
                                dayViewModal.itemProperty.isEqualTo(dayModal)
                            }
                        }
                        node.children.addAll(icon, label, arrow)
                        node.onLeftClick {
                            eraViewModal.item = null
                            monthViewModal.item = null
                            dayViewModal.rebind { item = dayModal }
                            calendarWizardController.showEditor(Editor.DayEditor)
                        }
                        node
                    }
                }
            }
        }
    }

}