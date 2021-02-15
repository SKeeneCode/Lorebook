package org.ksoftware.lorebook.organiser.tagtreeview

import org.ksoftware.lorebook.tags.TagFunction
import org.ksoftware.lorebook.tags.TagModel
import tornadofx.UIComponent
import tornadofx.ViewModel

class TagTreeViewModel : ViewModel() {
    val treeCells = mutableListOf<UIComponent>()
    var draggedTag: TagModel? = null
    var deleteFunction = TagFunction { false }
    var addFunction = TagFunction { false }
    var root = TagModel()
}