package org.ksoftware.lorebook.organiser

import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import org.ksoftware.lorebook.events.TagTreeRebuildRequest
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.styles.Styles
import org.ksoftware.lorebook.tagflow.TagFlow
import org.ksoftware.lorebook.tagflow.TagFlowViewModel
import org.ksoftware.lorebook.tags.TagFunction
import tornadofx.*

/**
 * The organiser allows the user to create and edit tags, as well as change a pages tags.
 */
class TagOrganiser : View("Page Organiser") {

    private val projectViewModel: ProjectViewModel by inject()
    private val tagOrganiserViewModel: TagOrganiserViewModel by inject()
    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val tagTree: TagTree by inject()
    private var initialized = false

    init {
        tagOrganiserViewModel.root = projectViewModel.rootTag

        println("tag organiser is $tagFlowViewModel")
    }

    override fun onDock() {
        subscribe<TagTreeRebuildRequest> {
            tagTree.rebuildTree()
        }
        if (!initialized) {
            tagTree.rebuildTree()
            initialized = true
        }
    }

    override fun onUndock() {
        unsubscribe<TagTreeRebuildRequest> {  }
    }



    override val root = vbox {
        setPrefSize(600.0, 700.0)
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
        addClass(Styles.headerBluePadding)
        add(TagOrganiserHeader::class)
        splitpane {
        vgrow = Priority.ALWAYS
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
                add(find(TagFlow::class, Scope(projectViewModel, tagOrganiserViewModel, tagFlowViewModel, tagTree)))
            }
        }
    }
}