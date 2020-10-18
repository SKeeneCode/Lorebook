package org.ksoftware.lorebook.io

import javafx.beans.property.StringProperty
import java.io.File

/**
 * Interface for model classes that should save to the file system.
 */
interface Savable {
    suspend fun save(projectFolder: File, taskMessage: StringProperty)
}