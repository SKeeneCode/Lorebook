package org.ksoftware.lorebook.organiser

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.input.DataFormat
import javafx.scene.input.MouseButton
import javafx.scene.input.TransferMode
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.ksoftware.lorebook.controls.PopupController
import org.ksoftware.lorebook.events.TagTreeRebuildRequest
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.styles.Styles
import org.ksoftware.lorebook.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.tags.TagViewModel
import org.ksoftware.lorebook.utilities.getContrastColor
import tornadofx.*

/**
 * A TagTreeCell is the view for a TagModal being displayed in a TagTree.
 */
class TagTreeCell : View() {

    private val tagViewModel: TagViewModel by inject()
    private val tagOrganiserViewModel: TagOrganiserViewModel by inject()
    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val projectViewModel: ProjectViewModel by inject()
    private val popupController: PopupController by inject(FX.defaultScope)

    private val item = tagViewModel.item
    private val enabled = SimpleBooleanProperty(true)
    private val editing = SimpleBooleanProperty(false)


    override val root = hbox {
        alignment = Pos.CENTER
        styleProperty().bind(tagViewModel.color.objectBinding {
            "-fx-background-color: ${tagViewModel.color.value?.css};" +
                    "-fx-background-radius: 12.0"
        })
        toggleClass(Styles.disabledCell, item.disabled)
        spacing = 2.0
        paddingVertical = 12
        paddingHorizontal = if (tagViewModel.item.children.isNotEmpty()) 100 else 16


        textfield(tagViewModel.name) {
            prefWidth = 50.0
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

        setOnMouseClicked { mouseEvent ->
            if (mouseEvent.button == MouseButton.PRIMARY) {
                if (mouseEvent.clickCount == 2) {
                    editing.value = true
                }
            }
        }

           contextmenu {
               item("Add Tag") {
                   graphic = MaterialIconView(MaterialIcon.CHEVRON_RIGHT).apply {
                       glyphSize = 20
                       fill = Color.BLUEVIOLET
                   }
                   action {
                       println("adding to $tagFlowViewModel")
                       tagFlowViewModel.tagSet.value.add(item)
                       item.disabled.value = true
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

        setOnDragDetected {
            if (item.disabled.value) return@setOnDragDetected
            val db = startDragAndDrop(TransferMode.MOVE)
            db.setContent { putImage(this@hbox.snapshot(null, null)) }
            tagFlowViewModel.draggedTag = tagViewModel.item
            it.consume()
        }

        setOnDragOver {
            val draggedTag = tagFlowViewModel.draggedTag
            if (it.gestureSource != this && draggedTag != null && !tagViewModel.item.anyParentsAre(draggedTag)) {
                it.acceptTransferModes(TransferMode.MOVE)
            }
            it.consume()
        }

        setOnDragDropped { event ->
            var result = false
            val draggedTag = tagFlowViewModel.draggedTag
            draggedTag?.let {
                tagViewModel.item.addChild(draggedTag)
                draggedTag.disabled.value = false
                event.dragboard.setContent { putString("requiresRebuild") }
                result = true
            }
            event.isDropCompleted = result
            event.consume()
        }

        setOnDragDone {
            if (it.isAccepted && it.dragboard.getContent(DataFormat.PLAIN_TEXT) == "requiresRebuild") fire(TagTreeRebuildRequest)
            it.consume()
        }
    }

}

