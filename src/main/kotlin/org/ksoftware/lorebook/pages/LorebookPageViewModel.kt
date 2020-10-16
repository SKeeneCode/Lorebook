package org.ksoftware.lorebook.pages

import tornadofx.*

/**
 * View model class for a lorebook page. When instantiated will bind to the provided LorebookPageModel or create a new
 * one if one isn't provided.
 */
class LorebookPageViewModel(model: LorebookPageModel = LorebookPageModel()) : ItemViewModel<LorebookPageModel>() {

    init {
        item = model
    }

}