package org.ksoftware.lorebook.pages

import tornadofx.*
import java.util.*

/**
 * Model class for a page. Properties in this class should (almost) never be bound to the user interface.
 * Instead use a PageViewModel.
 */
class PageModel {

    val idProperty = UUID.randomUUID().toString().toProperty()
    val id by idProperty

}