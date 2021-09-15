package org.ksoftware.lorebook.main

import com.squareup.moshi.JsonClass
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.*
import org.ksoftware.lorebook.attributes.Id
import org.ksoftware.lorebook.io.IOController
import org.ksoftware.lorebook.io.Savable
import org.ksoftware.lorebook.pages.PageModel
import org.ksoftware.lorebook.timeline.CalendarModal
import org.ksoftware.lorebook.timeline.EraModal
import java.io.File
import java.util.*

/**
 * Model class for a project. Properties in this class should (almost) never be bound to the user interface.
 * Instead use a ProjectViewModel.
 */
@JsonClass(generateAdapter = true)
data class ProjectModel(override val idProperty: StringProperty = SimpleStringProperty(UUID.randomUUID().toString())) :
    Savable, Id {

    // list of all page models in this project
    val pages: ObservableList<PageModel> = FXCollections.observableArrayList()

    val calendars = SimpleListProperty(FXCollections.observableArrayList<CalendarModal>())

    /**
     * Launches a coroutine for each page to save itself in the project folder.
     */
    override suspend fun save(projectFolder: File, ioController: IOController) {
        pages.map { page ->
            CoroutineScope(Dispatchers.IO).launch {
                launch { page.save(projectFolder, ioController) }
            }
        }
    }


}