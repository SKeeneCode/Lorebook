package org.ksoftware.lorebook.attributes

import javafx.beans.property.ObjectProperty
import javafx.scene.paint.Color

/**
 * Interface for models that should contain a Color (such as page tags)
 */
interface Colored {
    val colorProperty: ObjectProperty<Color>
}