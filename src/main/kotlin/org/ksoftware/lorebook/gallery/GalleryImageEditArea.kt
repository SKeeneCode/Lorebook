package org.ksoftware.lorebook.gallery

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Parent
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import org.ksoftware.lorebook.controls.AutoCompleteTagField
import org.ksoftware.lorebook.controls.AutoCompleteTagFieldViewModal
import org.ksoftware.lorebook.main.ProjectWorkspaceController
import org.ksoftware.lorebook.organiser.TagOrganiser
import org.ksoftware.lorebook.organiser.TagOrganiserController
import org.ksoftware.lorebook.organiser.TagOrganiserViewModel
import org.ksoftware.lorebook.styles.Styles
import org.ksoftware.lorebook.tagflow.TagFlow
import org.ksoftware.lorebook.tagflow.TagFlowCell
import org.ksoftware.lorebook.tagflow.TagFlowCellCompact
import org.ksoftware.lorebook.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.tags.TagViewModel
import org.ksoftware.lorebook.utilities.forceSize
import org.ksoftware.lorebook.utilities.mfxtextfield
import tornadofx.*
import java.awt.Desktop
import java.io.File

class GalleryImageEditArea : View() {

    private val galleryViewModel: GalleryViewModel by inject()
    private val tagFieldViewModel: AutoCompleteTagFieldViewModal by inject()
    private val tagOrganiserController: TagOrganiserController by inject()
    private val projectController: ProjectWorkspaceController by inject()
    private val baseTagFlowViewModel: TagFlowViewModel by inject()
    private val tagFlowViewModel: TagFlowViewModel by inject(Scope())
    private val tagOrganiserViewModel: TagOrganiserViewModel by inject()
    private val tagOrganiser: TagOrganiser by inject()
    private val tagFlow: TagFlow by inject(Scope(tagFlowViewModel))

    override val root = stackpane {

        vbox {
            visibleWhen(galleryViewModel.showImageEditPrompt)
            alignment = Pos.CENTER
            text("Select an image to edit it.")
        }

        vbox(2) {
            hiddenWhen(galleryViewModel.showImageEditPrompt)
            prefWidth = 200.0
            paddingAll = 10.0
            alignment = Pos.TOP_CENTER



            style {
                backgroundColor += c("#eaf0ff")
            }

            stackpane {
                forceSize(160.0, 160.0)
                imageview(galleryViewModel.projectImageToEdit.image) {
                    isPreserveRatio = true
                    fitWidth = 160.0
                }
            }

            hbox {
                textfield(galleryViewModel.projectImageToEdit.title) {
                    hgrow = Priority.ALWAYS
                    prefHeightProperty().bind(this@hbox.heightProperty())
                }
                button {
                    prefHeightProperty().bind(this@hbox.heightProperty())
                    paddingHorizontal = 6
                    graphic = MaterialIconView(MaterialIcon.FOLDER).apply {
                        glyphSize = 28
                    }
                    onHover {
                        cursor = Cursor.HAND
                    }
                    action {
                        Runtime.getRuntime()
                            .exec("explorer /select, ${galleryViewModel.projectImageToEdit.fileURI.value}")
                    }
                }
            }

            textarea(galleryViewModel.projectImageToEdit.caption) {
                promptText = "Image caption..."
                prefHeight = 40.0
            }

            label("Image Tags")
            hbox {
                add(AutoCompleteTagField::class) {
                    this.root.hgrow = Priority.ALWAYS
                    this.root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                    root.prefHeightProperty().bind(this@hbox.heightProperty())
                }
                button {
                    prefHeightProperty().bind(this@hbox.heightProperty())
                    paddingHorizontal = 8
                    graphic = MaterialIconView(MaterialIcon.CHROME_READER_MODE).apply {
                        glyphSize = 28
                    }
                    onHover {
                        cursor = Cursor.HAND
                    }
                    action {
                        tagOrganiserViewModel.updateHeader(
                            MaterialIcon.IMAGE.name,
                            "Tag Organiser",
                            "Here you can add and remove tags for the image ${galleryViewModel.projectImageToEdit.title.value}."
                        )
                        projectController.openOverlayWith(tagOrganiser)
                        baseTagFlowViewModel.tagSet.value = null
                        baseTagFlowViewModel.tagSet.value = galleryViewModel.projectImageToEdit.imageTags
                    }
                }
            }

            scrollpane {
                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                tagFlow.root.prefWidthProperty().bind(widthProperty())
                prefHeight = 120.0
                add(tagFlow)
                tagFlowViewModel.tagToNodeConverter = { tag ->
                    find(TagFlowCellCompact::class, Scope(tagFlowViewModel, TagViewModel(tag))).root
                }
                tagFlowViewModel.showDropHint.value = false
                tagFlowViewModel.tagSet.value = galleryViewModel.projectImageToEdit.imageTags
            }

        }
    }
}