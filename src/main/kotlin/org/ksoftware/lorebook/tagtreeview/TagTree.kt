package org.ksoftware.lorebook.tagtreeview

import javafx.scene.layout.Priority
import tornadofx.*

class TagTree : View("My View") {

    private val tagTreeController: TagTreeController by inject()
    private val tagTreeViewModel: TagTreeViewModel by inject()

    override val root = scrollpane {
        vgrow = Priority.ALWAYS
        isFitToWidth = true
    }

    fun rebuildTree() {
        val cache = tagTreeViewModel.treeCells
        cache.forEach { it.onDelete() }
        cache.clear()
        tagTreeController.build(root, tagTreeViewModel.root)
    }

    fun filterTreeByName(name: String) {
        tagTreeController.filterItemsAndRebuild(name, root, tagTreeViewModel.root)
    }

}
