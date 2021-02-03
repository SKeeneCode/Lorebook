package org.ksoftware.lorebook.tagtreeview

import javafx.event.EventType
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.tagflow.TagFlowViewModel
import tornadofx.*

class TagTree : View("My View") {

    private val tagTreeController: TagTreeController by inject()
    private val tagTreeViewModel: TagTreeViewModel by inject()
    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val pageViewModel: PageViewModel by inject()

    override val root = scrollpane {
        vgrow = Priority.ALWAYS
        isFitToWidth = true

        setOnDragOver {
            val draggedTag = tagFlowViewModel.draggedTag
            if (it.gestureSource != this && draggedTag != null && pageViewModel.tags.value.contains(draggedTag)) {
                it.acceptTransferModes(TransferMode.MOVE)
            }
            it.consume()
        }

        setOnDragDropped { event ->
            var result = false
            val draggedTag = tagFlowViewModel.draggedTag
            draggedTag?.let { tag ->
                tagFlowViewModel.deleteFunction.operate(tag)
                result = true
            }
            event.isDropCompleted = result
            event.consume()
        }

        setOnDragDone {
            it.consume()
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
