package org.ksoftware.lorebook.attributes

import javafx.beans.property.StringProperty

/**
 * For models that have a name
 */
interface Named {
    val nameProperty: StringProperty
}