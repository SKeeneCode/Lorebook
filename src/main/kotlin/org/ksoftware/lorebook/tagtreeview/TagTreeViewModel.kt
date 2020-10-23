package org.ksoftware.lorebook.tagtreeview

import org.ksoftware.lorebook.tags.TagFunction
import org.ksoftware.lorebook.tags.TagModel
import tornadofx.*

class TagTreeViewModel : ViewModel() {
    val treeCells = mutableListOf<UIComponent>()
    var draggedTag: TagModel? = null
    var deleteFunction = TagFunction { false }
    var addFunction = TagFunction { false }
    var root = TagModel()
}