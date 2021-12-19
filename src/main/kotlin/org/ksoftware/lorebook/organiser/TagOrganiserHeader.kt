package org.ksoftware.lorebook.organiser

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.paint.Color
import org.ksoftware.lorebook.styles.Styles
import tornadofx.*

class TagOrganiserHeader : View() {

    private val tagOrganiserViewModel: TagOrganiserViewModel by inject()

    override val root = hbox(6) {
        paddingHorizontal = 8.0
        alignment = Pos.CENTER_LEFT
        setPrefSize(600.0, 80.0)
        addClass(Styles.headerBlueBG)
        MaterialIconView(MaterialIcon.IMAGE).apply {
            glyphNameProperty().bind(tagOrganiserViewModel.headerGlyphProperty)
            glyphSize = 50
            fill = Color.DARKBLUE
            this@hbox.add(this)
        }
        vbox {
            alignment = Pos.CENTER_LEFT
            label(tagOrganiserViewModel.headerTextProperty) {
                addClass(Styles.headerFont)
            }
            label(tagOrganiserViewModel.subHeaderTextProperty)
        }
    }

}