package org.ksoftware.lorebook.gallery

import com.sksamuel.scrimage.ImmutableImage
import javafx.beans.property.SimpleStringProperty
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Pos
import javafx.scene.image.WritableImage
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.media.ProjectImageModel
import org.ksoftware.lorebook.media.ProjectImageViewModel
import org.ksoftware.lorebook.styles.Styles
import org.ksoftware.lorebook.utilities.ImageProperty
import tornadofx.*
import java.io.File

class GalleryDropArea : View() {

    private val projectViewModel: ProjectViewModel by inject()
    private val galleryViewModel: GalleryViewModel by inject()
    private val galleryController: GalleryController by inject()
    private val acceptedFiles = mutableListOf<File>()

    init {
        galleryController.processedImages.onChange {
            galleryViewModel.dropLabelPrompt.value = "Processed $it images"
        }
    }

    override val root = stackpane {

        prefWidth = 600.0
        prefHeight = 100.0
        alignment = Pos.CENTER


        addClass(Styles.baseBlueBG)

        stackpane {
            paddingAll = 16.0
            vbox {
                alignment = Pos.CENTER

                style {
                    borderColor += box(Color.GRAY)
                    borderWidth = multi(box(2.px))
                    borderRadius = multi(box(8.px))
                    borderStyle = multi(BorderStrokeStyle.DASHED)
                }
                text("Drag images here to load them into your project. \n Accepted filetypes: png, jpg, jpeg") {
                    textAlignment = TextAlignment.CENTER
                }
                text(galleryViewModel.dropLabelPrompt) {
                    textAlignment = TextAlignment.CENTER
                }
            }
        }



        setOnDragOver { event ->
            if (event.dragboard.hasFiles()) {
                acceptedFiles.clear()
                val draggedFiles = event.dragboard.files
                val acceptedFormats = listOf( "png", "jpg", "jpeg" )
                for (file in draggedFiles) {
                    if (file.extension in acceptedFormats) {
                        acceptedFiles += file
                    }
                }

                if (acceptedFiles.isNotEmpty()) {
                    galleryViewModel.dropLabelPrompt.value = "Release to process ${acceptedFiles.size} images."
                    event.acceptTransferModes(TransferMode.MOVE)
                }

                style {
                    backgroundColor += Styles.headerDarkBlue
                }
            }
            event.consume()
        }

        setOnDragDropped { event ->
            for (file in acceptedFiles) {
                galleryController.processImage(file)
            }
            acceptedFiles.clear()
            event.consume()
        }

    }

}