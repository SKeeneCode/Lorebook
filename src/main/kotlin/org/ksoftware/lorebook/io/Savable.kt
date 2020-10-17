package org.ksoftware.lorebook.io

import javafx.beans.property.StringProperty
import java.io.File

interface Savable {
    suspend fun save(projectFolder: File, taskMessage: StringProperty)
}