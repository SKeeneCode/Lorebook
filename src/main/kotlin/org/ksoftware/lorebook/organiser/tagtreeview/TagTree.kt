package org.ksoftware.lorebook.organiser.tagtreeview

import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import org.ksoftware.lorebook.organiser.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.pages.PageViewModel
import tornadofx.View
import tornadofx.scrollpane
import tornadofx.vgrow

class TagTree : View("My View") {

    private val tagTreeController: TagTreeController by inject()
    private val tagTreeViewModel: TagTreeViewModel by inject()
    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val pageViewModel: PageViewModel by inject()

    override val root = scrollpane {
        vgrow = Priority.ALWAYS
        isFitToWidth = true

        addEventFilter(DragEvent.DRAG_OVER) {
            if (it.dragboard.getContent(DataFormat.PLAIN_TEXT) == "dropFromTagFlow") {
                val draggedTag = tagFlowViewModel.draggedTag
                if (it.gestureSource != this && draggedTag != null && pageViewModel.tags.value.contains(draggedTag)) {
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
                    tagFlowViewModel.deleteFunction.operate(tag)
                    result = true
                }
                event.isDropCompleted = result
                event.consume()
            }
        }

    }

    fun rebuildTree() {
        clearTreeCache()
        tagTreeController.build(root, tagTreeViewModel.root)
    }

    fun clearTreeCache() {
        val cache = tagTreeViewModel.treeCells
        cache.forEach { it.onDelete() }
        cache.clear()
    }

    fun filterTreeByName(name: String) {
        tagTreeController.filterItemsAndRebuild(name, root, tagTreeViewModel.root)
    }

}
