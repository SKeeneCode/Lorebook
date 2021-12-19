package org.ksoftware.lorebook.tagflow

import javafx.geometry.Pos
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.ksoftware.lorebook.events.TagTreeRebuildRequest
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.organiser.TagOrganiserController
import org.ksoftware.lorebook.organiser.TagOrganiserViewModel
import tornadofx.*

/**
 * The TagFlow lays out a set of tags in a Flow layout. It allows Tags to be dragged and dropped into it.
 */
class TagFlow : View("My View") {

    private val tagFlowViewModel: TagFlowViewModel by inject()

    override val root = stackpane {
        vbox {
            button("Remove All") {
                paddingVertical = 6.0
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                tagFlowViewModel.tagSet.onChange { list ->
                    if (list == null) return@onChange
                    val binding = booleanBinding(list) { list.isEmpty() }
                    removeWhen(binding)
                }
                action {
                    tagFlowViewModel.tagSet.value.forEach { it.disabled.value = false }
                    tagFlowViewModel.tagSet.value.clear()
                }
            }
            flowpane {
                paddingAll = 10.0
                hgap = 5.0
                vgap = 5.0
                vgrow = Priority.ALWAYS
                hgrow = Priority.ALWAYS

                tagFlowViewModel.tagSet.onChange { list ->
                    if (list == null) return@onChange
                    bindChildren(list, tagFlowViewModel.tagToNodeConverter)
                }


                setOnDragOver {
                    val draggedTag = tagFlowViewModel.draggedTag
                    if (it.gestureSource != this && draggedTag != null && !tagFlowViewModel.tagSet.value.contains(
                            draggedTag
                        )
                    ) {
                        it.acceptTransferModes(TransferMode.MOVE)
                    }
                    it.consume()
                }

                setOnDragDropped { event ->
                    var result = false
                    val draggedTag = tagFlowViewModel.draggedTag
                    draggedTag?.let {
                        tagFlowViewModel.tagSet.value.add(it)
                        it.disabled.value = true
                        result = true
                    }
                    event.isDropCompleted = result
                    event.consume()
                }

                setOnDragDone {
                    if (it.isAccepted) fire(TagTreeRebuildRequest)
                    it.consume()
                }
            }
        }
        vbox {
            visibleWhen(tagFlowViewModel.showDropHint)
            alignment = Pos.CENTER
            paddingAll = 20.0
            isMouseTransparent = true
            vbox {
                vgrow = Priority.ALWAYS
                alignment = Pos.CENTER
                tagFlowViewModel.tagSet.onChange { list ->
                    if (list == null) return@onChange
                    val binding = booleanBinding(list) { list.isNotEmpty() }
                    removeWhen(binding)
                }
                style {
                    borderColor += box(Color.GRAY)
                    borderWidth = multi(box(4.px))
                    borderRadius = multi(box(12.px))
                    borderStyle = multi(BorderStrokeStyle.DASHED)
                    padding = box(20.px)
                }
                label("Drag and drop tags here.")
            }
        }
    }
}
