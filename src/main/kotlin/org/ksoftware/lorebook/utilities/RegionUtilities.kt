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

fun Region.forceHeight(height: Double) {
    prefHeight = height
    minHeight = Region.USE_PREF_SIZE
    maxHeight = Region.USE_PREF_SIZE
}
