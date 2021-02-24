package org.ksoftware.lorebook.pages

import javafx.beans.property.SimpleBooleanProperty
import tornadofx.ItemViewModel

/**
 * View model class for a page. When instantiated will bind to the provided PageModel or create a new
 * one if one isn't provided.
 */
class PageViewModel(model: PageModel = PageModel()) : ItemViewModel<PageModel>() {

    init {
        item = model
    }

    val id = bind(PageModel::idProperty)
    val tags = bind(PageModel::tagSet)
    val pageName = bind(PageModel::pageName)

    val gridMinorLinesVisible = SimpleBooleanProperty(true)
    val gridMajorLinesVisible = SimpleBooleanProperty(true)

    override fun onCommit() {
        item.modified = true
    }
}