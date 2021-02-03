package org.ksoftware.lorebook.tagflow

import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import org.ksoftware.lorebook.events.TagTreeRebuildRequest
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.tagtreeview.TagTreeViewModel
import tornadofx.*

class TagFlow : View("My View") {

    private val tagFlowController: TagFlowController by inject()
    private val pageViewModel: PageViewModel by inject()
    private val projectViewModel: ProjectViewModel by inject()
    private val tagTreeViewModel: TagTreeViewModel by inject()

    override val root = vbox {
        prefWidth = 300.0
        flowpane {
            hgap = 5.0
            vgap = 5.0
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
            bindChildren(pageViewModel.tags.value, tagFlowController.tagToNodeConvertor)


            setOnDragOver {
                val draggedTag = tagTreeViewModel.draggedTag
                if (it.gestureSource != this && draggedTag != null && !pageViewModel.tags.value.contains(draggedTag)) {
                    it.acceptTransferModes(TransferMode.MOVE)
                }
                it.consume()
            }

            setOnDragDropped {
                var result = false
                val draggedTag = tagTreeViewModel.draggedTag
                draggedTag?.let {
                    tagTreeViewModel.addFunction.operate(it)
                    result = true
                }
                it.isDropCompleted = result
                it.consume()
            }

            setOnDragDone {
                if (it.isAccepted) fire(TagTreeRebuildRequest)
                it.consume()
            }
        }
    }
}
