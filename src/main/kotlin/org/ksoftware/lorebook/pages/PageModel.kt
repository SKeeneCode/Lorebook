package org.ksoftware.lorebook.pages

import javafx.beans.property.StringProperty
import tornadofx.*
import java.util.*

/**
 * Model class for a page. Properties in this class should (almost) never be bound to the user interface.
 * Instead use a PageViewModel.
 */
data class PageModel(
    val idProperty: StringProperty = UUID.randomUUID().toString().toProperty()
)