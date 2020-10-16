package org.ksoftware.lorebook.pages

import tornadofx.*

/**
 * View model class for a page. When instantiated will bind to the provided PageModel or create a new
 * one if one isn't provided.
 */
class PageViewModel(model: PageModel = PageModel()) : ItemViewModel<PageModel>() {

    init {
        item = model
    }

    val id = bind(PageModel::idProperty)

}