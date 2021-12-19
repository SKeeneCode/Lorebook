package org.ksoftware.lorebook.tagflow

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.input.MouseButton
import javafx.scene.input.TransferMode
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.organiser.TagOrganiserViewModel
import org.ksoftware.lorebook.tags.TagViewModel
import org.ksoftware.lorebook.utilities.getContrastColor
import tornadofx.*

/**
 * The TagFlowCell is the appearance of a Tag inside a TagFlow. TagFlowCells may be edited by double-clicking, right
 * clicking and can be dragged and dropped.
 */
class TagFlowCell : View() {


    private val tagViewModel: TagViewModel by inject()
    private val projectViewModel: ProjectViewModel by inject()
    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val editing = SimpleBooleanProperty(false)
    private val item = tagViewModel.item


    override val root = hbox {
        alignment = Pos.CENTER
        styleProperty().bind(tagViewModel.color.objectBinding {
            "-fx-background-color: ${tagViewModel.color.value?.css};" +
                    "-fx-background-radius: 12.0"
        })
        spacing = 4.0
        paddingHorizontal = 16
        paddingVertical = 8
        prefHeight = 30.0

        contextmenu {
            item("Remove Tag") {
                graphic = MaterialIconView(MaterialIcon.CHEVRON_LEFT).apply {
                    glyphSize = 20
                    fill = Color.BLUEVIOLET
                }
                action {
                    tagFlowViewModel.tagSet.value.remove(item)
                    item.disabled.value = false
                }
            }
            item("Rename Tag") {
                graphic = MaterialIconView(MaterialIcon.EDIT).apply {
                    glyphSize = 18
                    fill = Color.FIREBRICK
                }
                action {
                    editing.value = true
                }
            }
            item("Change Color") {
                graphic = MaterialIconView(MaterialIcon.PALETTE).apply {
                    glyphSize = 18
                    fill = item.colorProperty.value
                }
                action {
                    //
                }
            }
            item("Delete Tag") {
                graphic = MaterialIconView(MaterialIcon.CLEAR).apply {
                    glyphSize = 18
                    fill = Color.RED
                }
            }
        }


        setOnMouseClicked { mouseEvent ->
            if (mouseEvent.button == MouseButton.PRIMARY) {
                if (mouseEvent.clickCount == 2) {
                        editing.value = true
                }
            }
        }

        textfield(tagViewModel.name) {
            prefWidth = tagViewModel.name.value.length.toDouble() * 8
            removeWhen(editing.not())
            whenVisible {
                requestFocus()
            }
            focusedProperty().onChange {
                if (!it) editing.value = false
            }
            action {
                editing.value = false
            }
        }

        label(item.nameProperty) {
            removeWhen(editing)
            textFillProperty().bind(tagViewModel.color.objectBinding {
                getContrastColor(it)
            })
            font = Font.font(12.0)
        }

        setOnDragDetected {
            val db = startDragAndDrop(TransferMode.MOVE)
            db.setContent {
                putImage(this@hbox.snapshot(null, null))
                putString("dropFromTagFlow")
            }
            tagFlowViewModel.draggedTag = tagViewModel.item
            it.consume()
        }
    }
}