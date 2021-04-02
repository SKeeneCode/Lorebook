package org.ksoftware.lorebook.utilities

import javafx.scene.layout.Region

fun Region.forceSize(width: Double, height: Double) {
    minWidth = Region.USE_PREF_SIZE
    prefWidth = width
    maxWidth = Region.USE_PREF_SIZE
    minHeight = Region.USE_PREF_SIZE
    prefHeight = height
    maxHeight = Region.USE_PREF_SIZE
}
