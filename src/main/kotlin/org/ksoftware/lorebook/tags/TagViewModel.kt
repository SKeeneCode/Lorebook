package org.ksoftware.lorebook.tags

import tornadofx.*

class TagViewModel(model: TagModel = TagModel()) : ItemViewModel<TagModel>() {
    init {
        item = model
    }

    val name = bind(TagModel::nameProperty, autocommit = true)
    val color = bind(TagModel::colorProperty, autocommit = true)
}