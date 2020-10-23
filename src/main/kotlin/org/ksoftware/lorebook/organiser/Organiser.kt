package org.ksoftware.lorebook.organiser

import org.ksoftware.lorebook.events.TagTreeRebuildRequest
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.tagflow.TagFlow
import org.ksoftware.lorebook.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.tags.TagFunction
import org.ksoftware.lorebook.tagtreeview.TagTree
import org.ksoftware.lorebook.tagtreeview.TagTreeViewModel
import tornadofx.*

class Organiser : View("Page Organiser") {

    private val pageViewModel: PageViewModel by inject()
    private val projectViewModel: ProjectViewModel by inject()
    private val tagTreeViewModel: TagTreeViewModel by inject()
    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val tagTree: TagTree by inject()

    init {
        tagTreeViewModel.root = projectViewModel.rootTag
        tagTreeViewModel.deleteFunction = TagFunction { false }
        tagTreeViewModel.addFunction = TagFunction { pageViewModel.tags.value.add(it) }
        tagFlowViewModel.deleteFunction = TagFunction { pageViewModel.tags.value.remove(it) }
    }

    override fun onDock() {
        subscribe<TagTreeRebuildRequest> {
            tagTree.rebuildTree()
        }
        tagTree.rebuildTree()
    }

    override val root = splitpane {
        prefWidth = 600.0
        prefHeight = 600.0
        vbox {
            textfield {
                promptText = "search..."
                textProperty().onChange {
                    tagTree.filterTreeByName(it ?: "")
                }
            }
            add(tagTree)
        }
        add<TagFlow>()
    }

}