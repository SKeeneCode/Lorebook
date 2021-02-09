package org.ksoftware.lorebook.organiser.tagflow

import javafx.scene.Node
import org.ksoftware.lorebook.main.ProjectViewModel
import org.ksoftware.lorebook.pages.PageViewModel
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.tags.TagViewModel
import tornadofx.*

class TagFlowController : Controller() {

    private val tagFlowViewModel: TagFlowViewModel by inject()
    private val projectViewModel: ProjectViewModel by inject()
    private val pageViewModel: PageViewModel by inject()

    val tagToNodeConvertor: (tag: TagModel) -> Node = { tag ->
         find(TagFlowCell::class, Scope(tagFlowViewModel, pageViewModel, projectViewModel, TagViewModel(tag))).root
    }


}