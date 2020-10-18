package org.ksoftware.lorebook.attributes

import javafx.beans.property.StringProperty

/**
 * This interface is for classes that should be uniquely identified by a String, normally a UUID.
 */
interface Id {
    val idProperty: StringProperty
}