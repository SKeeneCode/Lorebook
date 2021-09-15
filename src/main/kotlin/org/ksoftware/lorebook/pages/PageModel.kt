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
import org.ksoftware.lorebook.io.IOController
import org.ksoftware.lorebook.tags.TagModel
import tornadofx.toProperty
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

/**
 * Model class for a page. Properties in this class should (almost) never be bound to the user interface.
 * Instead use a PageViewModel.
 */
@JsonClass(generateAdapter = true)
data class PageModel(
        @Json(name = "id") override val idProperty: StringProperty = UUID.randomUUID().toString().toProperty(),
        val pageName: StringProperty = SimpleStringProperty(idProperty.value.substring(0, 8)),
        @Transient val tagSet: ObservableSet<TagModel> = FXCollections.observableSet(),
        @Transient var modified: Boolean = false
) : JsonModel(), Savable, Id {

    override suspend fun save(projectFolder: File, ioController: IOController) {
        val pageFolder = File(projectFolder.toString() + "/data/pages/" + idProperty.get())
        pageFolder.mkdirs()
        val pageFile = File(pageFolder.path + "/layout.txt")
        val bw = BufferedWriter(FileWriter(pageFile))
        val json = ioController.moshi.adapter(PageModel::class.java).indent("    ").toJson(this)
        bw.write(json)
        bw.close()
        modified = false
    }

    companion object {
         fun loadProjectPages(projectFolder: File, ioController: IOController) : Set<PageModel> {
            val pages = mutableSetOf<PageModel>()
            val pageFolder = File("$projectFolder/data/pages/")
            val pageFolders = pageFolder.listFiles() ?: return pages
            for (file in pageFolders) {
                if (!file.isDirectory) continue
                val pageLayout = File(file.path + "/layout.txt")
                val json = Files.readString(Path.of(pageLayout.path));
                val pageModel = ioController.moshi.adapter(PageModel::class.java).indent("    ").fromJson(json)
                if (pageModel is PageModel) pages += pageModel
            }
            return pages
        }
    }

}