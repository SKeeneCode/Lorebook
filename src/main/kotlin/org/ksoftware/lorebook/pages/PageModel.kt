package org.ksoftware.lorebook.pages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableSet
import org.ksoftware.lorebook.attributes.Id
import org.ksoftware.lorebook.attributes.JsonModel
import org.ksoftware.lorebook.io.Savable
import org.ksoftware.lorebook.tags.TagModel
import tornadofx.toProperty
import java.io.File
import java.util.*

/**
 * Model class for a page. Properties in this class should (almost) never be bound to the user interface.
 * Instead use a PageViewModel.
 */
@JsonClass(generateAdapter = true)
data class PageModel(
        @Json(name = "id") override val idProperty: StringProperty = UUID.randomUUID().toString().toProperty(),
        val pageName: StringProperty = SimpleStringProperty("myPage"),
        val tagSet: ObservableSet<TagModel> = FXCollections.observableSet(),
        // flag to indicate if this model has had any commits since it was last saved
        @Transient var modified: Boolean = false
) : JsonModel(), Savable, Id {

    fun getJson() {
        println(moshi.adapter(PageModel::class.java).toJson(this))
    }

    override suspend fun save(projectFolder: File) {
        val pageFolder = File(projectFolder.toString() + "/data/pages/" + idProperty.get())
        pageFolder.mkdirs()
        modified = false
    }
}