package org.ksoftware.lorebook.pages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import javafx.beans.property.StringProperty
import org.ksoftware.lorebook.adapters.StringPropertyAdapter
import org.ksoftware.lorebook.io.Savable
import tornadofx.*
import java.io.File
import java.util.*

/**
 * Model class for a page. Properties in this class should (almost) never be bound to the user interface.
 * Instead use a PageViewModel.
 */
@JsonClass(generateAdapter = true)
data class PageModel(
    @Json(name = "id") val idProperty: StringProperty = UUID.randomUUID().toString().toProperty(),
    // flag to indicate if this model has had any commits since it was last saved
    @Transient var modified: Boolean = false
) : Savable {

    fun getJson() {
        val builder = Moshi.Builder()
                .add(StringPropertyAdapter()).build()
        val adapter = builder.adapter(PageModel::class.java)
        val json = adapter.toJson(this)
        println(json)
    }

    override suspend fun save(projectFolder: File, taskMessage: StringProperty) {
        val pageFolder = File(projectFolder.toString() + "/data/pages/" + idProperty.get())
        pageFolder.mkdirs()
        modified = false
    }
}