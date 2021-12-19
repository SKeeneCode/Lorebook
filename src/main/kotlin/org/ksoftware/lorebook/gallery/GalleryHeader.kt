package org.ksoftware.lorebook.gallery

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.paint.Color
import org.ksoftware.lorebook.styles.Styles
import tornadofx.*

class GalleryHeader : View() {

    override val root = hbox(6) {
        paddingHorizontal = 8.0
        alignment = Pos.CENTER_LEFT
        setPrefSize(800.0, 100.0)
        addClass(Styles.headerBlueBG)
        MaterialIconView(MaterialIcon.PHOTO_LIBRARY).apply {
            glyphSize = 50
            fill = Color.DARKBLUE
            this@hbox.add(this)
        }
        vbox {
            alignment = Pos.CENTER_LEFT
            label("Gallery") {
                addClass(Styles.headerFont)
            }
            label("Here you can upload and manage your project images.")
        }
    }

}