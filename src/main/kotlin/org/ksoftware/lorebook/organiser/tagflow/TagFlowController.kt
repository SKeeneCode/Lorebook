package org.ksoftware.lorebook.organiser.tagflow

import javafx.scene.Node
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.tags.TagViewModel
import tornadofx.Controller
import tornadofx.Scope
import tornadofx.find

/**
 * A controller for a TagFlow. Contains the converter dictating how a tag should be displayed as a node in a TagFlow.
 */
class TagFlowController : Controller() {

    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val projectViewModel: ProjectViewModel by inject()
    private val pageViewModel: PageViewModel by inject()

    val tagToNodeConvertor: (tag: TagModel) -> Node = { tag ->
         find(TagFlowCell::class, Scope(tagFlowViewModel, pageViewModel, projectViewModel, TagViewModel(tag))).root
    }


}