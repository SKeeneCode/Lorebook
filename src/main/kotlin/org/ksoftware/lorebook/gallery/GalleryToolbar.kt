package org.ksoftware.lorebook.gallery

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Cursor
import javafx.scene.Parent
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.navigator.BookmarkTreeNode
import tornadofx.*
import java.util.*
import java.util.function.Predicate

class GalleryToolbar : View() {

    private val projectViewModel: ProjectViewModel by inject()
    private val galleryViewModel: GalleryViewModel by inject()
    private val galleryController: GalleryController by inject()

    override val root = hbox {
        prefHeight = 28.0
        button {
            paddingHorizontal = 8
            graphic = MaterialIconView(MaterialIcon.UNFOLD_MORE).apply {
                val binding = stringBinding(galleryViewModel.compactMode) {
                    if (this.value) {
                        MaterialIcon.UNFOLD_MORE.name
                    } else {
                        MaterialIcon.UNFOLD_LESS.name
                    }
                }
                glyphNameProperty().bind(binding)
                glyphSize = 24
            }
            tooltip {
                text = "Toggle compact mode for the image list."
            }
            onHover {
                cursor = Cursor.HAND
            }
            action {
                galleryViewModel.compactMode.value = !galleryViewModel.compactMode.value
            }
        }

        button {
            paddingHorizontal = 8
            graphic = MaterialIconView(MaterialIcon.CREATE_NEW_FOLDER).apply {
                glyphSize = 24
            }
            action {
                val galleryFolder = GalleryItem()
                projectViewModel.galleryRoot.value.addChild(galleryFolder, 1)
            }
            onHover {
                cursor = Cursor.HAND
            }
        }

        textfield {
            prefHeightProperty().bind(this@hbox.heightProperty())
            promptText = "search images..."
            galleryViewModel.searchString.bind(textProperty())
        }


        combobox<ImageSearchMethod> {
            prefHeightProperty().bind(this@hbox.heightProperty())
            items = ImageSearchMethod.values().toList().toObservable()
            cellFormat {
                text = "by ${it.name.lowercase(Locale.getDefault())}"
            }
            value = items.first()
            galleryViewModel.searchMethod.bind(valueProperty())
        }
    }


}

enum class ImageSearchMethod {
    TITLE,
    TAGS
}