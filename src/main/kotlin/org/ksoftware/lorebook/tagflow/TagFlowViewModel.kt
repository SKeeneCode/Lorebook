package org.ksoftware.lorebook.tagflow

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleSetProperty
import javafx.scene.Node
import javafx.scene.layout.Pane
import org.ksoftware.lorebook.tags.TagModel
import org.ksoftware.lorebook.tags.TagViewModel
import tornadofx.*

class TagFlowViewModel : ViewModel() {

    init {
        println(this)
    }

    val tagSet = SimpleSetProperty<TagModel>()
    var draggedTag: TagModel? = null
    val showDropHint = SimpleBooleanProperty(true)

    var tagToNodeConverter: (tag: TagModel) -> Node = {  Pane()  }
}