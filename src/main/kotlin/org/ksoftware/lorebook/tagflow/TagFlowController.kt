package org.ksoftware.lorebook.tagflow

import javafx.scene.Node
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.tags.TagViewModel
import tornadofx.*

class TagFlowController : Controller() {

    private val tagFlowViewModel: TagFlowViewModel by inject()

    val tagToNodeConvertor: (tag: TagModel) -> Node = { tag ->
        find(TagFlowCell::class, Scope(tagFlowViewModel, TagViewModel(tag))).root
    }


}