package org.ksoftware.lorebook.organiser

import javafx.scene.layout.Region
import org.ksoftware.lorebook.events.TagTreeRebuildRequest
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.organiser.tagflow.TagFlow
import org.ksoftware.lorebook.organiser.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.organiser.tagtreeview.TagTree
import org.ksoftware.lorebook.organiser.tagtreeview.TagTreeViewModel
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.tags.TagFunction
import tornadofx.*

/**
 * The organiser allows the user to create and edit tags, as well as change a pages tags.
 */
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
        minWidth = Region.USE_PREF_SIZE
        prefWidth = 600.0
        maxWidth = Region.USE_PREF_SIZE
        minHeight = Region.USE_PREF_SIZE
        prefHeight = 600.0
        maxHeight = Region.USE_PREF_SIZE
        vbox {
            textfield {
                promptText = "search..."
                textProperty().onChange {
                    tagTree.clearTreeCache()
                    tagTree.filterTreeByName(it ?: "")
                }
            }
            add(tagTree)
        }
        scrollpane(fitToWidth = true, fitToHeight = true) {
            add<TagFlow>()
        }
    }
}