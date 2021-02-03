package org.ksoftware.lorebook.tagflow

import org.ksoftware.lorebook.tags.TagFunction
import org.ksoftware.lorebook.tags.TagModel
import tornadofx.*

class TagFlowViewModel : ViewModel() {
    var draggedTag: TagModel? = null
    var deleteFunction = TagFunction { false }
}