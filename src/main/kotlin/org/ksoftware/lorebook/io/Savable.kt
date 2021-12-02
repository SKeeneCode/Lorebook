package org.ksoftware.lorebook.io

import java.io.File

/**
 * Interface for model classes that should save to the file system.
 */
interface Savable {
    suspend fun save(projectFolder: File, ioController: IOController)
}