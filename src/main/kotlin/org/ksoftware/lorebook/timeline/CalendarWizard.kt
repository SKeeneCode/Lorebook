package org.ksoftware.lorebook.timeline

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import org.ksoftware.lorebook.styles.Styles
import org.ksoftware.lorebook.utilities.forceSize
import tornadofx.*


class CalendarWizard : View("Create a new Calendar") {

    override val root = vbox {
        maxWidth = 500.0
        maxHeight = 480.0
        addClass(Styles.headerBluePadding, Styles.baseShadow)
        stackpane {
            addClass(Styles.headerBlueBG)
            minHeight = 48.0
            prefHeight = 48.0
            alignment = Pos.CENTER_LEFT
            paddingLeft = 18.0
            label("Calendar Manager") {
                addClass(Styles.headerFont)
            }
        }
        hbox {
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
            add<CalendarSelection>()
            add<EraMonthDaySqueezeBox>()
            add<CalendarWizardEditor>()
        }
    }

}
