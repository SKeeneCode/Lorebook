package org.ksoftware.lorebook.organiser

import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import org.ksoftware.lorebook.events.TagTreeRebuildRequest
import org.ksoftware.lorebook.tagflow.TagFlowViewModel
import tornadofx.View
import tornadofx.onChange
import tornadofx.scrollpane
import tornadofx.vgrow

/**
 * The TagTree displays a tree of Tags wrapped in a scrollpane.
 */
class TagTree : View("My View") {

    private val tagOrganiserController: TagOrganiserController by inject()
    private val tagOrganiserViewModel: TagOrganiserViewModel by inject()
    private val tagFlowViewModel: TagFlowViewModel by inject()

    init {
        tagFlowViewModel.tagSet.onChange { set ->
            if (set == null) return@onChange
            tagOrganiserController.disabledCellIfInSet(set)
        }
    }

    override fun onDock() {
        fire(TagTreeRebuildRequest)
    }

    override val root = scrollpane {
        vgrow = Priority.ALWAYS
        isFitToWidth = true

        addEventFilter(DragEvent.DRAG_OVER) {
            if (it.dragboard.getContent(DataFormat.PLAIN_TEXT) == "dropFromTagFlow") {
                val draggedTag = tagFlowViewModel.draggedTag
                if (it.gestureSource != this && draggedTag != null && tagFlowViewModel.tagSet.value.contains(draggedTag)) {
                    it.acceptTransferModes(TransferMode.MOVE)
                }
                it.consume()
            }
        }

        addEventFilter(DragEvent.DRAG_DROPPED) { event ->
            if (event.dragboard.getContent(DataFormat.PLAIN_TEXT) == "dropFromTagFlow") {
                var result = false
                val draggedTag = tagFlowViewModel.draggedTag
                draggedTag?.let { tag ->
                    tagFlowViewModel.tagSet.value.remove(tag)
                    tag.disabled.value = false
                    result = true
                }
                event.isDropCompleted = result
                event.consume()
            }
        }

    }

    fun rebuildTree() {
        clearTreeCache()
        tagOrganiserController.build(root, tagOrganiserViewModel.root)
    }


    fun clearTreeCache() {
        val cache = tagOrganiserViewModel.treeCells
        cache.forEach { it.onDelete() }
        cache.clear()
    }

    fun filterTreeByName(name: String) {
        tagOrganiserController.filterItemsAndRebuild(name, root, tagOrganiserViewModel.root)
    }

}
