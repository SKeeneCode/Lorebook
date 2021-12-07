package org.ksoftware.lorebook.main

import com.squareup.moshi.JsonClass
import javafx.beans.property.*
import javafx.collections.FXCollections
import kotlinx.coroutines.*
import org.ksoftware.lorebook.io.IOController
import org.ksoftware.lorebook.io.Savable
import org.ksoftware.lorebook.navigator.BookmarkTreeNode
import org.ksoftware.lorebook.pages.PageModel
import org.ksoftware.lorebook.timeline.CalendarModal
import org.ksoftware.lorebook.utilities.Id
import java.io.File
import java.util.*

/**
 * Model class for a project. Properties in this class should (almost) never be bound to the user interface.
 * Instead use a ProjectViewModel.
 */
@JsonClass(generateAdapter = true)
data class ProjectModel(val idProperty: StringProperty = SimpleStringProperty(UUID.randomUUID().toString())) :
    Savable {

    // map of all page models in this project
    val pages = SimpleMapProperty(FXCollections.observableHashMap<Id, PageModel>())

    val calendars = SimpleListProperty(FXCollections.observableArrayList<CalendarModal>())

    val bookmarks = SimpleObjectProperty(BookmarkTreeNode())

    /**
     * Launches a coroutine for each page to save itself in the project folder.
     */
    override suspend fun save(projectFolder: File, ioController: IOController) {
        pages.values.map { page ->
            CoroutineScope(Dispatchers.IO).launch {
                launch { page.save(projectFolder, ioController) }
            }
        }
    }


}