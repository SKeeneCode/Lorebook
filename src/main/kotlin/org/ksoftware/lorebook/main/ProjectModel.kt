package org.ksoftware.lorebook.main

import com.squareup.moshi.JsonClass
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.ksoftware.lorebook.attributes.Id
import org.ksoftware.lorebook.io.Savable
import org.ksoftware.lorebook.pages.PageModel
import java.io.File
import java.util.*

/**
 * Model class for a project. Properties in this class should (almost) never be bound to the user interface.
 * Instead use a ProjectViewModel.
 */
@JsonClass(generateAdapter = true)
data class ProjectModel(override val idProperty: StringProperty = SimpleStringProperty(UUID.randomUUID().toString())) : Savable, Id {

    // list of all page models in this project
    val pages: ObservableList<PageModel> = FXCollections.observableArrayList()

    /**
     * Launches a coroutine for each page to save itself in the project folder.
     */
    override suspend fun save(projectFolder: File, taskMessage: StringProperty) {
        taskMessage.value = "Saving Project"
        val jobs: List<Job> = pages.map { page ->
            coroutineScope {
                launch {
                    page.save(projectFolder, taskMessage)
                }
            }
        }
        jobs.joinAll() // wait until all jobs are finished
        taskMessage.value = "Finished Saving"
    }
}