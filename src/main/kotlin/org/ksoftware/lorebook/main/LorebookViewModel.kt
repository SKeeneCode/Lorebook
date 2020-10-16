package org.ksoftware.lorebook.main

import tornadofx.*

/**
 * View model for a LorebookModel. When instantiated will bind to the provided LorebookModel or create a new
 * one if one isn't provided.
 */
class LorebookViewModel(model: LorebookModel = LorebookModel()) : ItemViewModel<LorebookModel>() {

    init {
        item = model
    }

}